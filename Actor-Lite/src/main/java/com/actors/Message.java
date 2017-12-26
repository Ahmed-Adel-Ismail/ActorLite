package com.actors;

/**
 * a Message to be sent through the {@link ActorSystem#send(Message, Class[])}
 * <p>
 * Created by Ahmed Adel Ismail on 5/2/2017.
 */
public class Message {

    private final long id;
    private final Object content;

    public Message(long id) {
        this(id, null);

    }

    public Message(long id, Object content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
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
