package com.w3engineers.unicef.telemesh.data.helper.constants;

import android.os.Build;

public class Constants {

    public static boolean IS_LOADING_ENABLE = false;
    public static boolean IsMeshInit;
    public static boolean IS_LOG_UPLOADING_START = false;

    public interface DefaultValue {
        int NEG_INTEGER_ONE = -1;
        int DELAY_INTERVAL = 3000;
        int DOUBLE_PRESS_INTERVAL = 2000;
        int MINIMUM_TEXT_LIMIT = 2;
        int MINIMUM_INFO_LIMIT = 3;
        int MAXIMUM_TEXT_LIMIT = 20;
        int INTEGER_VALUE_ZERO = 0;
        int MAXIMUM_BADGE_VALUE = 99;
    }

    public interface preferenceKey {
        String USER_NAME = "first_name";
        String IMAGE_INDEX = "image_index";
        String MY_USER_ID = "my_user_id";
        String IS_USER_REGISTERED = "user_registered";
        String IS_NOTIFICATION_ENABLED = "notification_enable";
        String APP_LANGUAGE = "app_language";
        String APP_LANGUAGE_DISPLAY = "app_language_display";
        String COMPANY_NAME = "company_name";
        String COMPANY_ID = "company_id";
        String MY_REGISTRATION_TIME = "registration_time";
        String MY_SYNC_IS_DONE = "my_sync_is_done";
    }

    public interface MenuItemPosition {
        int POSITION_FOR_CONTACT = 0;
        int POSITION_FOR_MESSAGE_FEED = 1;
        int POSITION_FOR_MESSAGE_SETTINGS = 2;
    }

    public interface drawables {
        String AVATAR_IMAGE = "avatar";
        String AVATAR_DRAWABLE_DIRECTORY = "mipmap";
    }

    public interface MessageStatus {
        int STATUS_UNREAD = 1;
        int STATUS_READ = 2;
        int STATUS_SENDING = 3;
        int STATUS_DELIVERED = 4;
        int STATUS_FAILED = 5;
    }

    public interface DataType {
        //RM data type
        // Restricted for 1 and 3 for type
        byte MESSAGE = 0x2;
        byte MESSAGE_COUNT = 0x5;
        byte MESSAGE_FEED = 0x4;
        byte APP_SHARE_COUNT = 0x6;
    }

    public interface MessageType {
        int TEXT_MESSAGE = 100;
        int DATE_MESSAGE = 101;
        int MESSAGE_INCOMING = 1;
        int MESSAGE_OUTGOING = 0;
    }

    public interface UserStatus {
        int OFFLINE = 0;
        int WIFI_ONLINE = 5;
        int WIFI_MESH_ONLINE = 4;
        int BLE_MESH_ONLINE = 3;
        int BLE_ONLINE = 2;
        int INTERNET_ONLINE = 1;
    }

    public interface AppConstant {
        long LOADING_TIME = 30 * 1000;
        int MESSAGE_SYNC_PLOT = 10;
        int DEFAULT = 0;
        long LOADING_TIME_SHORT = 1000;
        String LOG_FOLDER = ".log";
        String INFO_LOG_FILE = "InfoLog.txt";
        String CRASH_REPORT_FILE_NAME = "Crashes.txt";
        String DASHES = "-------------------------------";
    }

    public interface Bulletin {
        int DEFAULT = 0;
        int BULLETIN_SEND = 1;
        int BULLETIN_RECEIVED = 2;
        int BULLETIN_SEND_TO_SERVER = 3;

        int MINE = 1;
        int OTHERS = 0;
    }

    public interface AnalyticsResponseType {
        byte MESSAGE_COUNT = 1;
        byte NEW_USER_COUNT = 2;
        byte APP_SHARE_COUNT = 3;
    }

    public interface UpdatedData {
        int YES = 0;
        int NO = 1;
    }

    public interface MeshLogType {
        int SPECIAL = 1;
        int WARNING = 2;
        int INFO = 3;
        int ERROR = 4;
        int DATE = 5;
    }

    /**
     * Purpose: Get device name.
     *
     * @return string type device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
