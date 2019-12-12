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

import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogProcessUtil {

    private static LogProcessUtil logProcessUtil;

    private LogProcessUtil() {

    }

    static {
        logProcessUtil = new LogProcessUtil();
    }

    public static LogProcessUtil getInstance() {
        return logProcessUtil;
    }

    public void writeCrash(String crashLog) {
        try {

            Context context = TeleMeshApplication.getContext();

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                    context.getString(R.string.app_name));

            if(!file.exists()){
                file.mkdirs();
            }

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


    private String getCrashLogDateWise(String crashLog) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm:ss dd-MMM-yy", Locale.getDefault());
        Date date = new Date();
        String currentTime = dateFormat.format(date);
        String dashes = "-------------------------------";

        return dashes + " " + currentTime + " " + dashes + "\n\n" + crashLog;
    }

}