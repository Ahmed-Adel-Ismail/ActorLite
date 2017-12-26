package com.actors;

import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;

/**
 * a {@link Fragment} that registers and unregisters itself from the {@link ActorSystem} and
 * {@link ActorScheduler} ... you can extend this Class or invoke the register/unregister methods
 * manually in the proper life cycle methods
 * <p>
 * Created by Ahmed Adel Ismail on 12/26/2017.
 */
public abstract class ActorFragment extends Fragment implements Actor {

    @CallSuper
    @Override
    public void onStart() {
        super.onStart();
        ActorSystem.register(this);
    }

    @CallSuper
    @Override
    public void onStop() {
        ActorSystem.unregister(this);
        super.onStop();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        if (getActivity() == null || getActivity().isFinishing()) {
            ActorScheduler.cancel(getClass());
        }
        super.onDestroy();
    }
}
