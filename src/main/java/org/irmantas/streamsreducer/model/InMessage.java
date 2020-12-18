package org.irmantas.streamsreducer.model;


import org.springframework.data.annotation.Id;

public class InMessage {
    private @Id Long senderID;
    private Long receiverID;
    private String heading;
    private String content;
    private String eventType;
    private String senderNick;
    private long timestamp;

    public InMessage() {
    }

    public InMessage(Long senderID, Long receiverID, String heading, String content, String eventType, String senderNick, long timestamp) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.heading = heading;
        this.content = content;
        this.eventType = eventType;
        this.senderNick = senderNick;
        this.timestamp = timestamp;
    }

    public InMessage(InMessage message) {
        this.senderID = message.senderID;
        this.receiverID = message.receiverID;
        this.heading = message.heading;
        this.content = message.content;
        this.eventType = message.eventType;
        this.senderNick = message.senderNick;
        this.timestamp = message.timestamp;
    }

    public Long getSenderID() {
        return senderID;
    }

    public void setSenderID(Long senderID) {
        this.senderID = senderID;
    }

    public Long getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(Long receiverID) {
        this.receiverID = receiverID;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSenderNick() {
        return senderNick;
    }

    public void setSenderNick(String senderNick) {
        this.senderNick = senderNick;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "InMessage{" +
                "senderID=" + senderID +
                ", receiverID=" + receiverID +
                ", heading='" + heading + '\'' +
                ", content='" + content + '\'' +
                ", eventType='" + eventType + '\'' +
                ", senderNick='" + senderNick + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
