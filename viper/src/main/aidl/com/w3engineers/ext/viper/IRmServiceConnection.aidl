// IRmServiceConnection.aidl
package com.w3engineers.ext.viper;

import com.w3engineers.ext.viper.IRmCommunicator;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;

interface IRmServiceConnection {

    void setBroadCastActionString(in String actionString);

    void setServiceToCloseWithTask(in boolean isToCloseWithTask);

    void setProfile(in byte[] profileInfo, in String userId);

    void setProfileInfo(in byte[] profileInfo);

    String sendMeshData(in MeshData meshData);

    void setRmCommunicator(IRmCommunicator iRmCommunicator);

    void setServiceForeground(in boolean isForeGround);

    void resetCommunicator(IRmCommunicator iRmCommunicator);

    List<BaseMeshData> getLivePeers();

    void openRmSettings();

    void stopRmService();

    void stopMeshProcess();

    String getMyId();

    void restartMeshService();

    List<String> getCurrentSellers();

    int getUserLinkType(in String userId);

    int getMyMode();
}
