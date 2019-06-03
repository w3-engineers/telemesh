package com.w3engineers.unicef.telemesh.data.helper.constants;

public class Constants {

    public static boolean IS_LOADING_ENABLE = false;

    //bottom navigation item position
    /*public interface MenuItemPosition {
        int POSITION_FOR_CONTACT = 0;
        int POSITION_FOR_MESSAGE_FEED = 1;
        int POSITION_FOR_SURVEY = 2;
        int POSITION_FOR_SETTINGS = 3;
        float MENU_ITEM_WIDTH = 50f;
        float MENU_ITEM_HEIGHT = 50f;
        int MAXIMUM_MENU_ITEMS = 4;
    }*/

    public interface DefaultValue {
//        int INTEGER_VALUE_ZERO = 0;
//        int MAXIMUM_BADGE_VALUE = 9;
        int NEG_INTEGER_ONE = -1;
        int DELAY_INTERVAL = 3000;
        int DOUBLE_PRESS_INTERVAL = 2000;
        int MINIMUM_TEXT_LIMIT = 2;
        int MINIMUM_INFO_LIMIT = 3;
        int MAXIMUM_TEXT_LIMIT = 20;
    }

    public interface ButtonOpacity {
        float DISABLE_EFFECT = 0.5f;
        float ENABLE_EFFECT = 1.0f;
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
        byte USER = 0x1;
        byte MESSAGE = 0x2;
        byte SURVEY = 0x3;
        byte MESSAGE_FEED = 0x4;
        byte BROADCAST_MESSAGE = 0x5;
    }

    public interface MessageType {
        int TEXT_MESSAGE = 100;
        int DATE_MESSAGE = 101;
    }

    public interface UserStatus {
        int OFFLINE = 0;
        int ONLINE = 1;
    }

    public interface AppConstant {
        long LOADING_TIME = 30 * 1000;
        String BROADCAST_URL = "ws://telemesh.w3engineers.com/websocket";
    }

    public interface BuyerStatus {
        int DEFAULT = 0;
        int ACTIVE = 1;
        int IN_USE = 2;
    }
}
