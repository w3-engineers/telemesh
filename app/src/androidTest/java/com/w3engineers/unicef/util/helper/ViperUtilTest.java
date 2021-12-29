package com.w3engineers.unicef.util.helper;


import static org.junit.Assert.assertTrue;

import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.ContentPendingModel;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.util.helper.model.ViperData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ViperUtilTest {

    class CommonViper extends ViperUtil{

        protected CommonViper(UserModel userModel) {
            super(userModel);
        }

        @Override
        protected void onMesh(String myMeshId) {

        }

        @Override
        protected void onMeshPrepared(String myWalletAddress) {

        }

        @Override
        protected void offMesh() {

        }

        @Override
        protected void peerAdd(String peerId, byte[] peerData) {

        }

        @Override
        protected void peerAdd(String peerId, UserModel userModel) {

        }

        @Override
        protected void peerRemove(String nodeId) {

        }

        @Override
        protected void onData(String peerId, ViperData viperData) {

        }

        @Override
        protected void onAck(String messageId, int status) {

        }

        @Override
        protected boolean isNodeAvailable(String nodeId, int userActiveStatus) {
            return false;
        }

        @Override
        protected void contentReceiveStart(String contentId, String contentPath, String userId, byte[] metaData) {

        }

        @Override
        protected void contentReceiveInProgress(String contentId, int progress) {

        }

        @Override
        protected void contentReceiveDone(String contentId, boolean contentStatus, String msg) {

        }

        @Override
        protected void pendingContents(ContentPendingModel contentPendingModel) {

        }

        @Override
        protected void receiveBroadcast(String broadcastId, String metaData, String contentPath, double latitude, double longitude, double range, String expiryTime) {

        }

        @Override
        protected void openSelectAccountActivity() {

        }

        @Override
        protected void onWalletPrepared(boolean isOldAccount, boolean isImportWallet) {

        }

        @Override
        protected void onWalletBackUp(boolean isSuccess) {

        }
    }

    CommonViper commonViper;

    @Before
    public void setUp() throws Exception {
        UserModel userModel = new UserModel();
        commonViper = new CommonViper(userModel);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testNullSellers(){
        addDelay();
        commonViper.getAllSellers();
        assertTrue(true);
    }

    @Test
    public void testUserActiveStatusException(){
        addDelay();
        commonViper.getUserActiveStatus(null);
        assertTrue(true);

    }

    @Test
    public void testUserInfoSaveException(){
        addDelay();
        commonViper.saveUserInfo(null);
        assertTrue(true);
    }



    private void addDelay() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
