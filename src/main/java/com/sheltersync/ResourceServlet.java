package com.sheltersync;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/resources")
public class ResourceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection()) {
            if ("delete".equals(req.getParameter("action"))) {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM resource WHERE resource_id=?")) {
                    ps.setInt(1, Integer.parseInt(req.getParameter("id")));
                    ps.executeUpdate();
                } catch (SQLException e) {
                    req.setAttribute("error", "Cannot delete: requests or distributions use this resource.");
                }
            }
            req.setAttribute("resources", DBConnection.query(con,
                "SELECT * FROM resource ORDER BY resource_id"));
            req.getRequestDispatcher("/resources.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String rid = req.getParameter("resource_id");
        try (Connection con = DBConnection.getConnection()) {
            if (rid != null && !rid.isEmpty()) {            // RESTOCK existing
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE resource SET quantity = quantity + ? WHERE resource_id=?")) {
                    ps.setInt(1, Integer.parseInt(req.getParameter("add_qty")));
                    ps.setInt(2, Integer.parseInt(rid));
                    ps.executeUpdate();
                }
            } else {                                         // ADD new
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO resource (name, category, quantity) VALUES (?,?,?)")) {
                    ps.setString(1, req.getParameter("name"));
                    ps.setString(2, req.getParameter("category"));
                    ps.setInt(3, Integer.parseInt(req.getParameter("quantity")));
                    ps.executeUpdate();
                }
            }
            resp.sendRedirect("resources");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}