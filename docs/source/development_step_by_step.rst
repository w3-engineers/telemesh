.. _development_step_by_step:

Telemesh
--------

1. Find the ``versions.gradle`` in the Telemesh root directory of the repo and
   any new support library reference should be added here.

2. Any support library on app-level ``build.gradle`` should be added in
   this way

  ::

    implementation deps.support.app_compat
    implementation deps.support.design
    implementation deps.constraint_layout
    implementation deps.support.recyclerview
    implementation deps.support.cardview

3. ``Viper`` dependency should be added in the same way inside the app-level ``build.gradle``

4. The *Telemesh* app is using different ``URLs`` for different services. Preparing all those services is time-consuming as well as tedious too. But if you are a contributor to *Telemesh* app, you don't need to worry at all about those monotonous tasks. All the ``URLs`` and other permission related information are provided in a ``.so`` file as solid package to give you a staging environment the same as production for development purposes. Just download the project and run.

  But, we don't give the guarantee of continuous services for the staging environment, as it's only for development and testing purposes.

5. If you want to deploy your server machines and services you need to do a little more work. Assuming that you already have the services installed on the online machine(s), please follow the steps below.

  a. If android **NDK** and **CMake** are not installed on your development machine, please download and install them using **SDK manager**. For more help visit https://developer.android.com/studio/projects/install-ndk#default-version


  b. Create a directory named *cpp* under the *main* package of the *Telemesh* app.

        .. figure:: img/cpp_directory.png
           :scale: 100 %
           :alt: cpp directory

  c. Put/Create the following two files into the *cpp* folder.

      1. CMakeLists.txt
      2. native-lib.cpp


  d. ``CMakeLists.txt`` build script can be prepared from the following link: https://developer.android.com/studio/projects/configure-cmake#create_script

  If you need any help on any stage of work and you want to communicate with us please join our `Discord`_ channel.

  e. Any change or addition of credentials has to do under ``native-lib.cpp`` file. Copy the following code snippet and paste it into this file.

  ::

    #include <jni.h>
    #include <string>

    extern "C" JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_telemesh_data_helper_AppCredentials_getBroadCastToken(JNIEnv *env, jobject) {
      std::string broadcast_token= "Here have to set your broadcast token";
      return env->NewStringUTF(broadcast_token.c_str());
    }

    extern "C" JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_telemesh_data_helper_AppCredentials_getBroadCastUrl(JNIEnv *env, jobject) {
      std::string broadcast_url = "Here have to set your broadcast url";
      return env->NewStringUTF(broadcast_url.c_str());
    }

    extern "C" JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_telemesh_data_helper_AppCredentials_getParseUrl(JNIEnv *env, jobject) {
      std::string parse_url = "Here have to set your parse server url";
      return env->NewStringUTF(parse_url.c_str());
    }

    extern "C" JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_telemesh_data_helper_AppCredentials_getParseAppId(JNIEnv *env, jobject) {
      std::string parse_app_id = "Here have to set your parse app id";
      return env->NewStringUTF(parse_app_id.c_str());
    }

    extern "C" JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_telemesh_data_helper_AppCredentials_getAuthUserName(JNIEnv *env, jobject) {
      std::string auth_user_name = "Here have to set your Authenticate name";
      return env->NewStringUTF(auth_user_name.c_str());
    }

    extern "C" JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_telemesh_data_helper_AppCredentials_getAuthPassword(JNIEnv *env, jobject) {
      std::string auth_password = "Here have to set your authenticate password";
      return env->NewStringUTF(auth_password.c_str());
    }

    extern "C" JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_telemesh_data_helper_AppCredentials_getFileRepoLink(JNIEnv *env, jobject) {
      std::string file_repo_link = "Here have to set your file download repo link";
      return env->NewStringUTF(file_repo_link.c_str());
    }

    extern "C" JNIEXPORT jstring JNICALL
    Java_com_w3engineers_unicef_telemesh_data_helper_AppCredentials_getConfiguration(JNIEnv *env, jobject) {
      std::string config_file = "Here have to set a Json file as string like below”;
      return env->NewStringUTF(config_file.c_str());
    }


  JSON file for configuration:
  ::
    {
      "config_version_name":"0.0.1",
      "config_version_code":1,
      "token_per_mb":1.0,
      "default_network_type":2,
      "token_guide_version":0,
      "GIFT_DONATE_LINK" : "Here set your gift donate link",
      "wallet_rmesh_available": false,
      "network": [
        {
          "network_type":2,
          "network_name":"Kotti",
          "network_url":"Here set network url",
          "currency_symbol":"ETC",
          "token_symbol":"TMESH",
          "token_address":"Here set your token address",
          "channel_address":"Here set your channel address",
          "gas_price":25000000000,
          "gas_limit":800000,
          "token_amount":0,
          "currency_amount":0
        }
      ]
    }


  For more query please join us through `Discord`_ channel.

  e. If any new credential is added have to add an API into AppCredentials.java class to access that credentials.

  f. Delete the following two files from **jniLibs** package.

    1. armeabi-v7a
    2. x86

  g. Find the externalNativeBuild {} tag from app-level build.gradle and uncomment this line: path ``src/main/cpp/CMakeLists.txt``

  h. Execute Gradle sync


