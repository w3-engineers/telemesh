package com.w3engineers.unicef.telemesh.data.broadcast;

import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    /**
     * Any BlockingQueue may be used to transfer and hold submitted tasks.
     */
    private final BlockingQueue<Runnable> mTaskQueue;

    /**
     * New threads are created using a ThreadFactory
     */
    private BackgroundThreadFactory backgroundThreadFactory;


    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;


    //The reference is later used to communicate with the UI thread
    private WeakReference<UiThreadCallback> uiThreadCallbackWeakReference;

    //an interface which extends Executor interface. It is used to manage threads in the threads pool.
    private final ExecutorService mExecutorService;


    private List<Future> mRunningTaskList;


    private static BroadcastManager mBroadcastManager = null;


    // The class is used as a singleton
    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        mBroadcastManager = new BroadcastManager();
    }

    private BroadcastManager(){

        // initialize a queue for the thread pool. New tasks will be added to this queue
        mTaskQueue = new LinkedBlockingQueue<Runnable>();
        mRunningTaskList = new ArrayList<>();
        backgroundThreadFactory = new BackgroundThreadFactory();

        mExecutorService = new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES*2,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mTaskQueue,
                backgroundThreadFactory);
    }

    public static BroadcastManager getInstance(){
        return mBroadcastManager;
    }

    /**
     * New threads are created using a ThreadFactory
     */
    private static class BackgroundThreadFactory implements ThreadFactory {

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
                    Log.e("TPE", thread.getName() + " encountered an error: " + ex.getMessage());
                }
            });
            return thread;
        }
    }


    // Add a callable to the queue, which will be executed by the next available thread in the pool
    public void addBroadCastMessage(Callable callable){
        Future future = mExecutorService.submit(callable);
        mRunningTaskList.add(future);
    }

    /* Remove all tasks in the queue and stop all running threads
     * Notify UI thread about the cancellation
     */
    /*public void cancelAllTasks() {
        synchronized (this) {
            mTaskQueue.clear();
            for (Future task : mRunningTaskList) {
                if (!task.isDone()) {
                    task.cancel(true);
                }
            }
            mRunningTaskList.clear();
        }
        sendMessageToUiThread(Util.createMessage(Util.MESSAGE_ID, "All tasks in the thread pool are cancelled"));
    }*/

    // Keep a weak reference to the UI thread, so we can send messages to the UI thread
    public void setUiThreadCallback(UiThreadCallback uiThreadCallback) {
        this.uiThreadCallbackWeakReference = new WeakReference<UiThreadCallback>(uiThreadCallback);
    }

    // Pass the message to the UI thread
    public void sendMessageToUiThread(Message message){
        if(uiThreadCallbackWeakReference != null && uiThreadCallbackWeakReference.get() != null) {
            uiThreadCallbackWeakReference.get().publishToUiThread(message);
        }
    }


}
