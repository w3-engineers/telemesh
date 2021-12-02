.. _deployment:

Telemesh Deployment
-------------------

After cloning the Telemesh GitHub repo in your local machine it is effortless to build the Telemesh app.

The *Telemesh* app is using different ``URLs`` for different services. Preparing all those services is time-consuming as well as tedious too for outsiders.

Here if you want to simply deploy/build the *Telemesh* app, you don't need to worry at all about those tasks.

All the ``URLs`` and other permission related information are provided in a ``.so`` file as a solid package to give you a staging environment the same as production for deployment purposes.

Just download the project and run.

But, we don't give the guarantee of continuous services for the staging environment, as it's only for deployment purposes.

After a successful build, install the apk into any Android device (minimum android version ``Lollipop - 5.0``)

and you need to download the ``TeleService`` apk from Telemesh inside which will provide multihop mesh support into Telemesh.


Parse deployment
~~~~~~~~~~~~~~~~

In Telemesh we are using ``Parse`` server to store analytics data from local mesh.

Please follow this `Parse`_ installation process in Android.

   To configure parse with Telemesh, follow the steps are given below:

      Step 1:  Add parse server Android SDK version in ``version.gradle``

      Step 2: If you want to deploy your own parse server in any platform, you have to update parse server ``URL`` and parse server ``APP-ID`` in the Telemesh project.

      Step 3. You have to add the ``PARSE_URL`` and ``PARSE_APP_ID`` in the GradleBuildValues interface that located in Constants class.



Parse Server installation Inside a Docker container

   ::

         $ git clone https://github.com/parse-community/parse-server
         $ cd parse-server
         $ docker build --tag parse-server .
         $ docker run --name my-parse-server -p 1337:1337  -d parse-server --appId APPLICATION_ID --masterKey MASTER_KEY --databaseURI mongodb://mongo/test

If you need any help on any stage of your deployment and you want to communicate with us please join our `Discord`_ channel or communicate to our community manager
through [media@telemesh.net] & [info@telemesh.net].






.. _Discord: https://discord.gg/SHG4qrH
.. _Parse: https://docs.parseplatform.org/android/guide/

