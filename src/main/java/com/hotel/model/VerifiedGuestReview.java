package com.hotel.model;

import java.time.LocalDateTime;

/**
 * Verified guest review - includes guest info and stay details.
 */
public class VerifiedGuestReview extends Review {
    private String guestId;
    private String guestName;
    private String reservationId;

    public VerifiedGuestReview() {}

    public VerifiedGuestReview(String reviewId, int rating, String comment,
                               LocalDateTime timestamp, String hotelResponse,
                               String guestId, String guestName, String reservationId) {
        super(reviewId, rating, comment, timestamp, hotelResponse);
        this.guestId = guestId;
        this.guestName = guestName;
        this.reservationId = reservationId;
    }

    @Override
    public String getDisplayFormat() {
        return String.format("[Verified] %s | Rating: %d/5 | Stay: %s | %s | %s",
                guestName != null ? guestName : "Guest",
                getRating(),
                reservationId != null ? reservationId : "N/A",
                getComment(),
                getTimestamp() != null ? getTimestamp().toLocalDate().toString() : "");
    }

    @Override
    public boolean isVerified() {
        return true;
    }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
}
