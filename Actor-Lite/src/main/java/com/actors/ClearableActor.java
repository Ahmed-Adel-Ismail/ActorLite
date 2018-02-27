package com.actors;

/**
 * implement this interface if you want to take action when this {@link Actor} is unregistered
 * <p>
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */
public interface ClearableActor extends Actor {

    /**
     * a function that is invoked when this {@link Actor} is unregistered
     */
    void onUnregister();
}
