package com.actors;

/**
 * a Message to be sent through the {@link ActorSystem#send(Message, Class[])}
 * <p>
 * Created by Ahmed Adel Ismail on 5/2/2017.
 */
public class Message {

    private final int id;
    private final Object content;

    public Message(int id) {
        this(id, null);

    }

    public Message(int id, Object content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public <T> T getContent() {
        return (T) content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content=" + content +
                '}';
    }
}
