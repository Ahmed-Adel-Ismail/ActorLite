package com.actors.annotations;

import com.actors.agents.Agent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.reactivex.Observable;
import io.reactivex.subjects.Subject;

/**
 * mark any {@link Observable} or {@link Subject} with this annotation so that an
 * {@link Agent} can observe on it and take
 * action when required
 * <p>
 * Created by Ahmed Adel Ismail on 3/7/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ObserveByAgent {

    String key();

    Class<? extends Agent>[] agents();
}
