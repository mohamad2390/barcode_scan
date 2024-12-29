package com.tafavotco.samarapp.model;

public class EventRequestModel {

    private String eventHash;
    private String participantHash;

    public EventRequestModel(String eventHash, String participantHash) {
        this.eventHash = eventHash;
        this.participantHash = participantHash;
    }

    public String getEventHash() {
        return eventHash;
    }

    public void setEventHash(String eventHash) {
        this.eventHash = eventHash;
    }

    public String getParticipantHash() {
        return participantHash;
    }

    public void setParticipantHash(String participantHash) {
        this.participantHash = participantHash;
    }
}
