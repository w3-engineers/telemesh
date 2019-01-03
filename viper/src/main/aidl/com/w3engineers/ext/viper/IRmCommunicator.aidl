// IRmServiceConnection.aidl
package com.w3engineers.ext.viper;

import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;

interface IRmCommunicator {

    void onLibraryInitSuccess();

    void onServiceDestroy();

    void onProfileInfo(in BaseMeshData baseMeshData);

    void onPeerRemoved(in MeshPeer meshPeer);

    void onMeshData(in MeshData meshData);

    void onMeshAcknowledgement(in MeshAcknowledgement meshAcknowledgement);
}
