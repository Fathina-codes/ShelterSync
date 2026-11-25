package com.sheltersync;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection()) {

            req.setAttribute("stats", DBConnection.query(con,
                "SELECT " +
                "(SELECT COUNT(*) FROM shelter) AS shelters, " +
                "(SELECT COALESCE(SUM(capacity),0) FROM shelter) AS capacity, " +
                "(SELECT COUNT(*) FROM affected_person) AS occupied, " +
                "(SELECT COUNT(*) FROM affected_person) AS people, " +
                "(SELECT COUNT(*) FROM emergency_request WHERE status='PENDING') AS pending, " +
                "(SELECT COUNT(*) FROM emergency_request WHERE status='PENDING' AND urgency='CRITICAL') AS critical, " +
                "(SELECT COUNT(*) FROM resource WHERE quantity < 50) AS lowstock").get(0));

            req.setAttribute("shelterList", DBConnection.query(con,
                "SELECT s.shelter_id, s.name, s.capacity, COUNT(p.person_id) AS occupancy, " +
                "ROUND(COUNT(p.person_id)*100/NULLIF(s.capacity,0)) AS pct, " +
                "COUNT(p.person_id) AS registered " +
                "FROM shelter s LEFT JOIN affected_person p ON s.shelter_id = p.shelter_id " +
                "GROUP BY s.shelter_id, s.name, s.capacity ORDER BY s.shelter_id"));

            req.setAttribute("topRequests", DBConnection.query(con,
                "SELECT r.request_id, s.name AS shelter, res.name AS resource, r.quantity_needed, r.urgency " +
                "FROM emergency_request r JOIN shelter s ON r.shelter_id = s.shelter_id " +
                "JOIN resource res ON r.resource_id = res.resource_id " +
                "WHERE r.status='PENDING' " +
                "ORDER BY FIELD(r.urgency,'CRITICAL','HIGH','MEDIUM','LOW'), r.request_id LIMIT 5"));

            req.setAttribute("byCategory", DBConnection.query(con,
                "SELECT res.category, COALESCE(SUM(d.quantity),0) AS total " +
                "FROM resource res LEFT JOIN distribution d ON res.resource_id = d.resource_id " +
                "GROUP BY res.category ORDER BY res.category"));

            req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}