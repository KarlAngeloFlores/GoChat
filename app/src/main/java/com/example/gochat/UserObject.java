package com.example.gochat;

import java.io.Serializable;

public class UserObject implements Serializable {

    private String name, phone, uid;

    private Boolean selected = false;

    public UserObject(String uid){
        this.uid = uid;
    }

    public Boolean getSelected() {
        return selected;
    }

    public UserObject(String uid, String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
