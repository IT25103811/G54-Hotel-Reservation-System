package com.hotel.dao;

import com.hotel.model.*;
import java.util.*;

/**
 * DAO for Staff data. Format: staffId|name|email|password|role|salary|shift|staffType
 * Initializes with a default admin account if file is empty.
 */
public class StaffDAO {
    private static final String FILE = "staff.txt";

    public StaffDAO() {
        FileUtils.ensureFileExists(FILE);
        initDefaultAdmin();
    }

    private void initDefaultAdmin() {
        List<String> lines = FileUtils.readLines(FILE);
        if (lines.isEmpty()) {
            Manager admin = new Manager("S001", "Admin", "admin@hotel.com", "admin123", 5000.0, "ALL");
            save(admin);
        }
    }

    public void save(Staff staff) {
        List<String> lines = FileUtils.readLines(FILE);
        lines.add(toLine(staff));
        FileUtils.writeLines(FILE, lines);
    }

    public Staff findByEmail(String email) {
        for (String line : FileUtils.readLines(FILE)) {
            Staff s = fromLine(line);
            if (s != null && email.equalsIgnoreCase(s.getEmail())) return s;
        }
        return null;
    }

    public Staff findById(String id) {
        for (String line : FileUtils.readLines(FILE)) {
            Staff s = fromLine(line);
            if (s != null && id.equals(s.getStaffId())) return s;
        }
        return null;
    }

    public List<Staff> findAll() {
        List<Staff> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Staff s = fromLine(line);
            if (s != null) result.add(s);
        }
        return result;
    }

    public void update(Staff staff) {
        List<String> lines = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Staff s = fromLine(line);
            if (s != null && s.getStaffId().equals(staff.getStaffId())) {
                updated.add(toLine(staff));
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
            Staff s = fromLine(line);
            if (s != null && !s.getStaffId().equals(id)) {
                updated.add(line);
            }
        }
        FileUtils.writeLines(FILE, updated);
    }

    private String toLine(Staff s) {
        String type = (s instanceof Manager) ? "MANAGER" : "RECEPTIONIST";
        return String.join("|",
                safe(s.getStaffId()), safe(s.getName()), safe(s.getEmail()),
                safe(s.getPassword()), safe(s.getRole()),
                String.valueOf(s.getSalary()), safe(s.getShift()), type);
    }

    private Staff fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 8) return null;
        try {
            String id = p[0], name = p[1], email = p[2], password = p[3], role = p[4];
            double salary = parseDouble(p[5]);
            String shift = p[6], type = p[7];
            if ("MANAGER".equals(type)) {
                Manager m = new Manager(id, name, email, password, salary, shift);
                m.setRole(role);
                return m;
            } else {
                Receptionist r = new Receptionist(id, name, email, password, salary, shift);
                r.setRole(role);
                return r;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", ""); }
    private double parseDouble(String s) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; } }
}
