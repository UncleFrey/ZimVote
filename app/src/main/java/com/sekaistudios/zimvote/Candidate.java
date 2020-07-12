package com.sekaistudios.zimvote;

public class Candidate {
    private String electionID, campaignID, id, name, dob, pob, party;
    private long voteCount;
    private String securityCode, log;
    private boolean status;

    public Candidate() {
    }


    public Candidate(String electionID, String campaignID, String id, String name, String dob,
                     String pob, String party, long voteCount, String securityCode, String log, boolean status) {
        this.electionID = electionID;
        this.campaignID = campaignID;
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.pob = pob;
        this.party = party;
        this.voteCount = voteCount;
        this.securityCode = securityCode;
        this.log = log;
        this.status = status;
    }

    public String getElectionID() {
        return electionID;
    }

    public void setElectionID(String electionID) {
        this.electionID = electionID;
    }

    public String getCampaignID() {
        return campaignID;
    }

    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
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

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String toString(){
        return name + "- " + party;
    }
}
