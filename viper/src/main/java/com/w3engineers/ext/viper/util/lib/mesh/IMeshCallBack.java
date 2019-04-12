package com.w3engineers.ext.viper.util.lib.mesh;

import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public interface IMeshCallBack {

    void onMesh(MeshData meshData);
    void onMesh(MeshAcknowledgement meshAcknowledgement);

    void onProfileInfo(BaseMeshData baseMeshData);
    void onPeerRemoved(MeshPeer meshPeer);

    void onInitSuccess(MeshPeer selfMeshPeer);
    void onInitFailed(int reason);

}
