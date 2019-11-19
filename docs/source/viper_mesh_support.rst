.. viper_mesh_support:


Mesh Support
------------

To receive ``EVENTS`` from MeshService following Events are observed on Telemesh app end at ``ViperUtil.java`` class

inside the package ``com.w3engineers.unicef.util.helper``.



``ApiEvent.TRANSPORT_INIT`` - After initializing mesh service this event provide mesh initialization state with own user/peer id

``ApiEvent.WALLET_LOADED`` - After successfully wallet get loaded this event provide wallet status

``ApiEvent.PEER_ADD`` - This event provide the new peer id when another user/peer get discovered through local mesh

``ApiEvent.PEER_REMOVED`` - This event provide the remove peer id when user/peer get removed from local mesh

``ApiEvent.DATA`` - This event provide the received data in byte array format

``ApiEvent.DATA_ACKNOWLEDGEMENT`` - This event provide the send data/message acknowledgment status with message-id

``ApiEvent.USER_INFO`` - This event sends the connected peer’s info like peer name, peer image index, etc.


  ::

       private void initObservers() {

               AppDataObserver.on().startObserver(ApiEvent.TRANSPORT_INIT, event -> {
                   TransportInit transportInit = (TransportInit) event;

                   if (transportInit.success) {
                       myUserId = transportInit.nodeId;

                       onMesh(myUserId);
                   }
               });

               AppDataObserver.on().startObserver(ApiEvent.WALLET_LOADED, event -> {
                   WalletLoaded walletLoaded = (WalletLoaded) event;

                   if (walletLoaded.success) {
                       onMeshPrepared();
                   }
               });

               AppDataObserver.on().startObserver(ApiEvent.PEER_ADD, event -> {
                   PeerAdd peerAdd = (PeerAdd) event;
                   peerDiscoveryProcess(peerAdd.peerId, true);
               });

               AppDataObserver.on().startObserver(ApiEvent.PEER_REMOVED, event -> {
                   PeerRemoved peerRemoved = (PeerRemoved) event;
                   peerDiscoveryProcess(peerRemoved.peerId, false);
               });

               AppDataObserver.on().startObserver(ApiEvent.DATA, event -> {

                   DataEvent dataEvent = (DataEvent) event;

                   dataReceive(dataEvent.peerId, dataEvent.data);
               });

               AppDataObserver.on().startObserver(ApiEvent.DATA_ACKNOWLEDGEMENT, event -> {

                   DataAckEvent dataAckEvent = (DataAckEvent) event;

                   onAck(dataAckEvent.dataId, dataAckEvent.status);

               });

               AppDataObserver.on().startObserver(ApiEvent.USER_INFO, event -> {

                   UserInfoEvent userInfoEvent = (UserInfoEvent) event;

                   UserModel userModel = new UserModel().setName(userInfoEvent.getUserName())
                           .setImage(userInfoEvent.getAvatar())
                           .setTime(userInfoEvent.getRegTime());

                   peerAdd(userInfoEvent.getAddress(), userModel);
               });

           }




To receive data from Viper to Telemesh Android app following abstract methods are used on Telemesh app end at ``MeshDataSource.java`` class

inside the package ``com.w3engineers.unicef.telemesh.data.helper``.



``protected abstract void onMesh(String myMeshId)`` - When observer receive ``ApiEvent.TRANSPORT_INIT`` EVENT then this method get called.

``protected abstract void peerAdd(String peerId, byte[] peerData)`` - When observer receive ``ApiEvent.DATA`` EVENT then this method get called.

``protected abstract void peerAdd(String peerId, UserModel userModel)`` - When observer receive ``ApiEvent.USER_INFO`` EVENT then this method get called.

``protected abstract void peerRemove(String nodeId)`` - When observer receive ``ApiEvent.PEER_REMOVED`` EVENT then this method get called.

``protected abstract void onData(String peerId, ViperData viperData)`` -  When observer receive ``ApiEvent.DATA`` EVENT then this method get called.

``protected abstract void onAck(String messageId, int status)`` - When observer receive ``ApiEvent.DATA_ACKNOWLEDGEMENT`` EVENT then this method get called.

``protected abstract boolean isNodeAvailable(String nodeId, int userActiveStatus)`` - To check whether the user/peer is currently active/online
