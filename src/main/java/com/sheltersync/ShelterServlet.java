package com.sheltersync;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/shelters")
public class ShelterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        try (Connection con = DBConnection.getConnection()) {

            // DELETE
            if ("delete".equals(action)) {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM shelter WHERE shelter_id=?")) {
                    ps.setInt(1, Integer.parseInt(req.getParameter("id")));
                    ps.executeUpdate();
                } catch (SQLException e) {
                    req.setAttribute("error",
                        "Cannot delete: people or requests are linked to this shelter.");
                }
            }

            // EDIT (load one row into the form)
            if ("edit".equals(action)) {
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM shelter WHERE shelter_id=?")) {
                    ps.setInt(1, Integer.parseInt(req.getParameter("id")));
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        Map<String, Object> e = new HashMap<>();
                        e.put("id", rs.getInt("shelter_id"));
                        e.put("name", rs.getString("name"));
                        e.put("location", rs.getString("location"));
                        e.put("capacity", rs.getInt("capacity"));
                        req.setAttribute("edit", e);
                    }
                }
            }

            // LIST
            try (Statement st = con.createStatement()) {
                st.executeUpdate(
                        "UPDATE shelter s SET occupancy = " +
                        "(SELECT COUNT(*) FROM affected_person p WHERE p.shelter_id = s.shelter_id)");
            }

            List<Map<String, Object>> list = new ArrayList<>();
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT s.shelter_id, s.name, s.location, s.capacity, " +
                         "COUNT(p.person_id) AS occupancy, " +
                         "s.capacity - COUNT(p.person_id) AS available " +
                         "FROM shelter s LEFT JOIN affected_person p ON s.shelter_id = p.shelter_id " +
                         "GROUP BY s.shelter_id, s.name, s.location, s.capacity " +
                         "ORDER BY s.shelter_id")) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", rs.getInt("shelter_id"));
                    m.put("name", rs.getString("name"));
                    m.put("location", rs.getString("location"));
                    m.put("capacity", rs.getInt("capacity"));
                    m.put("occupancy", rs.getInt("occupancy"));
                    m.put("available", rs.getInt("available"));
                    list.add(m);
                }
            }
            req.setAttribute("shelters", list);
            req.getRequestDispatcher("/shelters.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String location = req.getParameter("location");
        int capacity = Integer.parseInt(req.getParameter("capacity"));

        try (Connection con = DBConnection.getConnection()) {
            if (id == null || id.isEmpty()) {              // ADD
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO shelter (name, location, capacity) VALUES (?,?,?)")) {
                    ps.setString(1, name);
                    ps.setString(2, location);
                    ps.setInt(3, capacity);
                    ps.executeUpdate();
                }
            } else {                                       // UPDATE
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE shelter SET name=?, location=?, capacity=? WHERE shelter_id=?")) {
                    ps.setString(1, name);
                    ps.setString(2, location);
                    ps.setInt(3, capacity);
                    ps.setInt(4, Integer.parseInt(id));
                    ps.executeUpdate();
                }
            }
            resp.sendRedirect("shelters");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}