package com.actors;

/**
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */

public class ActorSystemConfiguration {

    final boolean spawnActors;
    final boolean postponeMailboxOnStop;
    @RegistrationStage
    final int registerActors;
    @UnregistrationStage
    final int unregisterActors;

    private ActorSystemConfiguration(Builder builder) {
        spawnActors = builder.spawnActors;
        postponeMailboxOnStop = builder.postponeMailboxOnStop;
        registerActors = builder.registerActors;
        unregisterActors = builder.unregisterActors;
    }


    /**
     * {@code ActorSystemConfiguration} builder static inner class.
     */
    public static final class Builder {
        private boolean spawnActors = true;
        private int registerActors = RegistrationStage.ON_START;
        private int unregisterActors = UnregistrationStage.ON_STOP;
        private boolean postponeMailboxOnStop = true;

        public Builder() {
        }

        /**
         * Sets the {@code spawnActors} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param spawnActors the {@code spawnActors} to set
         * @return a reference to this Builder
         */
        public Builder spawnActors(boolean spawnActors) {
            this.spawnActors = spawnActors;
            return this;
        }

        /**
         * Sets the {@code postponeMailboxOnStop} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param postponeMailboxOnStop the {@code postponeMailboxOnStop} to set
         * @return a reference to this Builder
         */
        public Builder postponeMailboxOnStop(boolean postponeMailboxOnStop) {
            this.postponeMailboxOnStop = postponeMailboxOnStop;
            return this;
        }

        /**
         * Sets the {@code registerActors} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param registerActors the {@code registerActors} to set
         * @return a reference to this Builder
         */
        public Builder registerActors(int registerActors) {
            this.registerActors = registerActors;
            return this;
        }

        /**
         * Sets the {@code unregisterActors} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param unregisterActors the {@code unregisterActors} to set
         * @return a reference to this Builder
         */
        public Builder unregisterActors(int unregisterActors) {
            this.unregisterActors = unregisterActors;
            return this;
        }

        /**
         * Returns a {@code ActorSystemConfiguration} built from the parameters previously set.
         *
         * @return a {@code ActorSystemConfiguration} built with parameters of this {@code ActorSystemConfiguration.Builder}
         */
        public ActorSystemConfiguration build() {
            return new ActorSystemConfiguration(this);
        }
    }
}
