.. _project_structure:

Project’s Structure
-------------------

::

       .
       |-- app
       |-- src
       |-- main
           |-- com.w3engineers.unicef
               |-- telemesh
                   |-- data #local database, file, shared preferences etc.
                   |-- ui #ui components
               |-- util
                   |-- helper #Generic tasks like TimeUtil, NetworkUtil etc.
                   |-- lib #third party library, component etc.
               |-- Application.java #Android Application class
       |-- appshare #W3Engineers in app share module
       |-- localserver #W3Engineers in app update module
       |-- build.gradle
       |-- settings.gradle
       |-- versions.gradle
       |-- gradle.properties

-  **Alias** N/A

-  **Commands** N/A