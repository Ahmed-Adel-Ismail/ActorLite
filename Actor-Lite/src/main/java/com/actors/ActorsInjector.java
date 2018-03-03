package com.actors;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import com.actors.annotations.Spawn;
import com.chaining.Chain;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private final ConcurrentMap<Actor, Set<Object>> injectedActorsOwners = new ConcurrentHashMap<>();
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
                .map(toValuesAndActorNamesClassList())
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
    private Function<Spawn, List<Class<? extends Actor>>> toValuesAndActorNamesClassList() {
        return new Function<Spawn, List<Class<? extends Actor>>>() {
            @Override
            public List<Class<? extends Actor>> apply(Spawn spawn) throws Exception {

                Class<? extends Actor>[] classes = spawn.value();
                String[] classesNames = spawn.actorClasses();
                List<Class<? extends Actor>> spawningClasses = new LinkedList<>();

                if (Spawn.NullActor.class != classes[0]) {
                    spawningClasses.addAll(Arrays.asList(classes));
                }

                if (!Spawn.NO_ACTORS_CLASSES.equals(classesNames[0])) {
                    appendActorClassFromClassName(classesNames, spawningClasses);
                }

                return spawningClasses;
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
                Constructor constructor = aClass.getDeclaredConstructor();
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
                Set<Object> owners = getOrCreateOwners(injectedActor);
                owners.add(actor);
                injectedActorsOwners.put(injectedActor, owners);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private void appendActorClassFromClassName(String[] classesNames,
                                               List<Class<? extends Actor>> spawningClasses) {
        for (String className : classesNames) {
            try {
                spawningClasses.add((Class<? extends Actor>) Class.forName(className));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    private Set<Object> getOrCreateOwners(Actor injectedActor) {
        Set<Object> owners = injectedActorsOwners.get(injectedActor);
        if (owners == null) {
            owners = new LinkedHashSet<>();
        }
        return owners;
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
                .filter(byNonRegisteredOwners(actor))
                .map(toInjectedActor())
                .doOnNext(removeFromInjectedActorsOwners())
                .doOnNext(unregisterInjectedActor())
                .blockingSubscribe();
    }

    @NonNull
    private Predicate<Map.Entry<Actor, Set<Object>>> byNonRegisteredOwners(final Object actor) {
        return new Predicate<Map.Entry<Actor, Set<Object>>>() {
            @Override
            public boolean test(Map.Entry<Actor, Set<Object>> injectedActorsOwners) throws Exception {
                Set<Object> owners = injectedActorsOwners.getValue();
                if(owners != null) {
                    owners.remove(actor);
                }
                return owners == null || owners.isEmpty();
            }
        };
    }

    @NonNull
    private Function<Map.Entry<Actor, Set<Object>>, Actor> toInjectedActor() {
        return new Function<Map.Entry<Actor, Set<Object>>, Actor>() {
            @Override
            public Actor apply(Map.Entry<Actor, Set<Object>> injectedActorOwner) throws Exception {
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

    @RestrictTo(RestrictTo.Scope.TESTS)
    Map<Actor, Set<Object>> getInjectedActorsOwners() {
        return injectedActorsOwners;
    }
}
