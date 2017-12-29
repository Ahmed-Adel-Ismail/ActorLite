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

    private ActorSystem() {

    }

    /**
     * send an empty {@link Message} with the passed id
     *
     * @param messageId       the id of the {@link Message}
     * @param actorsAddresses the actor (or group of actors) that will receive this message
     */
    public static void send(int messageId, @NonNull Class<?>... actorsAddresses) {
        ActorSystemImpl.getInstance(null).send(messageId, actorsAddresses);
    }

    /**
     * send a {@link Message} to a mailbox
     *
     * @param message         the {@link Message} object
     * @param actorsAddresses the actor (or group of actors) that will receive this message
     */
    public static void send(final Message message, @NonNull Class<?>... actorsAddresses) {
        ActorSystemImpl.getInstance(null).send(message, actorsAddresses);
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
        ActorSystemImpl.getInstance(null).register(actor, observeOn, onMessageReceived);
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
        ActorSystemImpl.getInstance(null).register(actor);
    }

    /**
     * register a class to a mailbox
     *
     * @param actor          the class (Actor) that will handle messages
     * @param mailboxBuilder a function that takes a {@link MailboxBuilder} and generates a Mailbox
     */
    public static void register(@NonNull Object actor, @NonNull Consumer<MailboxBuilder> mailboxBuilder) {
        ActorSystemImpl.getInstance(null).register(actor, mailboxBuilder);
    }

    /**
     * unregister a class from it's mailbox, notice that this method will execute
     * {@link Subject#onComplete()} to notify the actor that it has completed it's task and
     * will not receive messages any more
     *
     * @param actor the Actor that was registered
     */
    public static void unregister(@NonNull Object actor) {
        ActorSystemImpl.getInstance(null).unregister(actor);
    }


}
