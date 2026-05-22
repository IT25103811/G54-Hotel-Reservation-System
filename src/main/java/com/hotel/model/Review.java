package com.hotel.model;

import java.time.LocalDateTime;

/**
 * Abstract base class for reviews.
 */
public abstract class Review {
    private String reviewId;
    private int rating; // 1-5
    private String comment;
    private LocalDateTime timestamp;
    private String hotelResponse;

    // Feedback & Review Management fields
    //private ReviewStatus status = ReviewStatus.PENDING;
    private String moderatedBy;          // staffId who last changed the status
    private String moderationNote;       // internal staff note (not shown to public)
    private LocalDateTime moderatedAt;   // when the status was last changed

    public Review() {}

    public Review(String reviewId, int rating, String comment,
                  LocalDateTime timestamp, String hotelResponse) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.hotelResponse = hotelResponse;
       // this.status = ReviewStatus.PENDING;
    }

    public abstract String getDisplayFormat();
    public abstract boolean isVerified();

    // ── Core getters / setters ──────────────────────────────────────────────

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getHotelResponse() { return hotelResponse; }
    public void setHotelResponse(String hotelResponse) { this.hotelResponse = hotelResponse; }

    // ── Moderation getters / setters ────────────────────────────────────────

    //public ReviewStatus getStatus() { return status != null ? status : ReviewStatus.PENDING; }
    //public void setStatus(ReviewStatus status) { this.status = status; }

    public String getModeratedBy() { return moderatedBy; }
    public void setModeratedBy(String moderatedBy) { this.moderatedBy = moderatedBy; }

    public String getModerationNote() { return moderationNote; }
    public void setModerationNote(String moderationNote) { this.moderationNote = moderationNote; }

    public LocalDateTime getModeratedAt() { return moderatedAt; }
    public void setModeratedAt(LocalDateTime moderatedAt) { this.moderatedAt = moderatedAt; }

    /** Convenience: is this review visible to the public? */
   // public boolean isPubliclyVisible() {
     //   return getStatus() == ReviewStatus.APPROVED;
    }
//}
