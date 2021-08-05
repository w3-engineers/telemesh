package com.w3engineers.unicef.telemesh.data.broadcast;

/**
 * Created by Frank Tan on 10/04/2016.
 *
 * A helper class with static properties and methods
 */
public class Util {

    public static final String LOG_TAG = "BackgroundThread";
    public static final int MESSAGE_ID = 1;
    public static final String MESSAGE_BODY = "MESSAGE_BODY";
    public static final String EMPTY_MESSAGE = "<EMPTY_MESSAGE>";

    /*private static int jobId = 32;
    // schedule the start of the service every 10 - 30 seconds
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(@NonNull Context context) {

        ComponentName serviceComponent = new ComponentName(context, BulletinJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);
//        builder.setMinimumLatency(1000); // wait at least
        builder.setOverrideDeadline(TimeUnit.SECONDS.toMillis(10)); // maximum delay 2 miniute now statoc
//        builder.setOverrideDeadline(TimeUnit.HOURS.toMillis(24)); // maximum delay 24 hour
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresDeviceIdle(true); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }*/

    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void cancelJob(@NonNull Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);
    }*/

    /*public static boolean isJobExist(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> jobInfos = jobScheduler.getAllPendingJobs();
        for (JobInfo jobInfo : jobInfos) {
            if (jobInfo.getId() == jobId)
                return true;
        }
        return false;
    }*/

    public static String convertToTitleCaseIteratingChars(String text) {
        /*if (text != null && !text.isEmpty()) {
            StringBuilder converted = new StringBuilder();

            boolean convertNext = true;
            for (char ch : text.toCharArray()) {
                if (Character.isSpaceChar(ch)) {
                    convertNext = true;
                } else if (convertNext) {
                    ch = Character.toTitleCase(ch);
                    convertNext = false;
                } else {
                    ch = Character.toLowerCase(ch);
                }
                converted.append(ch);
            }

            text = converted.toString();
        }*/

        return text;
    }
}
