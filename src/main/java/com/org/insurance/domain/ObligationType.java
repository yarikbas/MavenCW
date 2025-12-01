package com.org.insurance.domain;

import lombok.Getter;

@Getter
public enum ObligationType {
    AUTO("auto", true) {
        public Obligation create() { return new AutoObligation(); }
    },
    BUSINESS("business", true) {
        public Obligation create() { return new BusinessObligation(); }
    },
    HEALTH("health", true) {
        public Obligation create() { return new HealthObligation(); }
    },
    LIABILITY("liability", true) {
        public Obligation create() { return new LiabilityObligation(); }
    },
    LIFE("life", true) {
        public Obligation create() { return new LifeObligation(); }
    },
    PROPERTY("property", true) {
        public Obligation create() { return new PropertyObligation(); }
    },
    TRAVEL("travel", true) {
        public Obligation create() { return new TravelObligation(); }
    };

    private final String displayName;
    private final boolean visible;

    ObligationType(String displayName, boolean visible) {
        this.displayName = displayName;
        this.visible = visible;
    }

    public abstract Obligation create();

    public static ObligationType[] visibleValues() {
        ObligationType[] all = values();
        java.util.List<ObligationType> list = new java.util.ArrayList<>();
        for (int i = 0; i < all.length; i++) {
            if (all[i].isVisible()) list.add(all[i]);
        }
        return list.toArray(new ObligationType[list.size()]);
    }

    public static void printAll(ObligationType[] menu) {
        for (int i = 0; i < menu.length; i++) {
            System.out.printf("%d) %s%n", i + 1, menu[i].getDisplayName());
        }
    }

    /** Створити екземпляр за індексом у локальному меню. */
    public static Obligation createByIndex(ObligationType[] menu, int index) {
        if (index < 1 || index > menu.length) return null;
        return menu[index - 1].create();
    }
}
