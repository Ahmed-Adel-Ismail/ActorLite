package com.actors;

import android.support.annotation.RestrictTo;

/**
 * a class that holds global configuration across multiple instances for {@link ActorSystemInstance}
 * <p>
 * Created by Ahmed Adel Ismail on 3/5/2018.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ActorSystemGlobalConfiguration {
    private static boolean testingMode = false;


    public static boolean isTestingMode() {
        return testingMode;
    }

    public static void setTestingMode(boolean testingMode) {
        ActorSystemGlobalConfiguration.testingMode = testingMode;
    }
}
