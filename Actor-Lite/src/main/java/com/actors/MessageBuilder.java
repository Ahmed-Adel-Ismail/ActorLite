package com.actors;

/**
 * a class that builds a {@link Message}
 * <p>
 * Created by Ahmed Adel Ismail on 3/1/2018.
 */
public class MessageBuilder {

    private final ActorSystemInstance system;
    private final int id;
    private Object content;
    private Class<?> replyToActor;

    MessageBuilder(ActorSystemInstance system, int id) {
        this.system = system;
        this.id = id;
    }

    /**
     * Sets the {@code content} and returns a reference to this {@link MessageBuilder} so
     * that the methods can be chained together.
     *
     * @param content the {@code content} to set
     * @return a reference to this Builder
     */
    public MessageBuilder withContent(Object content) {
        this.content = content;
        return this;
    }

    /**
     * Sets the {@code replyToActor} and returns a reference to this {@link MessageBuilder} so
     * that the methods can be chained together.
     *
     * @param replyToActor the {@code replyToActor} to set
     * @return a reference to this Builder
     */
    public MessageBuilder withReplyToActor(Class<?> replyToActor) {
        this.replyToActor = replyToActor;
        return this;
    }

    /**
     * set the receiver Actors
     *
     * @param actors the Actors that will receive the {@link Message}
     * @return a {@link MessageSender} to handle sending the message
     */
    public MessageSender withReceiverActors(Class<?>... actors) {
        return new MessageSender(system, new Message(id, content, replyToActor), actors);
    }

    /**
     * set the receiver Actors fully qualified class names
     *
     * @param actorsClassNames the Actors that will receive the {@link Message}
     * @return a {@link MessageSender} to handle sending the message
     */
    public MessageSender withReceiverActors(String... actorsClassNames) {
        return new MessageSender(system, new Message(id, content, replyToActor), actorsClassNames);
    }
}
