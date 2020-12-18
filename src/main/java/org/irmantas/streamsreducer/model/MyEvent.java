package org.irmantas.streamsreducer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.LinkedList;
import java.util.List;

@Document(indexName = "events")
public class MyEvent {
    private @Id
    String id;
    private List<Liker> likers;

    public MyEvent(String id) {
        this.id = id;
        likers = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Liker> getLikers() {
        return likers;
    }
}
