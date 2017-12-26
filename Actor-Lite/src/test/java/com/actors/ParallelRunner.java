package com.actors;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class ParallelRunner extends BlockJUnit4ClassRunner
{

    public ParallelRunner(Class<?> klass) throws InitializationError {
        super(klass);
        setScheduler(new ParallelScheduler());
    }
}
