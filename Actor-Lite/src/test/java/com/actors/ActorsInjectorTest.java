package com.actors;

import android.support.annotation.NonNull;

import com.actors.annotations.Spawn;
import com.chaining.Lazy;

import org.junit.After;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.TestScheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */
public class ActorsInjectorTest {


    private static final int MSG_ONE_ID = 1;
    private static final int MSG_TWO_ID = 2;

    @After
    public void reset() {
        Cache.instance.call().reset();
    }

    @Test
    public void injectForActorThenSpawnAllActors() {
        ActorSystemInstance system = ActorSystemInstance.getInstance("1", configuration());
        system.register(new OwnerOne());
        system.send(MSG_ONE_ID, ActorOne.class);
        system.send(MSG_TWO_ID, ActorTwo.class);

        Cache.instance.call().testScheduler.triggerActions();

        assertTrue(MSG_ONE_ID == Cache.instance.call().actorOneMessage[0].getId()
                && MSG_TWO_ID == Cache.instance.call().actorTwoMessage[0].getId());

    }

    @NonNull
    private ActorSystemConfiguration configuration() {
        return new ActorSystemConfiguration.Builder()
                .spawnActors(true)
                .build();
    }

    @Test
    public void injectForMultipleActorsThenSpawnTheSameInstance() {
        ActorSystemInstance system = ActorSystemInstance.getInstance("2", configuration());

        system.register(new OwnerOne());
        system.register(new OwnerTwo());

        Cache.instance.call().testScheduler.triggerActions();

        assertEquals(1, Cache.instance.call().actorOneInstancesCount[0]);

    }

    @Test
    public void clearForActorThenUnregisterAllSpawnedActors() {

        ActorSystemInstance system = ActorSystemInstance.getInstance("3", configuration());

        OwnerOne owner = new OwnerOne();
        system.register(owner);
        system.unregister(owner);

        Cache.instance.call().testScheduler.triggerActions();

        system.send(MSG_ONE_ID, ActorOne.class);
        system.send(MSG_TWO_ID, ActorTwo.class);

        Cache.instance.call().testScheduler.triggerActions();

        assertTrue(null == Cache.instance.call().actorOneMessage[0]
                && null == Cache.instance.call().actorTwoMessage[0]);

    }

    @Test
    public void clearForMultipleActorsThenDoNotUnregisterSpawnedActorWhenTheLastActorIsNotUnregistered() {

        ActorSystemInstance system = ActorSystemInstance.getInstance("5", configuration());

        OwnerOne owner = new OwnerOne();
        OwnerTwo ownerTwo = new OwnerTwo();

        system.register(ownerTwo);
        system.register(owner);
        system.unregister(owner);

        assertTrue(system.getActorsInjector().getInjectedActorsOwners().size() == 1 &&
                containsOwner(system.getActorsInjector().getInjectedActorsOwners(), ownerTwo));

    }

    private boolean containsOwner(Map<Actor, Set<Object>> injectedActorsOwners, Object owner) {
        for (Set<Object> owners : injectedActorsOwners.values()) {
            if (owners.contains(owner)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void clearForMultipleActorsThenUnregisterSpawnedActorWhenTheLastActorUnregisters() {

        ActorSystemInstance system = ActorSystemInstance.getInstance("5", configuration());

        OwnerTwo owner = new OwnerTwo();
        OwnerTwo ownerTwo = new OwnerTwo();


        system.register(ownerTwo);
        system.register(owner);
        system.unregister(owner);
        system.unregister(ownerTwo);


        assertTrue(system.getActorsInjector().getInjectedActorsOwners().isEmpty());

    }

}

class Cache {

    static Lazy<Cache> instance = Lazy.defer(new Callable<Cache>() {
        @Override
        public Cache call() throws Exception {
            return new Cache();
        }
    });
    final TestScheduler testScheduler = new TestScheduler();
    final int[] actorOneInstancesCount = {0};
    final Message[] actorOneMessage = {null};
    final Message[] actorTwoMessage = {null};

    private Cache() {
    }

    void reset() {
        actorOneInstancesCount[0] = 0;
        actorOneMessage[0] = null;
        actorTwoMessage[0] = null;
        testScheduler.triggerActions();
    }


}

@Spawn({ActorOne.class, ActorTwo.class})
class OwnerOne implements Actor {

    @Override
    public void onMessageReceived(Message message) {
        // do nothing
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Cache.instance.call().testScheduler;
    }
}

@Spawn(ActorOne.class)
class OwnerTwo implements Actor {

    @Override
    public void onMessageReceived(Message message) {
        // do nothing
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Cache.instance.call().testScheduler;
    }
}

class ActorOne implements Actor {

    public ActorOne() {
        Cache.instance.call().actorOneInstancesCount[0]++;
    }

    @Override
    public void onMessageReceived(Message message) {
        Cache.instance.call().actorOneMessage[0] = message;
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Cache.instance.call().testScheduler;
    }
}

class ActorTwo implements Actor {

    public ActorTwo() {

    }

    @Override
    public void onMessageReceived(Message message) {
        Cache.instance.call().actorTwoMessage[0] = message;
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Cache.instance.call().testScheduler;
    }
}


