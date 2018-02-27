package com.actors;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * an enum that indicates the stages which the Actor-system should unregister it's Component
 * <p>
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@IntDef({UnregistrationStage.ON_PAUSE, UnregistrationStage.ON_STOP, UnregistrationStage.ON_DESTROY})
public @interface UnregistrationStage {

    int ON_PAUSE = 1;
    int ON_STOP = 2;
    int ON_DESTROY = 3;

}
