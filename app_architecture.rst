TeleMesh App Architecture
-------------------------

We can use the Message Module to explain this App architecture. In case
of an Outgoing message, a message data will be sent from ChatActivity to
the ChatViewModel directly. Then the message data will be saved in
MessageSourceData . We are using Room Database for storing data. As Room
Database operate in background thread that’s why the message data will
be sent using RxJava. Because it manages both background and foreground
task smoothly. All messages will be stored in the Message Table. All SQL
is executed in MessageDao.

In the next step, message data will be sent to Communication Layer. The
Source will get the message from MessageDao as RxJava observer is
attached to it. Automatically RmDataHelper will get the message data
because RxJava(Flowable) is being used for getting last inserted data.

RmDataHelper will send the message to MeshDataSource and this will
prepare this message data into JSON data to pass via the Mesh Network.

In the case of an Incoming message, when any message will be available
in this network, it will be discovered in the application’s
Communication Layer. MeshDataSource will receive this message data and
send to RmDataHelper directly. .Source will receive the data from
RmDataHelper. Now message data will be saved in Message table using
MessageDao.

Now, View Layer will get the event of the message data.
MessageSourceData will get the message data from MessageDao as RxJava
observer is attached to it. To show message in view level, the message
data will be provided to ChatViewModel automatically using
RxJava(Flowable). After that, message will be seen in UI i.e.:
ChatActivity, through LiveData.

RMDataHelper is directly connected with NotificationUtil. When any data
is available to RMDataHelper, notification will be triggered as per
app’s requirements.



.. figure:: diagram/app_architecture.png
   :scale: 50 %
   :alt: map to buried treasure