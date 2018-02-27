package com.actors.annotations;

import com.actors.Actor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotate your {@link Actor} implementer with this annotation so it will create (spawn) the passed
 * Actors along the life-cycle of your {@link Actor}
 * <p>
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Spawn {

    Class<? extends Actor>[] value();

}