6. Now check the ``ViperUtil.java`` class and find the constructor ``ViperUtil`` where we use the above credentials

  ::

    protected ViperUtil(UserModel userModel) {
      try {
        context = MainActivity.getInstance() != null ? MainActivity.getInstance() : TeleMeshApplication.getContext();
        String appName = context.getResources().getString(R.string.app_name);


        String AUTH_USER_NAME = AppCredentials.getInstance().getAuthUserName();
        String AUTH_PASSWORD = AppCredentials.getInstance().getAuthPassword();
        String FILE_REPO_LINK = AppCredentials.getInstance().getFileRepoLink();
        String PARSE_APP_ID = AppCredentials.getInstance().getParseAppId();
        String PARSE_URL = AppCredentials.getInstance().getParseUrl();
        String CONFIG_DATA = AppCredentials.getInstance().getConfiguration();


        SharedPref sharedPref = SharedPref.getSharedPref(context);
        String address = sharedPref.read(Constants.preferenceKey.MY_WALLET_ADDRESS);
        String publicKey = sharedPref.read(Constants.preferenceKey.MY_PUBLIC_KEY);
        String networkSSID = sharedPref.read(Constants.preferenceKey.NETWORK_PREFIX);

        initObservers();

        if (TextUtils.isEmpty(networkSSID)) {
          networkSSID = context.getResources().getString(R.string.def_ssid);
        }

        viperClient = ViperClient.on(context, appName, context.getPackageName(), networkSSID, userModel.getName(), address, publicKey, userModel.getImage(), userModel.getTime(), true)
        .setConfig(AUTH_USER_NAME, AUTH_PASSWORD, FILE_REPO_LINK, PARSE_URL, PARSE_APP_ID, CONFIG_DATA);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

7. For wallet design currently, we are using default design from ``Viper``

8. In Telemesh we are using ``Parse`` server to store analytics data from local mesh.

   Please follow this `Parse`_ installation process in Android.

   To configure parse with Telemesh, follow the steps are given below:

      Step 1:  Add parse server Android SDK version in ``version.gradle``

      Step 2: If you want to deploy your own parse server in any platform, you have to update parse server ``URL`` and parse server ``APP-ID`` in the Telemesh project.

      Step 3. You have to add the ``PARSE_URL`` and ``PARSE_APP_ID`` in the GradleBuildValues interface that located in Constants class.

   The sample Parse model (Table) structure is

    ::

         ParseObject parseObj = new ParseObject(“table_name”);
         parseObj.put(“column_name”,”value”);
         ………….
         parseObj.saveInBackground();

   The parse server table structure and save/update process located in ``parseapi`` package.

   Parse Server installation Inside a Docker container

   ::

         $ git clone https://github.com/parse-community/parse-server
         $ cd parse-server
         $ docker build --tag parse-server .
         $ docker run --name my-parse-server -p 1337:1337  -d parse-server --appId APPLICATION_ID --masterKey MASTER_KEY --databaseURI mongodb://mongo/test





Happy Coding :)


After successful build, install the apk into any Android device (minimum android version ``Lollipop - 5.0``)

and you need to download ``TeleService`` apk from Telemesh inside which will provide multihop mesh support into Telemesh.



.. _Discord: https://discord.gg/SHG4qrH
.. _Parse: https://docs.parseplatform.org/android/guide/