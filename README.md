# 🏨 Grand Vista Hotel Reservation System

A full-stack **Java Web Application** for hotel reservation management, built with **JSP/Servlets** and **Embedded Apache Tomcat**. This is a group project developed by **SLIIT Kandy University — Group G54 (2026 Semester 2)**.

---

## 📋 Project Overview

The Grand Vista Hotel Reservation System provides a complete hotel management solution with modules for guest management, room management, reservations, payments, staff management, and reviews. Each module is developed independently by a team member using **version control (Git/GitHub)** for collaboration.

---

## 🏗️ System Architecture

```
Technology Stack:
├── Backend:    Java 11, JSP, Servlets
├── Frontend:   Bootstrap 5, Bootstrap Icons, CSS
├── Server:     Embedded Apache Tomcat 9.0.109
├── Build:      Apache Maven
├── Storage:    Text file-based (no database)
└── VCS:        Git + GitHub (branch-per-feature)
```

---

## 📁 Project Structure

```
G54-Hotel-Reservation-System/
├── pom.xml                              # Maven build configuration
├── data/                                # Text file storage
│   └── guests.txt                       # Guest data file
│
├── src/main/java/com/hotel/
│   ├── Main.java                        # Embedded Tomcat startup
│   │
│   ├── model/                           # Data models (OOP)
│   │   ├── Guest.java                   # Abstract base class
│   │   ├── RegularGuest.java            # Regular guest (5% discount)
│   │   └── VIPGuest.java                # VIP guest (20% discount)
│   │
│   ├── dao/                             # Data Access Objects
│   │   ├── FileUtils.java               # Shared file utility
│   │   └── GuestDAO.java                # Guest CRUD operations
│   │
│   └── servlet/                         # Controllers
│       └── GuestServlet.java            # Guest management servlet
│
├── src/main/webapp/
│   ├── index.jsp                        # Home page
│   ├── css/style.css                    # Custom styles
│   │
│   ├── includes/                        # Shared components
│   │   ├── header.jsp                   # Navigation bar
│   │   └── footer.jsp                   # Footer
│   │
│   ├── guest/                           # Guest pages
│   │   ├── login.jsp                    # Guest login
│   │   ├── register.jsp                 # Guest registration
│   │   ├── profile.jsp                  # Guest profile
│   │   └── list.jsp                     # Guest list (staff view)
│   │
│   └── WEB-INF/
│       ├── web.xml                      # Servlet mappings
│       └── error.jsp                    # Error page
│
└── README.md
```

---

## 🧩 Modules & Team Members

| Module | Member | Branch | Status |
|--------|--------|--------|--------|
| 👤 Guest Management | Gunarathna R.P.Y.S.A. (IT25103811) | `yasas` `Guest-managment-CRUD` `Guest-managment-CRUD-1`| ✅ Complete |
| 🛏️ Room Management | TBD | — | 🔄 In Progress |
| 📅 Reservation Management | TBD | — | 🔄 In Progress |
| 💳 Payment/Billing Management | TBD | — | 🔄 In Progress |
| 👨‍💼 Staff Management | Arachchi G.U.A.N.T.G.U. (IT25103812) | `Nisal` `Staff-managment-CRUD` | ✅ Complete |
| ⭐ Review Management | TBD | — | 🔄 In Progress |

---

## 👤 Guest Management Module (IT25103811)

### OOP Concepts Used

| Concept | Implementation |
|---------|---------------|
| **Abstraction** | `Guest` is an abstract class with abstract method `getDiscount()` |
| **Inheritance** | `RegularGuest` and `VIPGuest` extend `Guest` |
| **Polymorphism** | `getDiscount()` returns 5% for Regular, 20% for VIP |
| **Encapsulation** | All fields are `private` with getters/setters |

### Features

- ✅ Guest Registration (Regular / VIP with membership tier)
- ✅ Guest Login with email & password
- ✅ Profile View & Update (phone, password,name,email)
- ✅ Guest List with search (staff-only access)
- ✅ Guest selfe Delete 
- ✅ Guest Delete (staff-only access)
- ✅ Session management (login/logout)
- ✅ File-based data persistence (`data/guests.txt`)

### URL Endpoints

| Action | URL |
|--------|-----|
| Login Page | `GET /guests?action=login` |
| Register Page | `GET /guests?action=register` |
| Profile Page | `GET /guests?action=profile` |
| Guest List | `GET /guests?action=list` |
| Logout | `GET /guests?action=logout` |
| Submit Login | `POST /guests` (action=login) |
| Submit Register | `POST /guests` (action=register) |
| Update Profile | `POST /guests` (action=profile) |
| Delete Guest | `POST /guests` (action=delete) |


---

## 👤 Staff Management Module (IT25103812)


### OOP Concepts Used

