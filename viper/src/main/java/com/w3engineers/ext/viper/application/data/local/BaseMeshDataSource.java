package com.w3engineers.ext.viper.application.data.local;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.w3engineers.ext.viper.IRmCommunicator;
import com.w3engineers.ext.viper.IRmServiceConnection;
import com.w3engineers.ext.viper.application.data.local.service.MeshService;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;

public abstract class BaseMeshDataSource {

    private IRmServiceConnection iSetInfo;
    private Context context;
    private byte[] profileInfo;

    protected BaseMeshDataSource(Context context, byte[] profileInfo) {

        //intentional hard string
        if(context == null)
            throw new NullPointerException("Context can not be null");

        this.context = context;
        this.profileInfo = profileInfo;

        connectToService();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            iSetInfo = IRmServiceConnection.Stub.asInterface(service);

            try {
                iSetInfo.setServiceForeground(false);
                iSetInfo.setRmCommunicator(iGetInfo);
                iSetInfo.setProfileInfo(profileInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(iSetInfo != null){
                iSetInfo = null;
            }
        }
    };

    public long sendMeshData(MeshData meshData) {
        try {
            if(iSetInfo != null){
                return iSetInfo.sendMeshData(meshData);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    /**
     * To check underlying service properly initiated or not
     * @return true if connected
     */
    public boolean isServiceConnected() {
        return iSetInfo != null;
    }

    /**
     * If service is not initiated properly then this method throws {@link IllegalStateException}.
     * Before using the method check service initiation through {@link #isServiceConnected()}
     * @param isForeGround - set boolean for foreground mode
     */
    public void setServiceForeground(boolean isForeGround) {

        if(iSetInfo == null) {
            throw new IllegalStateException("Service not initiated properly");
        }

        try {
            iSetInfo.setServiceForeground(isForeGround);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void connectToService() {
        //Normally this should not be required but found in some devices service restarts upon
        // calling start service. e.g: Symphony ZVi
        Intent serviceIntent = new Intent(context, MeshService.class);
        context.startService(serviceIntent);
        context.bindService(serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    /**
     * Overridable method to receive the event of Library init
     * @throws RemoteException
     */
    protected abstract void onRmOn();

    /**
     * Called upon receiving any Peer data
     * @param profileInfo
     */
    protected abstract void onPeer(BaseMeshData profileInfo);

    /**
     * Calls upon disappearing of peers
     * @param meshPeer
     */
    protected abstract void onPeerGone(MeshPeer meshPeer);

    /**
     * Upon receiving any data from any peer
     * @param meshData
     */
    protected abstract void onData(MeshData meshData);

    /**
     * Upon receiving Data delivery acknowledgement
     * @param meshAcknowledgement
     */
    protected abstract void onAcknowledgement(MeshAcknowledgement meshAcknowledgement);

    protected abstract String getOwnUserId();

    /**
     * Overridable method to receive the event of library destroy
     * @throws RemoteException
     */
    protected abstract void onRmOff();

    IRmCommunicator.Stub iGetInfo = new IRmCommunicator.Stub() {
        @Override
        public void onLibraryInitSuccess() throws RemoteException {
            onRmOn();
        }

        @Override
        public void onServiceDestroy() throws RemoteException {
            onRmOff();
        }

        @Override
        public void onProfileInfo(BaseMeshData baseMeshData) throws RemoteException {
            onPeer(baseMeshData);
        }

        @Override
        public void onPeerRemoved(MeshPeer meshPeer) throws RemoteException {
            onPeerGone(meshPeer);
        }

        @Override
        public void onMeshData(MeshData meshData) throws RemoteException {
            onData(meshData);
        }

        @Override
        public void onMeshAcknowledgement(MeshAcknowledgement meshAcknowledgement) throws RemoteException {
            onAcknowledgement(meshAcknowledgement);
        }
    };
}
