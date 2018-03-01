package com.actors;

/**
 * a class that handles sending a message
 * <p>
 * Created by Ahmed Adel Ismail on 3/1/2018.
 */
public class MessageSender {

    private final ActorSystemInstance system;
    private final Message message;
    private final Class<?>[] actors;
    private final String[] actorsClassesNames;

    MessageSender(ActorSystemInstance system, Message message, Class<?>[] actors) {
        this.system = system;
        this.message = message;
        this.actors = actors;
        this.actorsClassesNames = null;
    }

    MessageSender(ActorSystemInstance system, Message message, String[] actorsClassesNames) {
        this.system = system;
        this.message = message;
        this.actorsClassesNames = actorsClassesNames;
        this.actors = null;

    }

    /**
     * send the Message to the declared actors
     */
    public void send() {
        if (actors != null) {
            system.send(message, actors);
        } else if (actorsClassesNames != null) {
            system.send(message, actorsClassesNames);
        } else {
            system.send(message, (Class<?>) null); // crash
        }
    }


}
