package com.actors;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.Subject;

/**
 * a class that handles the Actor System for the application
 * <p>
 * Created by Ahmed Adel Ismail on 5/2/2017.
 */
@SuppressWarnings("deprecation")
public class ActorSystem {

    private static final ActorSystemImpl implementation = ActorSystemImpl.getInstance(null);

    private ActorSystem() {

    }

    /**
     * send an empty {@link Message} with the passed id
     *
     * @param messageId       the id of the {@link Message}
     * @param actorsAddresses the actor (or group of actors) that will receive this message
     */
    public static void send(int messageId, @NonNull Class<?>... actorsAddresses) {
        implementation.send(messageId, actorsAddresses);
    }

    /**
     * send a {@link Message} to a mailbox
     *
     * @param message         the {@link Message} object
     * @param actorsAddresses the actor (or group of actors) that will receive this message
     */
    public static void send(final Message message, @NonNull Class<?>... actorsAddresses) {
        implementation.send(message, actorsAddresses);
    }


    /**
     * register a class to a mailbox but with the default configurations
     *
     * @param actor             the Actor that will handle messages
     * @param observeOn         the {@link Scheduler} that will host the received messages
     * @param onMessageReceived the {@link Consumer} function that will be invoked
     *                          when a message is received
     */
    public static void register(@NonNull Object actor,
                                @NonNull final Scheduler observeOn,
                                @NonNull final Consumer<Message> onMessageReceived) {
        implementation.register(actor, observeOn, onMessageReceived);
    }


    /**
     * register a class to a mailbox but with default configurations and will invoke
     * {@link Actor#onMessageReceived(Message)} when the passed {@link Actor} receives a message,
     * the address of this {@link Actor}'s Mailbox will be the {@link Class} of the passed object
     *
     * @param actor the {@link Actor} that will receive messages and it's {@link Class}
     *              will be the address of it's mailbox
     */
    public static void register(@NonNull final Actor actor) {
        implementation.register(actor);
    }

    /**
     * register a class to a mailbox
     *
     * @param actor          the class (Actor) that will handle messages
     * @param mailboxBuilder a function that takes a {@link MailboxBuilder} and generates a Mailbox
     */
    public static void register(@NonNull Object actor, @NonNull Consumer<MailboxBuilder> mailboxBuilder) {
        implementation.register(actor, mailboxBuilder);
    }

    /**
     * postpone the current Actor, this means that this actor will either register again, or
     * unregister itself in a later point ... this function will cause any coming message to be
     * queued until the Actor Registers itself again, and then it will re-send the queued messages
     * <p>
     * if the Actor unregistered itself, the pending messages will be cancelled
     *
     * @param actor the Actor to be postponed
     */
    public static void postpone(@NonNull Object actor) {
        implementation.postpone(actor);
    }

    /**
     * unregister a class from it's mailbox, notice that this method will execute
     * {@link Subject#onComplete()} to notify the actor that it has completed it's task and
     * will not receive messages any more
     *
     * @param actor the Actor that was registered
     */
    public static void unregister(@NonNull Object actor) {
        implementation.unregister(actor);
    }


}
