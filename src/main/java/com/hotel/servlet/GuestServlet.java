package com.hotel.servlet;

import com.hotel.dao.GuestDAO;
import com.hotel.model.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/guests")
public class GuestServlet extends HttpServlet {

    private final GuestDAO guestDAO = new GuestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "login";
        try {
            switch (action) {
                case "register":
                    req.getRequestDispatcher("/guest/register.jsp").forward(req, resp);
                    break;
                case "profile":
                    requireGuestLogin(req, resp);
                    if (!resp.isCommitted()) req.getRequestDispatcher("/guest/profile.jsp").forward(req, resp);
                    break;
                case "list":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) {
                        req.setAttribute("guests", guestDAO.findAll());
                        req.getRequestDispatcher("/guest/list.jsp").forward(req, resp);
                    }
                    break;
                case "logout":
                    req.getSession().invalidate();
                    resp.sendRedirect(req.getContextPath() + "/guests?action=login");
                    break;
                default:
                    req.getRequestDispatcher("/guest/login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "";
        try {
            switch (action) {
                case "register":
                    handleRegister(req, resp);
                    break;
                case "login":
                    handleLogin(req, resp);
                    break;
                case "profile":
                    handleUpdateProfile(req, resp);
                    break;
                case "selfDelete":
                    requireGuestLogin(req, resp);
                    if (!resp.isCommitted()) {
                        HttpSession s = req.getSession(false);
                        Guest g = (Guest) s.getAttribute("loggedInGuest");

                        guestDAO.delete(g.getId());     // delete from file
                        s.invalidate();                 // logout user
                        resp.sendRedirect(req.getContextPath() + "/guests?action=login&deleted=true");
                    }
                    break;
                case "delete":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) {
                        guestDAO.delete(req.getParameter("id"));
                        resp.sendRedirect(req.getContextPath() + "/guests?action=list");
                    }
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/guests?action=login");
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String guestType = req.getParameter("guestType");
        String tier = req.getParameter("membershipTier");

        if (guestDAO.findByEmail(email) != null) {
            req.setAttribute("error", "Email already registered.");
            req.getRequestDispatcher("/guest/register.jsp").forward(req, resp);
            return;
        }
        String id = com.hotel.dao.FileUtils.generateId("G");
        Guest guest;
        if ("VIP".equals(guestType)) {
            guest = new VIPGuest(id, name, email, phone, password, 0, tier != null ? tier : "Silver");
        } else {
            guest = new RegularGuest(id, name, email, phone, password, 0);
        }
        guestDAO.save(guest);
        resp.sendRedirect(req.getContextPath() + "/guests?action=login&registered=true");
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        Guest guest = guestDAO.findByEmail(email);
        if (guest != null && password != null && password.equals(guest.getPassword())) {
            req.getSession().setAttribute("loggedInGuest", guest);
            resp.sendRedirect(req.getContextPath() + "/guests?action=profile");
        } else {
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/guest/login.jsp").forward(req, resp);
        }
    }

    private void handleUpdateProfile(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        Guest guest = (session != null) ? (Guest) session.getAttribute("loggedInGuest") : null;
        if (guest == null) { resp.sendRedirect(req.getContextPath() + "/guests?action=login"); return; }
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        if (email != null && !email.isBlank() && !email.equalsIgnoreCase(guest.getEmail())) {
            Guest existing = guestDAO.findByEmail(email);
            if (existing != null && !existing.getId().equals(guest.getId())) {
                req.setAttribute("error", "This email is already used by another account.");
                req.getRequestDispatcher("/guest/profile.jsp").forward(req, resp);
                return;
            }
        }
        if (phone != null && !phone.isEmpty()) guest.setPhone(phone);
        if (password != null && !password.isEmpty()) guest.setPassword(password);
        if (name != null && !name.isBlank()) guest.setName(name);
        if (email != null && !email.isBlank()) guest.setEmail(email);

        guestDAO.update(guest);
        session.setAttribute("loggedInGuest", guest);
        req.setAttribute("success", "Profile updated.");
        req.getRequestDispatcher("/guest/profile.jsp").forward(req, resp);
    }

    private void requireGuestLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedInGuest") == null) {
            resp.sendRedirect(req.getContextPath() + "/guests?action=login");
        }
    }

    private void requireStaffLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedInStaff") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff?action=login");
        }
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, Exception e)
            throws ServletException, IOException {
        req.setAttribute("errorMessage", e.getMessage());
        req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
    }
}
