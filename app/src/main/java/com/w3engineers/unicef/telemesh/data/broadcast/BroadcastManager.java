package com.w3engineers.unicef.telemesh.data.broadcast;

import android.os.Process;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.w3engineers.unicef.telemesh.data.helper.ContentModel;
import com.w3engineers.unicef.telemesh.data.helper.DataModel;
import com.w3engineers.unicef.util.helper.model.ViperContentData;
import com.w3engineers.unicef.util.helper.model.ViperData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class BroadcastManager {

    /**
     * One thing the thread pool framework does not handle is the Android
     * activity lifecycle. If you want your thread pool to survive the
     * activity lifecycle and reconnect to your activity after it is re-created
     * (E.g. after an orientation change), it needs to be created and maintained outside the activity.
     */

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    /**
     * If the pool currently has more than corePoolSize threads,
     * excess threads will be terminated if they have been idle for more than the keepAliveTime.
     * This provides a means of reducing resource consumption when the pool is not being actively used.
     */
    private static final int KEEP_ALIVE_TIME = 1;


    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    //an interface which extends Executor interface. It is used to manage threads in the threads pool.
    private final ExecutorService mExecutorService;


    private List<Future> mRunningTaskList;


    private static BroadcastManager mBroadcastManager = null;

    private BroadcastSendCallback broadcastSendCallback;

    public interface BroadcastSendCallback {
        void dataSent(@NonNull DataModel rmDataModel, String dataSendId);
        void contentSent(ContentModel contentModel, String dataSendId);
        void onGroupContentSend(ContentModel contentModel, String result);
    }

    public void setBroadcastSendCallback(@Nullable BroadcastSendCallback broadcastSendCallback) {
        this.broadcastSendCallback = broadcastSendCallback;
    }

    // The class is used as a singleton
    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        mBroadcastManager = new BroadcastManager();
    }

    private BroadcastManager(){

        // initialize a queue for the thread pool. New tasks will be added to this queue
        /**
         * Any BlockingQueue may be used to transfer and hold submitted tasks.
         */
        BlockingQueue<Runnable> mTaskQueue = new LinkedBlockingQueue<Runnable>();
        mRunningTaskList = new ArrayList<>();
        /**
         * New threads are created using a ThreadFactory
         */
        BackgroundThreadFactory backgroundThreadFactory = new BackgroundThreadFactory();

        mExecutorService = new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES*2,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mTaskQueue,
                backgroundThreadFactory);
    }

    @Nullable
    public static BroadcastManager getInstance(){
        return mBroadcastManager;
    }

    /**
     * New threads are created using a ThreadFactory
     */
    protected static class BackgroundThreadFactory implements ThreadFactory {

        private static int sTag = 1;

        @Override
        public Thread newThread(@NonNull Runnable runnable) {

            Thread thread = new Thread(runnable);
            thread.setName("CustomThread" + sTag);
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

            // A exception handler is created to log the exception from threads
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Timber.tag("TPE").e(thread.getName() + " encountered an error: " + ex.getMessage());
                }
            });
            return thread;
        }
    }


    // Add a callable to the queue, which will be executed by the next available thread in the pool
    public void addBroadCastMessage(@NonNull SendDataTask sendDataTask){
        Future future = mExecutorService.submit(sendDataTask);
        mRunningTaskList.add(future);

        try {

            String result = (String) future.get();
            if (broadcastSendCallback != null) {
                Timber.v("Group Message Test", "content sent from library %s", result);

                ViperData viperData = sendDataTask.getViperData();
                ViperContentData viperContentData = sendDataTask.getViperContentData();

                if (viperData != null) {
                    DataModel rmDataModel = new DataModel()
                            .setUserId(sendDataTask.getPeerId())
                            .setRawData(viperData.rawData)
                            .setDataType(viperData.dataType);
                    broadcastSendCallback.dataSent(rmDataModel, result);
                }

                if (viperContentData != null) {
                    if(viperContentData.contentModel.isGroupContent()){
                        broadcastSendCallback.onGroupContentSend(viperContentData.contentModel, result);
                    }else {
                        broadcastSendCallback.contentSent(viperContentData.contentModel, result);
                    }
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
