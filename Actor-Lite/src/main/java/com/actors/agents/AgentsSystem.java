package com.actors.agents;

import com.actors.ActorSystemInstance;
import com.actors.annotations.ObserveByAgent;

import org.javatuples.Pair;

import java.lang.reflect.Field;

import static com.actors.agents.Agent.MSG_OBSERVE_ON_FIELD;

/**
 * Created by Ahmed Adel Ismail on 3/7/2018.
 */
public class AgentsSystem {


    private final ActorSystemInstance actorSystem;

    public AgentsSystem(ActorSystemInstance actorSystem) {
        this.actorSystem = actorSystem;
    }

    public void register(Object actor) {
        if (actor instanceof Agent) {
            return;
        }

        try {
            notifyAgentsForRegistration(actor);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void notifyAgentsForRegistration(Object actor) throws IllegalAccessException {
        Field[] fields = actor.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ObserveByAgent.class)) {
                notifyAgentsToObserveOnField(actor, field);
            }
        }
    }

    private void notifyAgentsToObserveOnField(Object actor, Field field) throws IllegalAccessException {
        ObserveByAgent observeByAgent = field.getAnnotation(ObserveByAgent.class);
        actorSystem.createMessage(MSG_OBSERVE_ON_FIELD)
                .withContent(Pair.with(observeByAgent.key(), field.get(actor)))
                .withReceiverActors(observeByAgent.agents())
                .send();
    }


    public void unregister(Object actor) {
        if (actor instanceof Agent) {
            return;
        }
    }

}
