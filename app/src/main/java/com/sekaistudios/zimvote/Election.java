package com.sekaistudios.zimvote;

public class Election {
    private String id, name, campaigns, log;
    private Boolean status;

    public Election() {
    }

    public Election(String id, String name, String campaigns, String log, Boolean status) {
        this.id = id;
        this.name = name;
        this.campaigns = campaigns;
        this.log = log;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(String campaigns) {
        this.campaigns = campaigns;
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

    @Override
    public String toString(){
        return name;
    }
}
