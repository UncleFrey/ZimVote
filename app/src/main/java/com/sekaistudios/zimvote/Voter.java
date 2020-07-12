package com.sekaistudios.zimvote;

public class Voter {
    private String id, name, dob, pob;
    private VoterVerification voterVoteVerification;
    private String log;
    private Boolean status;

    public Voter() {
    }

    public Voter(String id, String name, String dob, String pob, VoterVerification voterVoteVerification, String log, Boolean status) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.pob = pob;
        this.voterVoteVerification = voterVoteVerification;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public VoterVerification getVoterVoteVerification() {
        return voterVoteVerification;
    }

    public void setVoterVoteVerification(VoterVerification voterVoteVerification) {
        this.voterVoteVerification = voterVoteVerification;
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