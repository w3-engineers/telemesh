package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.w3engineers.mesh.util.MeshLog;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.ui.showlog.MeshLogModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;

public class LogProcessUtil {

    private static LogProcessUtil logProcessUtil;
    private BehaviorSubject<List<MeshLogModel>> listBehaviorSubject = BehaviorSubject.create();
    private BehaviorSubject<MeshLogModel> behaviorMeshLogModel = BehaviorSubject.create();
    private String fullContent;
    private ArrayList<String> pendingLogs = new ArrayList<>();

    private LogProcessUtil() {

    }

    static {
        logProcessUtil = new LogProcessUtil();
    }

    public static LogProcessUtil getInstance() {
        return logProcessUtil;
    }

    public void writeLog(String log) {
        saveLogInSd(log);
    }

    public void readLog() {
        Thread thread = new Thread(this::retrieveLogFromSd);
        thread.setDaemon(true);
        thread.start();
    }

    public void loadAllLogs() {
        Thread thread = new Thread(this::retrieveLogForFirstTime);
        thread.setDaemon(true);
        thread.start();
    }

    public Flowable<List<MeshLogModel>> getAllMeshLog() {
        return listBehaviorSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public Flowable<MeshLogModel> getMeshLog() {
        return behaviorMeshLogModel.toFlowable(BackpressureStrategy.LATEST);
    }

    public void writeCrash(String crashLog) {
        try {

            Context context = TeleMeshApplication.getContext();

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                    context.getString(R.string.app_name));
            file.mkdirs();

            File directory = new File(file, Constants.AppConstant.LOG_FOLDER);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File crashFile = new File(directory, Constants.AppConstant.CRASH_REPORT_FILE_NAME);

            if (crashFile.exists()) {

                FileInputStream fileInputStream = new FileInputStream(crashFile);
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(fileInputStream));

                String aDataRow = "", aBuffer = "";
                while ((aDataRow = bufferedReader.readLine()) != null) {
                    aBuffer += aDataRow + "\n";
                }

                aBuffer = aBuffer + "\n" + getCrashLogDateWise(crashLog);
                bufferedReader.close();

                FileOutputStream fileOutputStream = new FileOutputStream(crashFile);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                outputStreamWriter.append(aBuffer);
                outputStreamWriter.close();
                fileOutputStream.close();

            } else {

                crashFile.createNewFile();

                FileOutputStream fileOutputStream = new FileOutputStream(crashFile);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                outputStreamWriter.append(getCrashLogDateWise(crashLog));
                outputStreamWriter.close();
                fileOutputStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLogInSd(String log) {

        try {
            Context context = TeleMeshApplication.getContext();

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                    context.getString(R.string.app_name));
            file.mkdirs();

            File directory = new File(file, Constants.AppConstant.LOG_FOLDER);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File crashFile = new File(directory, Constants.AppConstant.INFO_LOG_FILE);

            if (crashFile.exists()) {

                if (TextUtils.isEmpty(fullContent)) {

                    pendingLogs.add(log);

                } else {

                    String logData = getLogDateWise(fullContent, log);

                    sendCurrentLog(log, logData.contains(Constants.AppConstant.DASHES));

                    fullContent = fullContent + (logData.contains(Constants.AppConstant.DASHES) ? "\n" : "") + logData;

                    FileOutputStream fileOutputStream = new FileOutputStream(crashFile);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                    outputStreamWriter.append(fullContent);
                    outputStreamWriter.close();
                    fileOutputStream.close();

                }

            } else {

                crashFile.createNewFile();

                FileOutputStream fileOutputStream = new FileOutputStream(crashFile);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                sendCurrentLog(log, true);
                outputStreamWriter.append(getLogDateWise(null, log));
                outputStreamWriter.close();
                fileOutputStream.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveLogFromSd() {

        try {
            Context context = TeleMeshApplication.getContext();

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                    context.getString(R.string.app_name));
            file.mkdirs();

            File directory = new File(file, Constants.AppConstant.LOG_FOLDER);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File crashFile = new File(directory, Constants.AppConstant.INFO_LOG_FILE);

            if (crashFile.exists()) {

                FileInputStream fileInputStream = new FileInputStream(crashFile);
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(fileInputStream));

                List<MeshLogModel> meshLogModels = new ArrayList<>();

                String aDataRow = "";
                while ((aDataRow = bufferedReader.readLine()) != null) {
                    if (!TextUtils.isEmpty(aDataRow)) {

                        int logType = 0;
                        if (aDataRow.contains(Constants.AppConstant.DASHES)) {
                            aDataRow = aDataRow.replaceAll(Constants.AppConstant.DASHES, "");
                            logType = Constants.MeshLogType.DATE;
                        }

                        aDataRow = aDataRow.trim();
                        if (!TextUtils.isEmpty(aDataRow)) {

                            if (logType != Constants.MeshLogType.DATE)
                                logType = getTypeByLog(aDataRow);

                            MeshLogModel meshLogModel = new MeshLogModel(logType, aDataRow);
                            meshLogModels.add(meshLogModel);
                        }
                    }
                }

                if (meshLogModels.size() > 0) {
                    listBehaviorSubject.onNext(meshLogModels);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveLogForFirstTime() {
        try {
            if (TextUtils.isEmpty(fullContent)) {
                Context context = TeleMeshApplication.getContext();

                File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                        context.getString(R.string.app_name));
                file.mkdirs();

                File directory = new File(file, Constants.AppConstant.LOG_FOLDER);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File crashFile = new File(directory, Constants.AppConstant.INFO_LOG_FILE);

                if (crashFile.exists()) {

                    FileInputStream fileInputStream = new FileInputStream(crashFile);
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(fileInputStream));

                    String aDataRow = "", aBuffer = "";
                    while ((aDataRow = bufferedReader.readLine()) != null) {
                        aBuffer += aDataRow + "\n";
                    }

                    fullContent = aBuffer;

                    if (pendingLogs.size() > 0) {

                        for (String log : pendingLogs) {

                            String logData = getLogDateWise(fullContent, log);

                            fullContent = fullContent + (logData.contains(Constants.AppConstant.DASHES) ? "\n" : "") + logData + "\n";
                        }

                        FileOutputStream fileOutputStream = new FileOutputStream(crashFile);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                        outputStreamWriter.append(fullContent);
                        outputStreamWriter.close();
                        fileOutputStream.close();

                        pendingLogs.clear();

                    } else {
                        pendingLogs.clear();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCurrentLog(String log, boolean isAddedDate) {
        if (isAddedDate) {
            String date = getTodayDate();
            behaviorMeshLogModel.onNext(new MeshLogModel(Constants.MeshLogType.DATE, date));
        }

        behaviorMeshLogModel.onNext(new MeshLogModel(getTypeByLog(log), log));
    }

    private int getTypeByLog(String msg) {
        if (msg.startsWith(MeshLog.INFO)) {
            return Constants.MeshLogType.INFO;
        } else if (msg.startsWith(MeshLog.WARNING)) {
            return Constants.MeshLogType.WARNING;
        } else if (msg.startsWith(MeshLog.ERROR)) {
            return Constants.MeshLogType.ERROR;
        } else {
            return Constants.MeshLogType.SPECIAL;
        }
    }

    private String getLogDateWise(String fullText, String log) {
        String currentTime = getTodayDate();

        if (!TextUtils.isEmpty(fullText) && fullText.contains(currentTime)) {
            return log; // If date exist then send only the log data
        }

        return Constants.AppConstant.DASHES + " " + currentTime + " " + Constants.AppConstant.DASHES + "\n\n" + log;
    }

    private String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getCrashLogDateWise(String crashLog) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm:ss dd-MMM-yy");
        Date date = new Date();
        String currentTime = dateFormat.format(date);
        String dashes = "-------------------------------";

        return dashes + " " + currentTime + " " + dashes + "\n\n" + crashLog;
    }

}