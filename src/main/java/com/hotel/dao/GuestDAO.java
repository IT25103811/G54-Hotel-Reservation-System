package com.hotel.dao;

import com.hotel.model.*;
import java.util.*;

/**
 * DAO for Guest data. Format: id|name|email|phone|password|loyaltyPoints|type|membershipTier
 */
public class GuestDAO {
    private static final String FILE = "guests.txt";

    public GuestDAO() {
        FileUtils.ensureFileExists(FILE);
    }

    public void save(Guest guest) {
        List<String> lines = FileUtils.readLines(FILE);
        lines.add(toLine(guest));
        FileUtils.writeLines(FILE, lines);
    }

    public Guest findByEmail(String email) {
        for (String line : FileUtils.readLines(FILE)) {
            Guest g = fromLine(line);
            if (g != null && email.equalsIgnoreCase(g.getEmail())) return g;
        }
        return null;
    }

    public Guest findById(String id) {
        for (String line : FileUtils.readLines(FILE)) {
            Guest g = fromLine(line);
            if (g != null && id.equals(g.getId())) return g;
        }
        return null;
    }

    public List<Guest> findAll() {
        List<Guest> guests = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Guest g = fromLine(line);
            if (g != null) guests.add(g);
        }
        return guests;
    }

    public void update(Guest guest) {
        List<String> lines = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Guest g = fromLine(line);
            if (g != null && g.getId().equals(guest.getId())) {
                updated.add(toLine(guest));
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
            Guest g = fromLine(line);
            if (g != null && !g.getId().equals(id)) {
                updated.add(line);
            }
        }
        FileUtils.writeLines(FILE, updated);
    }

    private String toLine(Guest g) {
        String type = (g instanceof VIPGuest) ? "VIP" : "REGULAR";
        String tier = (g instanceof VIPGuest) ? ((VIPGuest) g).getMembershipTier() : "";
        return String.join("|",
                safe(g.getId()), safe(g.getName()), safe(g.getEmail()),
                safe(g.getPhone()), safe(g.getPassword()),
                String.valueOf(g.getLoyaltyPoints()), type, safe(tier));
    }

    private Guest fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 8) return null;
        try {
            String id = p[0], name = p[1], email = p[2], phone = p[3];
            String password = p[4];
            int loyalty = parseInt(p[5]);
            String type = p[6];
            String tier = p[7];
            if ("VIP".equals(type)) {
                return new VIPGuest(id, name, email, phone, password, loyalty, tier);
            } else {
                return new RegularGuest(id, name, email, phone, password, loyalty);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", ""); }
    private int parseInt(String s) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; } }
}
