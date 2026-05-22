package com.hotel.servlet;

import com.hotel.service.RoomService;
import com.hotel.model.*;
import com.hotel.util.RoomSorter;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/rooms")
public class RoomServlet extends HttpServlet {

    private final RoomService roomService = new RoomService();

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
                        String roomNumber = req.getParameter("roomNumber");
                        try {
                            Room room = roomService.getRoomByNumber(roomNumber);
                            req.setAttribute("room", room);
                            req.getRequestDispatcher("/room/edit.jsp").forward(req, resp);
                        } catch (Exception e) {
                            req.setAttribute("error", e.getMessage());
                            req.getRequestDispatcher("/room/add.jsp").forward(req, resp);
                        }
                    }
                    break;
                case "search":
                    handleSearch(req, resp);
                    break;
                default:
                    String sortParam = req.getParameter("sortBy");
                    List<Room> allRooms = roomService.getAllRooms();
                    if ("price_desc".equals(sortParam)) {
                        allRooms = RoomSorter.sort(allRooms, RoomSorter.SortBy.PRICE_DESC);
                    } else if ("price_asc".equals(sortParam)) {
                        allRooms = RoomSorter.sort(allRooms, RoomSorter.SortBy.PRICE_ASC);
                    } else if ("floor".equals(sortParam)) {
                        allRooms = RoomSorter.sort(allRooms, RoomSorter.SortBy.FLOOR_ASC);
                    } else if ("type".equals(sortParam)) {
                        allRooms = RoomSorter.sort(allRooms, RoomSorter.SortBy.TYPE);
                    } else if ("availability".equals(sortParam)) {
                        allRooms = RoomSorter.sort(allRooms, RoomSorter.SortBy.AVAILABILITY);
                    }
                    req.setAttribute("rooms", allRooms);
                    req.setAttribute("sortBy", sortParam);
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
                    if (!resp.isCommitted()) handleDelete(req, resp);
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
        String sortParam = req.getParameter("sortBy");

        List<Room> rooms = roomService.getAllRooms();
        if (type != null && !type.isEmpty()) {
            rooms = rooms.stream().filter(r -> type.equalsIgnoreCase(r.getType())).collect(java.util.stream.Collectors.toList());
        }
        if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
            try {
                double max = Double.parseDouble(maxPriceStr);
                rooms = rooms.stream().filter(r -> r.calculatePrice() <= max).collect(java.util.stream.Collectors.toList());
            } catch (NumberFormatException ignored) {}
        }
        if ("true".equals(availableStr)) {
            rooms = rooms.stream().filter(Room::isAvailable).collect(java.util.stream.Collectors.toList());
        }

        // Apply QuickSort based on requested sort criterion
        if ("price_desc".equals(sortParam)) {
            rooms = RoomSorter.sort(rooms, RoomSorter.SortBy.PRICE_DESC);
        } else if ("floor".equals(sortParam)) {
            rooms = RoomSorter.sort(rooms, RoomSorter.SortBy.FLOOR_ASC);
        } else if ("type".equals(sortParam)) {
            rooms = RoomSorter.sort(rooms, RoomSorter.SortBy.TYPE);
        } else if ("availability".equals(sortParam)) {
            rooms = RoomSorter.sort(rooms, RoomSorter.SortBy.AVAILABILITY);
        } else {
            // Default: sort by price ascending
            rooms = RoomSorter.sort(rooms, RoomSorter.SortBy.PRICE_ASC);
        }

        req.setAttribute("rooms", rooms);
        req.setAttribute("sortBy", sortParam);
        req.getRequestDispatcher("/room/search.jsp").forward(req, resp);
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            String roomNumber = req.getParameter("roomNumber");
            String type = req.getParameter("type");
            double price = parseDouble(req.getParameter("price"));
            String amenities = req.getParameter("amenities");
            int floor = parseInt(req.getParameter("floor"));
            String roomClass = req.getParameter("roomClass");
            boolean jacuzzi = "on".equals(req.getParameter("hasJacuzzi"));

            Room room;
            if ("SUITE".equals(roomClass)) {
                room = new SuiteRoom(roomNumber, type, price, amenities, true, floor, jacuzzi);
            } else {
                room = new StandardRoom(roomNumber, type, price, amenities, true, floor);
            }

            roomService.addRoom(room);
            resp.sendRedirect(req.getContextPath() + "/rooms?action=list");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/room/add.jsp").forward(req, resp);
        }
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            String roomNumber = req.getParameter("roomNumber");
            Room existing = roomService.getRoomByNumber(roomNumber);
            if (existing == null) {
                resp.sendRedirect(req.getContextPath() + "/rooms?action=list");
                return;
            }

            existing.setType(req.getParameter("type"));
            existing.setPrice(parseDouble(req.getParameter("price")));
            existing.setAmenities(req.getParameter("amenities"));
            existing.setFloor(parseInt(req.getParameter("floor")));
            existing.setAvailable("true".equals(req.getParameter("available")));
            if (existing instanceof SuiteRoom) {
                ((SuiteRoom) existing).setHasJacuzzi("on".equals(req.getParameter("hasJacuzzi")));
            }

            roomService.updateRoom(existing);
            resp.sendRedirect(req.getContextPath() + "/rooms?action=list");
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            String roomNumber = req.getParameter("roomNumber");
            try {
                req.setAttribute("room", roomService.getRoomByNumber(roomNumber));
                req.getRequestDispatcher("/room/edit.jsp").forward(req, resp);
            } catch (Exception ex) {
                handleError(req, resp, ex);
            }
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            String roomNumber = req.getParameter("roomNumber");
            roomService.deleteRoom(roomNumber);
            resp.sendRedirect(req.getContextPath() + "/rooms?action=list");
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
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

    private double parseDouble(String s) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; } }
    private int parseInt(String s) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; } }
}
