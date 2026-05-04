package com.hotel.dao;

import com.hotel.model.*;
import java.util.*;

/**
 * DAO for Room data. Format: roomNumber|type|price|amenities|available|floor|roomClass|hasJacuzzi
 */
public class RoomDAO {
    private static final String FILE = "rooms.txt";

    public RoomDAO() {
        FileUtils.ensureFileExists(FILE);
    }

    public void save(Room room) {
        List<String> lines = FileUtils.readLines(FILE);
        lines.add(toLine(room));
        FileUtils.writeLines(FILE, lines);
    }

    public Room findByNumber(String roomNumber) {
        for (String line : FileUtils.readLines(FILE)) {
            Room r = fromLine(line);
            if (r != null && roomNumber.equals(r.getRoomNumber())) return r;
        }
        return null;
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Room r = fromLine(line);
            if (r != null) rooms.add(r);
        }
        return rooms;
    }

    public List<Room> findAvailable() {
        List<Room> rooms = new ArrayList<>();
        for (Room r : findAll()) {
            if (r.isAvailable()) rooms.add(r);
        }
        return rooms;
    }

    public List<Room> findByType(String type) {
        List<Room> rooms = new ArrayList<>();
        for (Room r : findAll()) {
            if (type.equalsIgnoreCase(r.getType())) rooms.add(r);
        }
        return rooms;
    }

    public void update(Room room) {
        List<String> lines = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Room r = fromLine(line);
            if (r != null && r.getRoomNumber().equals(room.getRoomNumber())) {
                updated.add(toLine(room));
            } else {
                updated.add(line);
            }
        }
        FileUtils.writeLines(FILE, updated);
    }

    public void delete(String roomNumber) {
        List<String> lines = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Room r = fromLine(line);
            if (r != null && !r.getRoomNumber().equals(roomNumber)) {
                updated.add(line);
            }
        }
        FileUtils.writeLines(FILE, updated);
    }

    private String toLine(Room r) {
        String cls = (r instanceof SuiteRoom) ? "SUITE" : "STANDARD";
        String jacuzzi = (r instanceof SuiteRoom) ? String.valueOf(((SuiteRoom) r).isHasJacuzzi()) : "false";
        return String.join("|",
                safe(r.getRoomNumber()), safe(r.getType()),
                String.valueOf(r.getPrice()), safe(r.getAmenities()),
                String.valueOf(r.isAvailable()), String.valueOf(r.getFloor()),
                cls, jacuzzi);
    }

    private Room fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 8) return null;
        try {
            String num = p[0], type = p[1];
            double price = parseDouble(p[2]);
            String amenities = p[3];
            boolean available = Boolean.parseBoolean(p[4]);
            int floor = parseInt(p[5]);
            String cls = p[6];
            boolean jacuzzi = Boolean.parseBoolean(p[7]);
            if ("SUITE".equals(cls)) {
                return new SuiteRoom(num, type, price, amenities, available, floor, jacuzzi);
            } else {
                return new StandardRoom(num, type, price, amenities, available, floor);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", ""); }
    private double parseDouble(String s) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; } }
    private int parseInt(String s) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; } }
}
