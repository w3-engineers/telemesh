// IRmServiceConnection.aidl
package com.w3engineers.ext.viper;

import com.w3engineers.ext.viper.IRmCommunicator;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;

interface IRmServiceConnection {

    void setBroadCastActionString(in String actionString);

    void setServiceToCloseWithTask(in boolean isToCloseWithTask);

    void setProfile(in byte[] profileInfo);

    int sendMeshData(in MeshData meshData);

    void setRmCommunicator(IRmCommunicator iRmCommunicator);

    void setServiceForeground(in boolean isForeGround);

    void resetCommunicator(IRmCommunicator iRmCommunicator);

    List<BaseMeshData> getLivePeers();
}
