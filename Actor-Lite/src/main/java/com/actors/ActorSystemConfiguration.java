package com.actors;

/**
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */

public class ActorSystemConfiguration {

    @RegistrationStage
    final int activitiesRegistration;
    @RegistrationStage
    final int fragmentRegistration;
    @UnregistrationStage
    final int activitiesUnregistration;
    @UnregistrationStage
    final int fragmentUnregistration;
    final boolean postponeMailboxDisabled;

    private ActorSystemConfiguration(Builder builder) {
        activitiesRegistration = builder.activitiesRegistration;
        fragmentRegistration = builder.fragmentRegistration;
        activitiesUnregistration = builder.activitiesUnregistration;
        fragmentUnregistration = builder.fragmentUnregistration;
        postponeMailboxDisabled = builder.postponeMailboxDisabled;
    }


    /**
     * {@code ActorSystemConfiguration} builder static inner class.
     */
    public static final class Builder {
        private int activitiesRegistration = RegistrationStage.ON_START;
        private int fragmentRegistration = RegistrationStage.ON_START;
        private int activitiesUnregistration = UnregistrationStage.ON_STOP;
        private int fragmentUnregistration = UnregistrationStage.ON_STOP;
        private boolean postponeMailboxDisabled = false;

        public Builder() {
        }

        /**
         * Sets the {@code activitiesRegistration} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param activitiesRegistration the {@code activitiesRegistration} to set
         * @return a reference to this Builder
         */
        public Builder activitiesRegistration(@RegistrationStage int activitiesRegistration) {
            this.activitiesRegistration = activitiesRegistration;
            return this;
        }

        /**
         * Sets the {@code fragmentRegistration} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param fragmentRegistration the {@code fragmentRegistration} to set
         * @return a reference to this Builder
         */
        public Builder fragmentRegistration(@RegistrationStage int fragmentRegistration) {
            this.fragmentRegistration = fragmentRegistration;
            return this;
        }

        /**
         * Sets the {@code activitiesUnregistration} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param activitiesUnregistration the {@code activitiesUnregistration} to set
         * @return a reference to this Builder
         */
        public Builder activitiesUnregistration(@UnregistrationStage int activitiesUnregistration) {
            this.activitiesUnregistration = activitiesUnregistration;
            return this;
        }

        /**
         * Sets the {@code fragmentUnregistration} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param fragmentUnregistration the {@code fragmentUnregistration} to set
         * @return a reference to this Builder
         */
        public Builder fragmentUnregistration(@UnregistrationStage int fragmentUnregistration) {
            this.fragmentUnregistration = fragmentUnregistration;
            return this;
        }

        /**
         * Sets the {@code postponeMailboxDisabled} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param postponeMailboxDisabled the {@code postponeMailboxDisabled} to set
         * @return a reference to this Builder
         */
        public Builder postponeMailboxDisabled(boolean postponeMailboxDisabled) {
            this.postponeMailboxDisabled = postponeMailboxDisabled;
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
