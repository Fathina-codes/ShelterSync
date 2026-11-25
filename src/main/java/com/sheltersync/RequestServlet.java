package com.sheltersync;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/requests")
public class RequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        try (Connection con = DBConnection.getConnection()) {

            if ("fulfil".equals(action) || "delete".equals(action)) {
                String sql = "fulfil".equals(action)
                    ? "UPDATE emergency_request SET status='FULFILLED' WHERE request_id=?"
                    : "DELETE FROM emergency_request WHERE request_id=?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, Integer.parseInt(req.getParameter("id")));
                    ps.executeUpdate();
                }
                resp.sendRedirect("requests");
                return;
            }

            req.setAttribute("shelters", DBConnection.query(con, "SELECT shelter_id, name FROM shelter"));
            req.setAttribute("resources", DBConnection.query(con, "SELECT resource_id, name FROM resource"));
            req.setAttribute("requests", DBConnection.query(con,
                "SELECT r.request_id, s.name AS shelter, res.name AS resource, " +
                "r.quantity_needed, r.urgency, r.status " +
                "FROM emergency_request r " +
                "JOIN shelter s ON r.shelter_id = s.shelter_id " +
                "JOIN resource res ON r.resource_id = res.resource_id " +
                "ORDER BY (r.status='FULFILLED'), FIELD(r.urgency,'CRITICAL','HIGH','MEDIUM','LOW'), r.request_id"));
            req.getRequestDispatcher("/requests.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "INSERT INTO emergency_request (shelter_id, resource_id, quantity_needed, urgency) VALUES (?,?,?,?)")) {
            ps.setInt(1, Integer.parseInt(req.getParameter("shelter_id")));
            ps.setInt(2, Integer.parseInt(req.getParameter("resource_id")));
            ps.setInt(3, Integer.parseInt(req.getParameter("quantity_needed")));
            ps.setString(4, req.getParameter("urgency"));
            ps.executeUpdate();
            resp.sendRedirect("requests");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}