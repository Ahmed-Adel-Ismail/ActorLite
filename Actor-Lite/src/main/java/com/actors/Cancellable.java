package com.actors;

import io.reactivex.disposables.Disposable;


/**
 * a class that handles cancelling a scheduled {@link Message}
 * <p>
 * Created by Ahmed Adel Ismail on 5/26/2017.
 */
public class Cancellable {

    private final Class<?> actorAddress;
    private final int id;

    Cancellable(Class<?> actorAddress, int id) {
        this.id = id;
        this.actorAddress = actorAddress;
    }

    public Disposable cancel() {
        synchronized (ActorScheduler.lock) {
            return ActorScheduler.getNonNullDisposableGroup(actorAddress)
                    .remove(id);
        }
    }


}
