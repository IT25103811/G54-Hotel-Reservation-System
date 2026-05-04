package com.hotel.servlet;

import com.hotel.dao.RoomDAO;
import com.hotel.model.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/rooms")
public class RoomServlet extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        try {
            switch (action) {
                case "add":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) req.getRequestDispatcher("/room/add.jsp").forward(req, resp);
                    break;
                case "edit":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) {
                        Room room = roomDAO.findByNumber(req.getParameter("roomNumber"));
                        req.setAttribute("room", room);
                        req.getRequestDispatcher("/room/edit.jsp").forward(req, resp);
                    }
                    break;
                case "search":
                    handleSearch(req, resp);
                    break;
                default:
                    req.setAttribute("rooms", roomDAO.findAll());
                    req.getRequestDispatcher("/room/list.jsp").forward(req, resp);
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
                case "add":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) handleAdd(req, resp);
                    break;
                case "edit":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) handleEdit(req, resp);
                    break;
                case "delete":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) {
                        roomDAO.delete(req.getParameter("roomNumber"));
                        resp.sendRedirect(req.getContextPath() + "/rooms?action=list");
                    }
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/rooms?action=list");
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    private void handleSearch(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String type = req.getParameter("type");
        String maxPriceStr = req.getParameter("maxPrice");
        String availableStr = req.getParameter("available");

        List<Room> rooms = roomDAO.findAll();
        if (type != null && !type.isEmpty()) {
            rooms = rooms.stream().filter(r -> type.equalsIgnoreCase(r.getType())).collect(Collectors.toList());
        }
        if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
            try {
                double max = Double.parseDouble(maxPriceStr);
                rooms = rooms.stream().filter(r -> r.calculatePrice() <= max).collect(Collectors.toList());
            } catch (NumberFormatException ignored) {}
        }
        if ("true".equals(availableStr)) {
            rooms = rooms.stream().filter(Room::isAvailable).collect(Collectors.toList());
        }
        req.setAttribute("rooms", rooms);
        req.getRequestDispatcher("/room/search.jsp").forward(req, resp);
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String roomNumber = req.getParameter("roomNumber");
        String type = req.getParameter("type");
        double price = parseDouble(req.getParameter("price"));
        String amenities = req.getParameter("amenities");
        int floor = parseInt(req.getParameter("floor"));
        String roomClass = req.getParameter("roomClass");
        boolean jacuzzi = "on".equals(req.getParameter("hasJacuzzi"));

        if (roomDAO.findByNumber(roomNumber) != null) {
            req.setAttribute("error", "Room number already exists.");
            req.getRequestDispatcher("/room/add.jsp").forward(req, resp);
            return;
        }
        Room room;
        if ("SUITE".equals(roomClass)) {
            room = new SuiteRoom(roomNumber, type, price, amenities, true, floor, jacuzzi);
        } else {
            room = new StandardRoom(roomNumber, type, price, amenities, true, floor);
        }
        roomDAO.save(room);
        resp.sendRedirect(req.getContextPath() + "/rooms?action=list");
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String roomNumber = req.getParameter("roomNumber");
        Room existing = roomDAO.findByNumber(roomNumber);
        if (existing == null) { resp.sendRedirect(req.getContextPath() + "/rooms?action=list"); return; }
        existing.setType(req.getParameter("type"));
        existing.setPrice(parseDouble(req.getParameter("price")));
        existing.setAmenities(req.getParameter("amenities"));
        existing.setFloor(parseInt(req.getParameter("floor")));
        existing.setAvailable("true".equals(req.getParameter("available")));
        if (existing instanceof SuiteRoom) {
            ((SuiteRoom) existing).setHasJacuzzi("on".equals(req.getParameter("hasJacuzzi")));
        }
        roomDAO.update(existing);
        resp.sendRedirect(req.getContextPath() + "/rooms?action=list");
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

    private double parseDouble(String s) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; } }
    private int parseInt(String s) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; } }
}
