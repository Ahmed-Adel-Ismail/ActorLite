package com.actors;

import android.support.annotation.IntDef;

/**
 * an enum that indicates the stages which the Actor-system should register it's Component
 * <p>
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */
@IntDef({RegistrationStage.ON_CREATE, RegistrationStage.ON_START, RegistrationStage.ON_RESUME})
public @interface RegistrationStage {

    int ON_CREATE = 1;
    int ON_START = 2;
    int ON_RESUME = 3;

}
