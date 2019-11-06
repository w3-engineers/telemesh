.. _TeleMesh App Architecture:
.. _Room: https://developer.android.com/topic/libraries/architecture/room
.. _RxJava: https://www.toptal.com/android/functional-reactive-android-rxjava
.. _SQL: https://www.khanacademy.org/computing/computer-programming/sql-documentation

TeleMesh App Architecture
-------------------------

We can use the ``Message Module`` to explain this App architecture.

1. In case of an Outgoing message, a message data will be sent from ``ChatActivity`` to
the ``ChatViewModel`` directly.

2. Then the message data will be saved in ``MessageSourceData`` .

3. We are using `Room`_ for storing data. As Room operate in background thread that’s why the message data will
be sent using `RxJava`_. Because it manages both background and foreground
task smoothly. All messages will be stored in the Message Table. All `SQL`_
is executed in MessageDao.

4. In the next step, message data will be sent to **Communication Layer**.
The ``Source`` will get the message from **MessageDao** as **RxJava observer** is
attached to it. Automatically ``RmDataHelper`` will get the message data
because **RxJava(Flowable)** is being used for getting last inserted data.

5. ``RmDataHelper`` will send the message to MeshDataSource and this will
prepare this message data into JSON data to pass via the Mesh Network.

6. In the case of an Incoming message, when any message will be available
in this network, it will be discovered in the application’s
Communication Layer. ``MeshDataSource`` will receive this message data and
send to ``RmDataHelper`` directly. ``Source`` will receive the data from
``RmDataHelper``. Now message data will be saved in Message table using
MessageDao.

7. Now, View Layer will get the event of the message data.
``MessageSourceData`` will get the message data from MessageDao as **RxJava observer**
is attached to it. To show message in view level, the message
data will be provided to ``ChatViewModel`` automatically using
**RxJava(Flowable)**. After that, message will be seen in UI i.e.:
ChatActivity, through LiveData.

8. ``RMDataHelper`` is directly connected with ``NotificationUtil``. When any data
is available to RMDataHelper, notification will be triggered as per
app’s requirements.



.. figure:: app_architecture.png
   :scale: 50 %
   :alt: app architecture

