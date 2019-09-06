
#include <jni.h>

extern "C" {


    JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_telemesh_data_helper_RmDataHelper_getBroadCastUrl(JNIEnv *env, jobject instance){

        return env-> NewStringUTF("wss://telemesh.w3engineers.com/websocket/");
    }

    JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_telemesh_data_helper_RmDataHelper_getBroadcastToken(JNIEnv *env, jobject instance){

        return env-> NewStringUTF("ta8R9JY4Rf2gHA9YNHKg85eUK");
    }

    JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_TeleMeshApplication_getParseAppId(JNIEnv *env, jobject instance){

        return env-> NewStringUTF("2zSM6$5UjnMK9ANE6qkGR&T#mWngjr!@2RFeGG5H");
    }


    JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_TeleMeshApplication_getParseUrl(JNIEnv *env, jobject instance){

        return env-> NewStringUTF("https://telemesh.w3engineers.com/parse");
    }
}

