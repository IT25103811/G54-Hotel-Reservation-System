package com.hotel.model;

/**
 * Abstract base class for hotel staff.
 */
public abstract class Staff {
    private String staffId;
    private String name;
    private String email;
    private String password; // NOTE: Plain text for educational purposes only
    private String role;
    private double salary;
    private String shift;

    public Staff() {}

    public Staff(String staffId, String name, String email, String password,
                 String role, double salary, String shift) {
        this.staffId = staffId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.salary = salary;
        this.shift = shift;
    }

    public abstract String getPermissions();

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
}
