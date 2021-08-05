package com.w3engineers.unicef.telemesh.data.local.grouptable;

import java.util.List;

public class RelayGroupModel {
    String d;
    byte t;
    List<String> u;

    public String getData() {
        return d;
    }

    public RelayGroupModel setData(String data) {
        this.d = data;
        return this;
    }

    public byte getType() {
        return t;
    }

    public RelayGroupModel setType(byte type) {
        this.t = type;
        return this;
    }

    public List<String> getUsers() {
        return u;
    }

    public RelayGroupModel setUsers(List<String> users) {
        this.u = users;
        return this;
    }
}
