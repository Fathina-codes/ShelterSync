package com.sheltersync;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/distribution")
public class DistributionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection()) {
            req.setAttribute("pending", DBConnection.query(con,
                "SELECT r.request_id, r.shelter_id, r.resource_id, r.quantity_needed, r.urgency, " +
                "s.name AS shelter, res.name AS resource " +
                "FROM emergency_request r " +
                "JOIN shelter s ON r.shelter_id = s.shelter_id " +
                "JOIN resource res ON r.resource_id = res.resource_id " +
                "WHERE r.status='PENDING' " +
                "ORDER BY FIELD(r.urgency,'CRITICAL','HIGH','MEDIUM','LOW'), r.request_id"));
            req.setAttribute("shelters", DBConnection.query(con, "SELECT shelter_id, name FROM shelter ORDER BY shelter_id"));
            req.setAttribute("resources", DBConnection.query(con,
                "SELECT resource_id, name, quantity FROM resource"));
            req.setAttribute("history", DBConnection.query(con,
                "SELECT d.dist_id, s.name AS shelter, r.name AS resource, d.quantity, d.dist_date " +
                "FROM distribution d JOIN shelter s ON d.shelter_id = s.shelter_id " +
                "JOIN resource r ON d.resource_id = r.resource_id ORDER BY d.dist_id DESC"));
            req.getRequestDispatcher("/distribution.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int sid = Integer.parseInt(req.getParameter("shelter_id"));
        int rid = Integer.parseInt(req.getParameter("resource_id"));
        int qty = Integer.parseInt(req.getParameter("quantity"));

        try (Connection con = DBConnection.getConnection()) {
            int stock = 0;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT quantity FROM resource WHERE resource_id=?")) {
                ps.setInt(1, rid);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) stock = rs.getInt(1);
            }

            if (qty > stock) {                       // stock check
                req.setAttribute("error", "Not enough stock. Only " + stock + " units available.");
                doGet(req, resp);
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO distribution (shelter_id, resource_id, quantity) VALUES (?,?,?)")) {
                ps.setInt(1, sid);
                ps.setInt(2, rid);
                ps.setInt(3, qty);
                ps.executeUpdate();                  // trigger reduces resource.quantity
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE emergency_request SET status='FULFILLED' " +
                    "WHERE shelter_id=? AND resource_id=? AND status='PENDING' AND quantity_needed <= ? " +
                    "ORDER BY FIELD(urgency,'CRITICAL','HIGH','MEDIUM','LOW'), request_id LIMIT 1")) {
                ps.setInt(1, sid);
                ps.setInt(2, rid);
                ps.setInt(3, qty);
                ps.executeUpdate();
            }
            resp.sendRedirect("distribution");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}