package com.tafavotco.samarapp.model;

public class ActivityRequestModel {
    private String eventHash;
    private String activityHash;
    private String participantHash;

    public ActivityRequestModel(String eventHash, String activityHash, String participantHash) {
        this.eventHash = eventHash;
        this.activityHash = activityHash;
        this.participantHash = participantHash;
    }

    public String getEventHash() {
        return eventHash;
    }

    public void setEventHash(String eventHash) {
        this.eventHash = eventHash;
    }

    public String getActivityHash() {
        return activityHash;
    }

    public void setActivityHash(String activityHash) {
        this.activityHash = activityHash;
    }

    public String getParticipantHash() {
        return participantHash;
    }

    public void setParticipantHash(String participantHash) {
        this.participantHash = participantHash;
    }
}
