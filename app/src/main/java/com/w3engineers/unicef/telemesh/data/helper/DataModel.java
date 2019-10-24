package com.w3engineers.unicef.telemesh.data.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class DataModel {

    private String userId, dataTransferId;
    private byte[] rawData;
    private byte dataType;
    private boolean isAckSuccess;
    private int dataAckStatus;

    public String getUserId() {
        return userId;
    }

    public DataModel setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getDataTransferId() {
        return dataTransferId;
    }

    public DataModel setDataTransferId(String dataTransferId) {
        this.dataTransferId = dataTransferId;
        return this;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public DataModel setRawData(byte[] rawData) {
        this.rawData = rawData;
        return this;
    }

    public byte getDataType() {
        return dataType;
    }

    public DataModel setDataType(byte dataType) {
        this.dataType = dataType;
        return this;
    }

    public boolean isAckSuccess() {
        return isAckSuccess;
    }

    public DataModel setAckSuccess(boolean ackSuccess) {
        isAckSuccess = ackSuccess;
        return this;
    }

    public int getDataAckStatus() {
        return dataAckStatus;
    }

    public DataModel setDataAckStatus(int dataAckStatus) {
        this.dataAckStatus = dataAckStatus;
        return this;
    }
}
