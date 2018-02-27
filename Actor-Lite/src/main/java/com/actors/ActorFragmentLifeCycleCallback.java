package com.actors;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import static com.actors.RegistrationStage.ON_CREATE;
import static com.actors.RegistrationStage.ON_RESUME;
import static com.actors.RegistrationStage.ON_START;
import static com.actors.UnregistrationStage.ON_DESTROY;
import static com.actors.UnregistrationStage.ON_PAUSE;
import static com.actors.UnregistrationStage.ON_STOP;

/**
 * the Life-Cycle callback for Support Fragments
 * <p>
 * Created by Ahmed Adel Ismail on 1/2/2018.
 */
class ActorFragmentLifeCycleCallback extends FragmentManager.FragmentLifecycleCallbacks {

    private final ActorSystemConfiguration configuration;

    public ActorFragmentLifeCycleCallback(ActorSystemConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        if (ON_CREATE == configuration.registerActors) {
            registerActor(f);
        }
    }

    private void registerActor(Fragment fragment) {
        if (fragment instanceof Actor) {
            ActorSystem.register((Actor) fragment);
        }
    }

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        if (ON_START == configuration.registerActors) {
            registerActor(f);
        }
    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        if (ON_RESUME == configuration.registerActors) {
            registerActor(f);
        }
    }


    @Override
    public void onFragmentPaused(FragmentManager fm, Fragment f) {
        if (ON_PAUSE == configuration.unregisterActors) {
            unregisterActor(f);
        }
    }

    private void unregisterActor(Fragment fragment) {
        if (fragment instanceof Actor) {
            ActorSystem.unregister(fragment);
        }
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {

        if (ON_STOP == configuration.unregisterActors) {
            unregisterActor(f);
        }

        postponeActorIfRequired(f);
    }

    private void postponeActorIfRequired(Fragment fragment) {
        if (fragment instanceof Actor
                && configuration.postponeMailboxOnStop
                && ON_DESTROY == configuration.unregisterActors) {
            ActorSystem.postpone(fragment);
        }
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {

        if (ON_DESTROY == configuration.unregisterActors) {
            unregisterActor(f);
        }

        if (f.getActivity() == null || f.getActivity().isFinishing()) {
            ActorScheduler.cancel(f.getClass());
        }

    }

}
