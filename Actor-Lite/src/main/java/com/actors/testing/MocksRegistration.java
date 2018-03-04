package com.actors.testing;

import android.support.annotation.NonNull;

import com.actors.Message;

import org.javatuples.Pair;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

@SuppressWarnings("unchecked")
class MocksRegistration implements Consumer<ActorTestBuilder> {

    @Override
    public void accept(ActorTestBuilder builder) {
        Observable.fromIterable(builder.mocks)
                .blockingSubscribe(registerMocksForTesting(builder));
    }

    @NonNull
    private Consumer<Pair<Class<?>, Consumer<Message>>> registerMocksForTesting(
            final ActorTestBuilder builder) {
        return new Consumer<Pair<Class<?>, Consumer<Message>>>() {
            @Override
            public void accept(Pair<Class<?>, Consumer<Message>> mock) throws Exception {
                builder.system.register(mock.getValue0(),
                        builder.system.testScheduler, mock.getValue1());
            }
        };
    }
}
