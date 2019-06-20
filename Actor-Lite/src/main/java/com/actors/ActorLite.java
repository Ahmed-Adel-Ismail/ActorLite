package com.actors;

import android.app.Application;
import android.support.annotation.NonNull;


/**
 * a class that integrates the ActorLite library with the passed Application
 * <p>
 * Created by Ahmed Adel Ismail on 12/26/2017.
 */
public class ActorLite {

    public static void with(final @NonNull Application application) {
        ActorLite.with(application, new ActorSystemConfiguration.Builder().build());
    }

    public static void with(@NonNull Application application, ActorSystemConfiguration configuration) {
        application.registerActivityLifecycleCallbacks(new ActorActivityLifeCycleCallbacks(configuration));
        if (application instanceof Actor) {
            ActorSystem.register((Actor) application);
        }
    }

}
