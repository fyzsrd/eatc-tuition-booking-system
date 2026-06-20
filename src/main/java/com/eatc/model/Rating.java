package com.eatc.model;

public enum Rating {
    VERY_DISSATISFIED(1, "Very dissatisfied"),
    DISSATISFIED(2, "Dissatisfied"),
    OK(3, "Ok"),
    SATISFIED(4, "Satisfied"),
    VERY_SATISFIED(5, "Very satisfied");

    private final int value;
    private final String description;

    Rating(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static Rating fromValue(int value) {
        for (Rating rating : values()) {
            if (rating.value == value) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Rating must be between 1 and 5");
    }
}
