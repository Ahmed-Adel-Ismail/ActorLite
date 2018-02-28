package com.actors.annotations;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.Message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.reactivex.Scheduler;

/**
 * annotate your {@link Actor} implementer with this annotation so it will create (spawn) the passed
 * Actors along the life-cycle of your {@link Actor}
 * <p>
 * you can either
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Spawn {

    String NO_ACTORS_CLASSES = "NO_ACTORS_CLASSES";

    /**
     * mention the Classes of the Actors to Spawn
     *
     * @return an array of Actor classes
     */
    Class<? extends Actor>[] value() default NullActor.class;

    /**
     * mention the fully qualified class name for the desired actors to Spawn
     *
     * @return an array of fully qualified class names of Actors
     */
    String[] actorClasses() default NO_ACTORS_CLASSES;

    class NullActor implements Actor {
        @Override
        public void onMessageReceived(Message message) {

        }

        @NonNull
        @Override
        public Scheduler observeOnScheduler() {
            return null;
        }
    }


}
