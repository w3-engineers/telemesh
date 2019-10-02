package com.w3engineers.ext.viper.application.data.local;

import android.content.Context;
import android.os.Process;
import android.os.RemoteException;

import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.ext.viper.util.lib.mesh.MeshConfig;
import com.w3engineers.ext.viper.util.lib.mesh.MeshProviderWithoutAidl;

import java.util.ArrayList;
import java.util.List;

/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public abstract class BaseMeshDataSourceWithoutAidl implements MeshProviderWithoutAidl.ProviderCallback {

    private Context context;
    private byte[] profileInfo;
    private MeshProviderWithoutAidl meshProviderWithoutAidl;

    protected BaseMeshDataSourceWithoutAidl(Context context, byte[] profileInfo) {

        //intentional hard string
        if (context == null)
            throw new NullPointerException("Context can not be null");

        this.context = context;
        this.profileInfo = profileInfo;

        startMeshSystem();
    }

    private void startMeshSystem() {
        meshProviderWithoutAidl = MeshProviderWithoutAidl.getInstance();

        MeshConfig meshConfig = new MeshConfig();
        meshConfig.mPort = 10626;

        meshProviderWithoutAidl.setConfig(meshConfig);
        meshProviderWithoutAidl.setMyProfileInfo(profileInfo);
        meshProviderWithoutAidl.setProviderCallback(this);

        meshProviderWithoutAidl.startMesh();
    }

    public String sendMeshData(MeshData meshData) {
        try {
            if (meshProviderWithoutAidl != null) {
                return meshProviderWithoutAidl.sendMeshData(meshData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stopMeshService() {
        try {
            if (meshProviderWithoutAidl != null) {
                meshProviderWithoutAidl.stopMesh(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopProcess() {
        Process.killProcess(Process.myPid());
    }

    public void stopMeshProcess() {
        try {
            stopProcess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMyMeshId() {
        try {
            if (meshProviderWithoutAidl != null) {
                return meshProviderWithoutAidl.getMyUserId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void restartMesh() {
        try {
            if (meshProviderWithoutAidl != null) {
                meshProviderWithoutAidl.restartMesh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllSellers() {
        try {
            if (meshProviderWithoutAidl != null) {
                return meshProviderWithoutAidl.getAllSellers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public int getUserActiveStatus(String userId) {
        try {
            if (meshProviderWithoutAidl != null) {
                return meshProviderWithoutAidl.getUserActiveStatus(userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Overridable method to receive the event of Library init
     *
     * @throws RemoteException
     */
    protected abstract void onRmOn();

    /**
     * Called upon receiving any Peer data
     *
     * @param profileInfo
     */
    protected abstract void onPeer(BaseMeshData profileInfo);

    /**
     * Calls upon disappearing of peers
     *
     * @param meshPeer
     */
    protected abstract void onPeerGone(MeshPeer meshPeer);

    /**
     * Upon receiving any data from any peer
     *
     * @param meshData
     */
    protected abstract void onData(MeshData meshData);

    /**
     * Upon receiving Data delivery acknowledgement
     *
     * @param meshAcknowledgement
     */
    protected abstract void onAcknowledgement(MeshAcknowledgement meshAcknowledgement);

    protected abstract String getOwnUserId();

    protected abstract boolean isNodeAvailable(String nodeId, int userActiveStatus);

    protected abstract void showLog(String log);

    protected abstract void nodeIdDiscovered(String nodeId);

    /**
     * Overridable method to receive the event of library destroy
     *
     * @throws RemoteException
     */
    protected abstract void onRmOff();

    @Override
    public void meshStart() {
        onRmOn();
    }

    @Override
    public void connectionAdd(MeshData meshData) {
        onPeer(meshData);
    }

    @Override
    public void connectionRemove(MeshPeer meshPeer) {
        onPeerGone(meshPeer);
    }

    @Override
    public void receiveData(MeshData meshData) {
        onData(meshData);
    }

    @Override
    public void receiveAck(MeshAcknowledgement meshAcknowledgement) {
        onAcknowledgement(meshAcknowledgement);
    }

    @Override
    public void meshStop() {
        onRmOff();
    }

    @Override
    public boolean isNodeExist(String nodeId, int activeStatus) {
        return isNodeAvailable(nodeId, activeStatus);
    }

    @Override
    public void showMeshLog(String log) {
        showLog(log);
    }

    @Override
    public void onlyNodeDiscover(String nodeId) {
        nodeIdDiscovered(nodeId);
    }
}