| Concept | Implementation |
|---|---|
| **Abstraction** | `Staff` is an abstract class with abstract method `getPermissions()` |
| **Inheritance** | `Manager` and `Receptionist` extend `Staff` |
| **Polymorphism** | `getPermissions()` returns `"ALL_PERMISSIONS"` for Manager, `"GUEST_MANAGEMENT,RESERVATIONS,BILLING"` for Receptionist |
| **Encapsulation** | All fields (`staffId`, `name`, `email`, `password`, `role`, `salary`, `shift`) are private with getters/setters |

---

### Features

- ✅ Staff Login with email & password
- ✅ Role-based access control (Manager vs Receptionist)
- ✅ Staff Registration (Manager / Receptionist with shift & salary) — manager-only
- ✅ Staff Dashboard (accessible to all logged-in staff)
- ✅ Staff List & Management (manager-only access)
- ✅ Staff Profile Edit (name, password, shift, salary) — manager-only
- ✅ Staff Delete — manager-only
- ✅ `canApproveRefunds()` — `true` for Manager, `false` for Receptionist
- ✅ Session management (login/logout)
- ✅ File-based data persistence (`data/staff.txt`)

---

### URL Endpoints

| Action | URL |
|---|---|
| Login Page | `GET /staff?action=login` |
| Dashboard | `GET /staff?action=dashboard` |
| Register Page | `GET /staff?action=register` |
| Staff List | `GET /staff?action=manage` |
| Edit Staff Form | `GET /staff?action=edit&staffId={id}` |
| Logout | `GET /staff?action=logout` |
| Submit Login | `POST /staff` (action=login) |
| Submit Register | `POST /staff` (action=register) |
| Update Staff | `POST /staff` (action=edit) |
| Delete Staff | `POST /staff` (action=delete) |
---

## 🚀 How to Run

### Prerequisites

- **Java 11** or higher → [Download](https://adoptium.net/)
- **Apache Maven 3.6+** → [Download](https://maven.apache.org/download.cgi)
- **Git** → [Download](https://git-scm.com/)

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/IT25103811/G54-Hotel-Reservation-System.git
cd G54-Hotel-Reservation-System

# 2. Build the project
mvn clean compile

# 3. Run the application
mvn exec:java -Dexec.mainClass="com.hotel.Main"

# 4. Open in browser
# http://localhost:8080/
```

### Quick Test

1. Go to `http://localhost:8080/guests?action=register`
2. Register a new guest (Regular or VIP)
3. Login at `http://localhost:8080/guests?action=login`
4. View/update profile after login

---

## 🔧 Build & Package

```bash
# Build executable JAR
mvn clean package

# Run from JAR
java -jar target/hotel-reservation-system.jar
```

---

## 📊 Version Control Strategy

- **Main branch:** Production-ready merged code
- **Feature branches:** Each member works on their own branch
- **Pull Requests:** Code reviewed before merging to main

### Branch History
```
main
 ├── yasas (Guest Management - IT25103811) ✅ Merged
 ├── [room-management] 🔄 Pending
 ├── [reservation-management] 🔄 Pending
 ├── [payment-management] 🔄 Pending
 ├── Nisal (Staff Management - IT25103812) ✅ Merged
 └── [review-management] 🔄 Pending
```

---

## 📝 Commit History (Guest Management)

| # | Commit Message | Description |
|---|---------------|-------------|
| 1 | Add abstract Guest base class with encapsulation | OOP base class |
| 2 | Add RegularGuest subclass with 5% discount | Inheritance + Polymorphism |
| 3 | Add VIPGuest subclass with 20% discount and membership tier | Inheritance + Polymorphism |
| 4 | Add GuestDAO with full CRUD operations for file-based storage | Data layer |
| 5 | Add GuestServlet controller with login, register, profile, list actions | Controller layer |
| 6 | Add guest login page | View layer |
| 7 | Add guest registration page with Regular/VIP selection | View layer |
| 8 | Add guest profile page with update functionality | View layer |
| 9 | Add staff-only guest list page with search | View layer |
| 10 | Add Selfe delete account | View layer |
| 11 | Add update name and email | View layer |

---

## 🛠️ Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 11 | Backend language |
| JSP | 2.3 | Server-side pages |
| Servlet API | 4.0.1 | HTTP controllers |
| JSTL | 1.2 | JSP tag library |
| Embedded Tomcat | 9.0.109 | Web server |
| Maven | 3.6+ | Build tool |
| Bootstrap | 5.3.0 | CSS framework |
| Bootstrap Icons | 1.11.0 | Icon library |
| Git/GitHub | — | Version control |

---

## 📄 License

This project is developed for academic purposes as part of the **SLIIT Kandy University IT Program — Semester 2 (2026)**.

---

> **Group G54** | SLIIT Kandy University | 2026 Semester 2
