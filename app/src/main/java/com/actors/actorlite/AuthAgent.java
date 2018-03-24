package com.actors.actorlite;

import com.actors.Message;
import com.actors.agents.Agent;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Ahmed Adel Ismail on 3/7/2018.
 */

public class AuthAgent extends Agent<String> {

    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";

    BehaviorSubject<String> userName = BehaviorSubject.create();
    BehaviorSubject<String> password = BehaviorSubject.create();

    public AuthAgent() {
        Observable.combineLatest(userName, password, new BiFunction<String, String, Boolean>() {
            @Override
            public Boolean apply(String s1, String s2) throws Exception {
                return s1 == null || s1.isEmpty()
                        && s2 == null || s2.isEmpty();
            }
        }).subscribe();

    }

    @Override
    protected void observeOn(String key, Observable<String> observable) {
        if (USER_NAME.equals(key)) {
            observable.share()
                    .observeOn(observeOnScheduler())
                    .subscribe(userName::onNext);
        } else if (PASSWORD.equals(key)) {
            observable.share()
                    .observeOn(observeOnScheduler())
                    .subscribe(password::onNext);
        }
    }
}
