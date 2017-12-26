package com.actors;

import android.app.Application;
import android.support.annotation.NonNull;

import com.chaining.Chain;

/**
 * a class that integrates the ActorLite library with the passed Application
 * <p>
 * Created by Ahmed Adel Ismail on 12/26/2017.
 */
public class ActorLite {

    @SuppressWarnings("ConstantConditions")
    public static void with(@NonNull Application application) {
        Chain.let(new ActorActivityLifeCycleCallbacks())
                .apply(application::registerActivityLifecycleCallbacks)
                .to(application instanceof Actor)
                .when(Boolean::booleanValue)
                .thenMap(booleanValue -> (Actor) application)
                .apply(ActorSystem::register);
    }


}
