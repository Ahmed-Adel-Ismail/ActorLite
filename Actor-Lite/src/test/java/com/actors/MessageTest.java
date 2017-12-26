package com.actors;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ahmed Adel Ismail on 5/15/2017.
 */
public class MessageTest
{

    @Test
    public void createMessageWithIdOnly() throws Exception {
        Message message = new Message(1);
        assertTrue(message.getId() == 1 && message.getContent() == null);
    }

    @Test
    public void createMessageWithIdAndContent() throws Exception {
        Message message = new Message(1, new Object());
        assertTrue(message.getId() == 1 && message.getContent() != null);

    }
}