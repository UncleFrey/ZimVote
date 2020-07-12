package com.sekaistudios.zimvote;

public class Official {

    private String name, id, email, pin, secLevel, uid, log;
    private Boolean status;

    public Official() {
    }

    public Official(String name, String id, String email, String pin, String secLevel, String uid, String log, Boolean status) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.pin = pin;
        this.secLevel = secLevel;
        this.uid = uid;
        this.log = log;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getSecLevel() {
        return secLevel;
    }

    public void setSecLevel(String secLevel) {
        this.secLevel = secLevel;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
