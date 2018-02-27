package com.actors;

/**
 * a Message to be sent through the {@link ActorSystem#send(Message, Class[])}
 * <p>
 * Created by Ahmed Adel Ismail on 5/2/2017.
 */
public class Message {

    private final int id;
    private final Object content;
    private final Class<?> replyToActor;

    public Message(int id) {
        this(id, null);

    }

    public Message(int id, Object content) {
        this(id, content, null);
    }

    public Message(int id, Object content, Class<?> replyToActor) {
        this.id = id;
        this.content = content;
        this.replyToActor = replyToActor;
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public <T> T getContent() {
        return (T) content;
    }

    public Class<?> getReplyToActor() {
        return replyToActor;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content=" + content +
                ", replyToActor=" + replyToActor +
                '}';
    }
}
