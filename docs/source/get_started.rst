.. _get_started:

Get started
-----------

**Step 1: Clone repository:** Navigate to directory where you want to
keep source code. Open command prompt.

  Execute below command::

     git clone https://github.com/w3-engineers/telemesh.git

Here you will get how to install Git and start contribute to `Open source`_

**Step 2: Prepare gradle.properties file:** You need to request some credentials
to the Telemesh team to build the project successfully.

Edit the file ``~/.gradle/gradle.properties`` and add the

 following credentials::

              BROADCAST_TOKEN = "<BROADCAST_TOKEN>"
              BROADCAST_URL = "<BROADCAST_URL>"
              PARSE_URL = "<PARSE_URL>"
              PARSE_APP_ID = "<PARSE_APP_ID>"


 Directory ::

    For Unix based system the directory is ``~/.gradle/gradle.properties``

    For Windows system the directory is ``C:\Users\username\.gradle\gradle.properties``

If you don't find ``gradle.properties`` file then you can create your own.

For more details please follow the :ref:`Telemesh Development Guideline <development_step_by_step>`

Also you can use the following communication methods

-  The ``#get-help`` channel on our `Discord chat`_

-  The mailing list [media@telemesh.net] for long term discussion.

**Step 3: Sync and build:** If everything is ok then sync and build
should work as it should be. If not please recheck step 1 and 2.

**Step 4: Test on device:**

Minimum API: 19 (KitKat - 4.4.x)

.. _Discord chat: https://discord.gg/SHG4qrH
.. _Open source: https://www.digitalocean.com/community/tutorial_series/an-introduction-to-open-source
.. _Development Guideline: https://www.digitalocean.com/community/tutorial_series/an-introduction-to-open-source
