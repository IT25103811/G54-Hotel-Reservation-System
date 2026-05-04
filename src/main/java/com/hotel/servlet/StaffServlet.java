package com.hotel.servlet;

import com.hotel.dao.StaffDAO;
import com.hotel.dao.FileUtils;
import com.hotel.model.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/staff")
public class StaffServlet extends HttpServlet {

    private final StaffDAO staffDAO = new StaffDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "login";
        try {
            switch (action) {
                case "dashboard":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) req.getRequestDispatcher("/staff/dashboard.jsp").forward(req, resp);
                    break;
                case "register":
                    requireManagerLogin(req, resp);
                    if (!resp.isCommitted()) req.getRequestDispatcher("/staff/register.jsp").forward(req, resp);
                    break;
                case "manage":
                    requireManagerLogin(req, resp);
                    if (!resp.isCommitted()) {
                        req.setAttribute("staffList", staffDAO.findAll());
                        req.getRequestDispatcher("/staff/manage.jsp").forward(req, resp);
                    }
                    break;
                case "edit":
                    requireManagerLogin(req, resp);
                    if (!resp.isCommitted()) {
                        Staff s = staffDAO.findById(req.getParameter("staffId"));
                        req.setAttribute("staffMember", s);
                        req.getRequestDispatcher("/staff/register.jsp").forward(req, resp);
                    }
                    break;
                case "logout":
                    req.getSession().invalidate();
                    resp.sendRedirect(req.getContextPath() + "/staff?action=login");
                    break;
                default:
                    req.getRequestDispatcher("/staff/login.jsp").forward(req, resp);
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
                case "login":
                    handleLogin(req, resp);
                    break;
                case "register":
                    requireManagerLogin(req, resp);
                    if (!resp.isCommitted()) handleRegister(req, resp);
                    break;
                case "edit":
                    requireManagerLogin(req, resp);
                    if (!resp.isCommitted()) handleEdit(req, resp);
                    break;
                case "delete":
                    requireManagerLogin(req, resp);
                    if (!resp.isCommitted()) {
                        staffDAO.delete(req.getParameter("staffId"));
                        resp.sendRedirect(req.getContextPath() + "/staff?action=manage");
                    }
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/staff?action=login");
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        Staff staff = staffDAO.findByEmail(email);
        if (staff != null && password != null && password.equals(staff.getPassword())) {
            req.getSession().setAttribute("loggedInStaff", staff);
            resp.sendRedirect(req.getContextPath() + "/staff?action=dashboard");
        } else {
            req.setAttribute("error", "Invalid credentials.");
            req.getRequestDispatcher("/staff/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");
        double salary = parseDouble(req.getParameter("salary"));
        String shift = req.getParameter("shift");

        if (staffDAO.findByEmail(email) != null) {
            req.setAttribute("error", "Email already registered.");
            req.getRequestDispatcher("/staff/register.jsp").forward(req, resp);
            return;
        }
        String id = FileUtils.generateId("S");
        Staff staff;
        if ("MANAGER".equals(role)) {
            staff = new Manager(id, name, email, password, salary, shift);
        } else {
            staff = new Receptionist(id, name, email, password, salary, shift);
        }
        staffDAO.save(staff);
        resp.sendRedirect(req.getContextPath() + "/staff?action=manage");
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String staffId = req.getParameter("staffId");
        Staff s = staffDAO.findById(staffId);
        if (s == null) { resp.sendRedirect(req.getContextPath() + "/staff?action=manage"); return; }
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        String shift = req.getParameter("shift");
        double salary = parseDouble(req.getParameter("salary"));
        if (name != null && !name.isEmpty()) s.setName(name);
        if (password != null && !password.isEmpty()) s.setPassword(password);
        if (shift != null && !shift.isEmpty()) s.setShift(shift);
        if (salary > 0) s.setSalary(salary);
        staffDAO.update(s);
        resp.sendRedirect(req.getContextPath() + "/staff?action=manage");
    }

    private void requireStaffLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedInStaff") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff?action=login");
        }
    }

    private void requireManagerLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !(session.getAttribute("loggedInStaff") instanceof Manager)) {
            resp.sendRedirect(req.getContextPath() + "/staff?action=login");
        }
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, Exception e)
            throws ServletException, IOException {
        req.setAttribute("errorMessage", e.getMessage());
        req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
    }

    private double parseDouble(String s) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; } }
}
