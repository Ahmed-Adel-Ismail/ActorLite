package com.actors;

import android.support.annotation.NonNull;

import com.actors.annotations.Spawn;
import com.chaining.Chain;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * a class that is responsible for tracking Spawned actors, registering and un-registering them
 * when the Actor created them registers and un-registers
 * <p>
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */
class ActorsInjector {

    private final ConcurrentMap<Actor, Object> injectedActorsOwners = new ConcurrentHashMap<>();
    private final ActorSystemInstance actorSystem;

    ActorsInjector(ActorSystemInstance actorSystem) {
        this.actorSystem = actorSystem;
    }

    void injectFor(final Object actor) {
        try {
            invokeInjectFor(actor);
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

    private void invokeInjectFor(Object actor) {
        Chain.optional(actor)
                .map(toClass())
                .when(isAnnotatedWithSpawn())
                .thenMap(toSpawnAnnotation())
                .map(toValuesArray())
                .map(toValuesList())
                .defaultIfEmpty(new ArrayList<Class<? extends Actor>>())
                .flatMap(toActorClassObservable())
                .filter(byNonRegisteredActorClasses())
                .distinct()
                .map(toActorInstance())
                .doOnNext(registerToActorSystem())
                .doOnError(printStackTrace())
                .blockingForEach(addToInjectedActorsOwners(actor));
    }

    @NonNull
    private Function<Object, Class<?>> toClass() {
        return new Function<Object, Class<?>>() {
            @Override
            public Class<?> apply(Object o) throws Exception {
                return o.getClass();
            }
        };
    }

    @NonNull
    private Predicate<Class<?>> isAnnotatedWithSpawn() {
        return new Predicate<Class<?>>() {
            @Override
            public boolean test(Class<?> clazz) throws Exception {
                return clazz.isAnnotationPresent(Spawn.class);
            }
        };
    }

    @NonNull
    private Function<Class<?>, Spawn> toSpawnAnnotation() {
        return new Function<Class<?>, Spawn>() {
            @Override
            public Spawn apply(Class<?> aClass) throws Exception {
                return aClass.getAnnotation(Spawn.class);
            }
        };
    }

    @NonNull
    private Function<Spawn, Class<? extends Actor>[]> toValuesArray() {
        return new Function<Spawn, Class<? extends Actor>[]>() {
            @Override
            public Class<? extends Actor>[] apply(Spawn spawn) throws Exception {
                return spawn.value();
            }
        };
    }

    @NonNull
    private Function<Class<? extends Actor>[], List<Class<? extends Actor>>> toValuesList() {
        return new Function<Class<? extends Actor>[], List<Class<? extends Actor>>>() {
            @Override
            public List<Class<? extends Actor>> apply(Class<? extends Actor>[] classes) throws Exception {
                return Arrays.asList(classes);
            }
        };
    }

    @NonNull
    private Function<List<Class<? extends Actor>>, Observable<Class<? extends Actor>>> toActorClassObservable() {
        return new Function<List<Class<? extends Actor>>, Observable<Class<? extends Actor>>>() {
            @Override
            public Observable<Class<? extends Actor>> apply(List<Class<? extends Actor>> classes) {
                return Observable.fromIterable(classes);
            }
        };
    }

    @NonNull
    private Predicate<Class<? extends Actor>> byNonRegisteredActorClasses() {
        return new Predicate<Class<? extends Actor>>() {
            @Override
            public boolean test(Class<? extends Actor> aClass) throws Exception {
                return !actorSystem.getMailboxes().containsKey(aClass).blockingGet();
            }
        };
    }

    @NonNull
    private Function<Class<? extends Actor>, Actor> toActorInstance() {
        return new Function<Class<? extends Actor>, Actor>() {
            @Override
            public Actor apply(Class<? extends Actor> aClass) throws Exception {
                Constructor constructor = aClass.getConstructor();
                constructor.setAccessible(true);
                return (Actor) constructor.newInstance();
            }
        };
    }

    @NonNull
    private Consumer<Actor> registerToActorSystem() {
        return new Consumer<Actor>() {
            @Override
            public void accept(Actor actor) throws Exception {
                actorSystem.register(actor);
            }
        };
    }

    @NonNull
    private Consumer<Throwable> printStackTrace() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        };
    }

    @NonNull
    private Consumer<Actor> addToInjectedActorsOwners(final Object actor) {
        return new Consumer<Actor>() {
            @Override
            public void accept(Actor injectedActor) throws Exception {
                injectedActorsOwners.put(injectedActor, actor);
            }
        };
    }

    void clearFor(final Object actor) {
        try {
            invokeClearFor(actor);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void invokeClearFor(Object actor) {
        Observable.fromIterable(injectedActorsOwners.entrySet())
                .filter(byOwner(actor))
                .map(toInjectedActor())
                .doOnNext(unregisterInjectedActor())
                .blockingForEach(removeFromInjectedActorsOwners());
    }

    @NonNull
    private Predicate<Map.Entry<Actor, Object>> byOwner(final Object actor) {
        return new Predicate<Map.Entry<Actor, Object>>() {
            @Override
            public boolean test(Map.Entry<Actor, Object> injectedActorOwner) throws Exception {
                return actor.equals(injectedActorOwner.getValue());
            }
        };
    }

    @NonNull
    private Function<Map.Entry<Actor, Object>, Actor> toInjectedActor() {
        return new Function<Map.Entry<Actor, Object>, Actor>() {
            @Override
            public Actor apply(Map.Entry<Actor, Object> injectedActorOwner) throws Exception {
                return injectedActorOwner.getKey();
            }
        };
    }

    @NonNull
    private Consumer<Actor> unregisterInjectedActor() {
        return new Consumer<Actor>() {
            @Override
            public void accept(Actor actor) throws Exception {
                actorSystem.unregister(actor);
            }
        };
    }

    @NonNull
    private Consumer<Actor> removeFromInjectedActorsOwners() {
        return new Consumer<Actor>() {
            @Override
            public void accept(Actor actor) throws Exception {
                injectedActorsOwners.remove(actor);
            }
        };
    }

    Map<Actor, Object> getInjectedActorsOwners() {
        return injectedActorsOwners;
    }
}
