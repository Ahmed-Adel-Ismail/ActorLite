package com.actors.testing;

import android.support.annotation.NonNull;

import com.actors.ActorSystemConfiguration;
import com.actors.ActorSystemInstance;
import com.actors.MailboxBuilder;
import com.actors.Message;

import java.util.LinkedHashSet;
import java.util.Set;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.TestScheduler;

/**
 * an {@link ActorSystemInstance} that is used in Unit-Testing
 * <p>
 * Created by Ahmed Adel Ismail on 3/3/2018.
 */
@SuppressWarnings("deprecation")
class ActorSystemTestInstance extends ActorSystemInstance {

    private final long id = (long) (Math.random() * 10000);
    final TestScheduler testScheduler = new TestScheduler();
    private final Set<Class<?>> testActors = new LinkedHashSet<>();


    ActorSystemTestInstance(ActorSystemConfiguration configuration) {
        super(configuration);
        instances.put(id,this);
    }


    @Override
    protected void doRegister(Object actor, Consumer<MailboxBuilder> mailboxBuilderFunction) {
        if (actor instanceof Class) {
            invokeDoRegisterForClass((Class) actor, mailboxBuilderFunction);
        } else {
            invokeDoRegisterForObject(actor, mailboxBuilderFunction);
        }
    }

    private void invokeDoRegisterForClass(Class actor, Consumer<MailboxBuilder> mailboxBuilderFunction) {
        if (!testActors.contains(actor)) {
            super.doRegister(actor, mailboxBuilderFunction);
        }
    }

    private void invokeDoRegisterForObject(Object actor, Consumer<MailboxBuilder> mailboxBuilderFunction) {
        if (!testActors.contains(actor.getClass())) {
            super.doRegister(actor, mailboxBuilderFunction);
        }
    }

    @Override
    public void register(@NonNull Object actor, @NonNull Scheduler observeOn, @NonNull Consumer<Message> onMessageReceived) {

        super.register(actor, defaultMailboxBuilder(actor, null, onMessageReceived));

        if (actor instanceof Class) {
            testActors.add((Class) actor);
        } else {
            testActors.add(actor.getClass());
        }
    }

    @NonNull
    @Override
    protected Consumer<MailboxBuilder> defaultMailboxBuilder(
            Object actor, Scheduler observeOn, Consumer<Message> onMessageReceived) {
        return super.defaultMailboxBuilder(actor, testScheduler, onMessageReceived);
    }

    void clear() {
        mailboxes.clear();
        actorsDisposables.clear();
        actorsInjector.clear();
        instances.remove(id);
    }
}
