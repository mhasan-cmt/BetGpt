package com.ubetgpt.betgpt.enums;

public class SubscriptionPackage {
    public static final String MONTHLY = "basic";
    public static final String YEARLY = "complete";

    enum SubscriptionPackageEnum{
        MONTHLY("basic"),
        YEARLY("complete");

        private final String value;

        SubscriptionPackageEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }
}
