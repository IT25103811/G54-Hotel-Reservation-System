package com.hotel.dao;

import com.hotel.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * DAO for Review data.
 * Format: reviewId|rating|comment|timestamp|hotelResponse|reviewType|alias|guestId|guestName|reservationId
 */
public class ReviewDAO {
    private static final String FILE = "reviews.txt";

    public ReviewDAO() {
        FileUtils.ensureFileExists(FILE);
    }

    public void save(Review review) {
        List<String> lines = FileUtils.readLines(FILE);
        lines.add(toLine(review));
        FileUtils.writeLines(FILE, lines);
    }

    public Review findById(String id) {
        for (String line : FileUtils.readLines(FILE)) {
            Review r = fromLine(line);
            if (r != null && id.equals(r.getReviewId())) return r;
        }
        return null;
    }

    public List<Review> findAll() {
        List<Review> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Review r = fromLine(line);
            if (r != null) result.add(r);
        }
        return result;
    }

    public List<Review> findByGuestId(String guestId) {
        List<Review> result = new ArrayList<>();
        for (Review r : findAll()) {
            if (r instanceof VerifiedGuestReview && guestId.equals(((VerifiedGuestReview) r).getGuestId())) {
                result.add(r);
            }
        }
        return result;
    }

    public void update(Review review) {
        List<String> lines = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Review r = fromLine(line);
            if (r != null && r.getReviewId().equals(review.getReviewId())) {
                updated.add(toLine(review));
            } else {
                updated.add(line);
            }
        }
        FileUtils.writeLines(FILE, updated);
    }

    public void delete(String id) {
        List<String> lines = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Review r = fromLine(line);
            if (r != null && !r.getReviewId().equals(id)) {
                updated.add(line);
            }
        }
        FileUtils.writeLines(FILE, updated);
    }

    private String toLine(Review r) {
        String type = (r instanceof VerifiedGuestReview) ? "VERIFIED" : "ANONYMOUS";
        String alias = "", guestId = "", guestName = "", reservationId = "";
        if (r instanceof AnonymousReview) {
            alias = safe(((AnonymousReview) r).getAlias());
        } else if (r instanceof VerifiedGuestReview) {
            VerifiedGuestReview vr = (VerifiedGuestReview) r;
            guestId = safe(vr.getGuestId());
            guestName = safe(vr.getGuestName());
            reservationId = safe(vr.getReservationId());
        }
        return String.join("|",
                safe(r.getReviewId()), String.valueOf(r.getRating()),
                safe(r.getComment()),
                r.getTimestamp() != null ? r.getTimestamp().toString() : "",
                safe(r.getHotelResponse()), type, alias, guestId, guestName, reservationId);
    }

    private Review fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 10) return null;
        try {
            String id = p[0];
            int rating = parseInt(p[1]);
            String comment = p[2];
            LocalDateTime ts = p[3].isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(p[3]);
            String response = p[4], type = p[5], alias = p[6], guestId = p[7], guestName = p[8], resId = p[9];
            if ("VERIFIED".equals(type)) {
                return new VerifiedGuestReview(id, rating, comment, ts, response, guestId, guestName, resId);
            } else {
                return new AnonymousReview(id, rating, comment, ts, response, alias);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", ""); }
    private int parseInt(String s) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; } }
}
