package com.hotel.dao;

import com.hotel.model.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO for Review data.
 * Format (14 fields):
 *   reviewId|rating|comment|timestamp|hotelResponse|reviewType|alias|guestId|guestName|reservationId
 *   |status|moderatedBy|moderationNote|moderatedAt
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

    /** Returns only APPROVED reviews – used for the public view. */
    public List<Review> findApproved() {
        return findAll().stream()
                .filter(r -> r.getStatus() == ReviewStatus.APPROVED)
                .collect(Collectors.toList());
    }

    /** Returns reviews with a specific status. */
    public List<Review> findByStatus(ReviewStatus status) {
        return findAll().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
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

    /** Counts of reviews by status – used for the dashboard. */
    public Map<ReviewStatus, Long> countByStatus() {
        Map<ReviewStatus, Long> counts = new LinkedHashMap<>();
        for (ReviewStatus s : ReviewStatus.values()) counts.put(s, 0L);
        for (Review r : findAll()) counts.merge(r.getStatus(), 1L, Long::sum);
        return counts;
    }

    /** Average rating across all APPROVED reviews. Returns 0 if none. */
    public double averageApprovedRating() {
        List<Review> approved = findApproved();
        if (approved.isEmpty()) return 0;
        return approved.stream().mapToInt(Review::getRating).average().orElse(0);
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

    // ── Serialisation ────────────────────────────────────────────────────────

    private String toLine(Review r) {
        String type = (r instanceof VerifiedGuestReview) ? "VERIFIED" : "ANONYMOUS";
        String alias = "", guestId = "", guestName = "", reservationId = "";
        if (r instanceof AnonymousReview) {
            alias = safe(((AnonymousReview) r).getAlias());
        } else if (r instanceof VerifiedGuestReview) {
            VerifiedGuestReview vr = (VerifiedGuestReview) r;
            guestId      = safe(vr.getGuestId());
            guestName    = safe(vr.getGuestName());
            reservationId = safe(vr.getReservationId());
        }
        String status        = r.getStatus() != null ? r.getStatus().name() : ReviewStatus.PENDING.name();
        String moderatedBy   = safe(r.getModeratedBy());
        String moderationNote = safe(r.getModerationNote());
        String moderatedAt   = r.getModeratedAt() != null ? r.getModeratedAt().toString() : "";

        return String.join("|",
                safe(r.getReviewId()), String.valueOf(r.getRating()),
                safe(r.getComment()),
                r.getTimestamp() != null ? r.getTimestamp().toString() : "",
                safe(r.getHotelResponse()), type, alias, guestId, guestName, reservationId,
                status, moderatedBy, moderationNote, moderatedAt);
    }

    private Review fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 10) return null;
        try {
            String id         = p[0];
            int rating        = parseInt(p[1]);
            String comment    = p[2];
            LocalDateTime ts  = p[3].isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(p[3]);
            String response   = p[4];
            String type       = p[5];
            String alias      = p[6];
            String guestId    = p[7];
            String guestName  = p[8];
            String resId      = p[9];

            Review r;
            if ("VERIFIED".equals(type)) {
                r = new VerifiedGuestReview(id, rating, comment, ts, response, guestId, guestName, resId);
            } else {
                r = new AnonymousReview(id, rating, comment, ts, response, alias);
            }

            // Moderation fields (added later – tolerate missing)
            if (p.length >= 11 && !p[10].isEmpty()) {
                try { r.setStatus(ReviewStatus.valueOf(p[10])); } catch (Exception ignored) {}
            }
            if (p.length >= 12) r.setModeratedBy(p[11]);
            if (p.length >= 13) r.setModerationNote(p[12]);
            if (p.length >= 14 && !p[13].isEmpty()) {
                try { r.setModeratedAt(LocalDateTime.parse(p[13])); } catch (Exception ignored) {}
            }
            return r;
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", ""); }
    private int parseInt(String s) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; } }
}
