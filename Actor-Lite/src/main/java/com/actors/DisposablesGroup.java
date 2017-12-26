package com.actors;

import android.support.annotation.NonNull;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Ahmed Adel Ismail on 5/26/2017.
 */

class DisposablesGroup extends HashMap<Long, Disposable>
{

    /**
     * remove a {@link Disposable} if found, this method invokes {@link Disposable#dispose()}
     * before removing
     *
     * @param key the key for that {@link Disposable}
     * @return the removed {@link Disposable}
     */
    @Override
    public Disposable remove(Object key) {
        Disposable disposable = get(key);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        Observable.fromIterable(keySet()).blockingForEach(remove());
        super.clear();
    }

    @NonNull
    private Consumer<Long> remove() {
        return new Consumer<Long>()
        {
            @Override
            public void accept(@NonNull Long key) throws Exception {
                remove(key);
            }
        };
    }
}
