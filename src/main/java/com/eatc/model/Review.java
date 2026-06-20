package com.eatc.model;

import java.time.LocalDateTime;

public class Review {
    private final Rating rating;
    private final String comment;
    private final LocalDateTime createdAt;

    public Review(Rating rating, String comment) {
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null");
        }

        this.rating = rating;
        this.comment = comment == null ? "" : comment.trim();
        this.createdAt = LocalDateTime.now();
    }

    public Rating getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return rating.getValue() + "/5 - " + comment;
    }
}