package com.actors;

import android.app.Application;
import android.support.annotation.NonNull;

import com.chaining.Chain;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * a class that integrates the ActorLite library with the passed Application
 * <p>
 * Created by Ahmed Adel Ismail on 12/26/2017.
 */
public class ActorLite {

    public static void with(final @NonNull Application application) {
        ActorLite.with(application, new ActorSystemConfiguration.Builder().build());
    }

    public static void with(final @NonNull Application application,
                            ActorSystemConfiguration configuration) {
        Chain.let(new ActorActivityLifeCycleCallbacks(configuration))
                .apply(registerActivityLifeCycleCallbacks(application))
                .to(application instanceof Actor)
                .when(isTrue())
                .thenMap(toActor(application))
                .apply(registerToActorSystem());
    }

    @NonNull
    private static Consumer<ActorActivityLifeCycleCallbacks> registerActivityLifeCycleCallbacks(
            @NonNull final Application application) {
        return new Consumer<ActorActivityLifeCycleCallbacks>() {
            @Override
            public void accept(ActorActivityLifeCycleCallbacks actorActivityLifeCycleCallbacks) {
                application.registerActivityLifecycleCallbacks(actorActivityLifeCycleCallbacks);
            }
        };
    }

    @NonNull
    private static Predicate<Boolean> isTrue() {
        return new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean aBoolean) throws Exception {
                return aBoolean;
            }
        };
    }

    @NonNull
    private static Function<Boolean, Actor> toActor(@NonNull final Application application) {
        return new Function<Boolean, Actor>() {
            @Override
            public Actor apply(Boolean aBoolean) throws Exception {
                return (Actor) application;
            }
        };
    }

    @NonNull
    private static Consumer<Actor> registerToActorSystem() {
        return new Consumer<Actor>() {
            @Override
            public void accept(Actor actor) throws Exception {
                ActorSystem.register(actor);
            }
        };
    }

}
