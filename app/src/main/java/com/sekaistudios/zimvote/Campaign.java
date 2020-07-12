package com.sekaistudios.zimvote;

public class Campaign {

    private String electionID, id, type, province, district, region, ward, candidates, verifications,
    log;

    private Boolean status;

    public Campaign() {
    }

    public Campaign(String electionID, String id, String type, String province, String district,
                    String region, String ward, String candidates, String verifications, String log, Boolean status) {
        this.electionID = electionID;
        this.id = id;
        this.type = type;
        this.province = province;
        this.district = district;
        this.region = region;
        this.ward = ward;
        this.candidates = candidates;
        this.verifications = verifications;
        this.log = log;
        this.status = status;
    }

    public String getElectionID() {
        return electionID;
    }

    public void setElectionID(String electionID) {
        this.electionID = electionID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getCandidates() {
        return candidates;
    }

    public void setCandidates(String candidates) {
        this.candidates = candidates;
    }

    public String getVerifications() {
        return verifications;
    }

    public void setVerifications(String verifications) {
        this.verifications = verifications;
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

    public String toString(){
        final String listing = province + "- " + district + "- " + region + "- Ward " + ward;
        return listing;
    }
}
