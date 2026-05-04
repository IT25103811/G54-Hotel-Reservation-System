package com.hotel.model;

import java.time.LocalDateTime;

/**
 * Anonymous review - shows alias instead of real name, not verified.
 */
public class AnonymousReview extends Review {
    private String alias;

    public AnonymousReview() {}

    public AnonymousReview(String reviewId, int rating, String comment,
                           LocalDateTime timestamp, String hotelResponse, String alias) {
        super(reviewId, rating, comment, timestamp, hotelResponse);
        this.alias = alias;
    }

    @Override
    public String getDisplayFormat() {
        return String.format("[%s] Rating: %d/5 | %s | %s",
                alias != null ? alias : "Anonymous",
                getRating(),
                getComment(),
                getTimestamp() != null ? getTimestamp().toLocalDate().toString() : "");
    }

    @Override
    public boolean isVerified() {
        return false;
    }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
}
