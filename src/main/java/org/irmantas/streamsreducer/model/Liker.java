package org.irmantas.streamsreducer.model;

import org.springframework.data.annotation.Id;

public class Liker {
    private @Id String id;
    private String nickName;
    private long timestamp;


    public Liker() {
    }

    public Liker(String id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public Liker(String id, String nickName, long timestamp) {
        this.id = id;
        this.nickName = nickName;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "Liker{" +
                "id='" + id + '\'' +
                ", nickName='" + nickName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
