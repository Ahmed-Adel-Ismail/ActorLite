package com.actors.testing;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.Message;
import com.functional.curry.Tuples;

import org.javatuples.Pair;
import org.javatuples.Tuple;

import java.lang.reflect.Constructor;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

class ActorSystemTestRegistrations<R> implements BiConsumer<Class<?>, ActorTestBuilder<R>> {

    @Override
    public void accept(Class<?> targetActor, ActorTestBuilder<R> builder) throws Exception {
        registerCallbackActor(builder);
        registerMockedActors(builder);
        registerTargetActor(builder, targetActor);
    }

    private void registerCallbackActor(ActorTestBuilder<R> builder) throws Exception {
        builder.system.register(builder.callbackActor,
                builder.system.testScheduler, updateResultOnMessageReceived(builder));
    }

    private void registerMockedActors(ActorTestBuilder<R> builder) {
        Observable.fromIterable(builder.mockers)
                .blockingSubscribe(registerMocksForTesting(builder));
    }

    private void registerTargetActor(ActorTestBuilder<R> builder, final Class<?> targetActor) throws Exception {
        final Actor actor = createActor(targetActor);
        builder.system.register(actor, builder.system.testScheduler, new Consumer<Message>() {
            @Override
            public void accept(Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        });
    }

    @NonNull
    private Consumer<Message> updateResultOnMessageReceived(final ActorTestBuilder<R> testBuilder) {
        return new Consumer<Message>() {
            @Override
            public void accept(Message message) throws Exception {
                testBuilder.result.set(0, testBuilder.validationFunction.apply(message));
            }
        };
    }

    @NonNull
    private Consumer<Pair<Class<?>, Consumer<Message>>> registerMocksForTesting(final ActorTestBuilder<R> builder) {
        return new Consumer<Pair<Class<?>, Consumer<Message>>>() {
            @Override
            public void accept(Pair<Class<?>, Consumer<Message>> mocker) throws Exception {
                ;
                builder.system.register(mocker.getValue0(),
                        builder.system.testScheduler, mocker.getValue1());
            }
        };
    }

    @NonNull
    private Actor createActor(Class<?> targetActor) throws Exception {
        Constructor constructor = targetActor.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (Actor) constructor.newInstance();
    }
}
