.. _development_step_by_step:

Telemesh
---------------------------------

1. Find the ``versions.gradle`` in the Telemesh root directory of the repo and
   any new support library reference should be added here.

2. Any support library on app level ``build.gradle`` should be added in
   this way

   ::

           implementation deps.support.app_compat
           implementation deps.support.design
           implementation deps.constraint_layout
           implementation deps.support.recyclerview
           implementation deps.support.cardview


3. ``Viper`` dependency should be added in the same way inside app level ``build.gradle``

4. Edit the file `~/.gradle/gradle.properties` and add the following credentials.

    ..

          BROADCAST_TOKEN = "<BROADCAST_TOKEN>"
          BROADCAST_URL = "<BROADCAST_URL>"
          PARSE_URL = "<PARSE_URL>"
          PARSE_APP_ID = "<PARSE_APP_ID>"



    ..
        For Unix based system the directory is ``~/.gradle/gradle.properties``

        For Windows system the directory is ``C:\Users\username\.gradle\gradle.properties``

   If you don't find ``gradle.properties`` file then you can create your own.
   For credentials please join our `Discord`_ channel.


5. Prepare ``config.json`` file inside Android Studio and put it under the ``assets`` folder of your app module.
   Then paste this below text into file

    .. code-block:: JSON

      {
        "AUTH_USER_NAME" : "<AUTH_USER_NAME>",
        "AUTH_PASSWORD" : "<AUTH_PASSWORD>",
        "APP_DOWNLOAD_LINK" : "<UPDATED_APP_DOWNLOAD_LINK>",
        "GIFT_DONATE_LINK" : "<GIFT_DONATE_URL>"
      }

   Here you need to provide valid credentials. For credentials please join our `Discord`_ channel.


6. Now check the ``ViperUtil.java`` class and find the constructor ``ViperUtil`` where we use the above credentials

    ::

           protected ViperUtil(UserModel userModel) {

              try {

                  Context context = MainActivity.getInstance() != null ? MainActivity.getInstance() : TeleMeshApplication.getContext();
                  String appName = context.getResources().getString(R.string.app_name);

                  String jsonData = loadJSONFromAsset(context);

                  if (!TextUtils.isEmpty(jsonData)) {
                      JSONObject jsonObject = new JSONObject(jsonData);

                      String AUTH_USER_NAME = jsonObject.optString("AUTH_USER_NAME");
                      String AUTH_PASSWORD = jsonObject.optString("AUTH_PASSWORD");
                      String APP_DOWNLOAD_LINK = jsonObject.optString("APP_DOWNLOAD_LINK");
                      String GIFT_DONATE_LINK = jsonObject.optString("GIFT_DONATE_LINK");

                      viperClient = ViperClient.on(context, appName, "com.w3engineers.unicef.telemesh", "captor", userModel.getName(), userModel.getImage(), userModel.getTime(), true)
                              .setConfig(AUTH_USER_NAME, AUTH_PASSWORD, APP_DOWNLOAD_LINK, GIFT_DONATE_LINK);

                      initObservers();
                  }
              } catch (JSONException e) {
                  e.printStackTrace();
              }
           }



7. For wallet design currently we are using default design from ``Viper``



.. _Discord: https://discord.gg/SHG4qrH


