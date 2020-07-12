package com.sekaistudios.zimvote;

public class VoterVerification {
    private String verificationID, electionID;

    public VoterVerification() {
    }

    public VoterVerification(String verificationID, String electionID) {
        this.verificationID = verificationID;
        this.electionID = electionID;
    }

    public String getVerificationID() {
        return verificationID;
    }

    public void setVerificationID(String verificationID) {
        this.verificationID = verificationID;
    }

    public String getElectionID() {
        return electionID;
    }

    public void setElectionID(String electionID) {
        this.electionID = electionID;
    }
}
