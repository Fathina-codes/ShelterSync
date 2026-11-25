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

@WebServlet("/persons")
public class PersonServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try (Connection con = DBConnection.getConnection()) {

            if ("delete".equals(req.getParameter("action"))) {
                int pid = Integer.parseInt(req.getParameter("id"));
                con.setAutoCommit(false);
                try {
                    Integer sid = null;
                    try (PreparedStatement ps = con.prepareStatement(
                            "SELECT shelter_id FROM affected_person WHERE person_id=? FOR UPDATE")) {
                        ps.setInt(1, pid);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) sid = rs.getInt(1);
                        }
                    }

                    if (sid != null) {
                        try (PreparedStatement ps = con.prepareStatement(
                                "SELECT shelter_id FROM shelter WHERE shelter_id=? FOR UPDATE")) {
                            ps.setInt(1, sid);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    throw new SQLException("Shelter not found for affected person " + pid);
                                }
                            }
                        }

                        int removed;
                        try (PreparedStatement ps = con.prepareStatement(
                                "DELETE FROM affected_person WHERE person_id=?")) {
                            ps.setInt(1, pid);
                            removed = ps.executeUpdate();
                        }

                        if (removed > 0) {
                            try (PreparedStatement ps = con.prepareStatement(
                                    "UPDATE shelter SET occupancy = " +
                                    "(SELECT COUNT(*) FROM affected_person WHERE shelter_id=?) " +
                                    "WHERE shelter_id=?")) {
                                ps.setInt(1, sid);
                                ps.setInt(2, sid);
                                ps.executeUpdate();
                            }
                        }
                    }
                    con.commit();
                } catch (SQLException e) {
                    con.rollback();
                    throw e;
                }
                resp.sendRedirect("persons");
                return;
            }

            req.setAttribute("shelters", DBConnection.query(con,
                "SELECT s.shelter_id, s.name, s.capacity, COUNT(p.person_id) AS occupancy " +
                "FROM shelter s LEFT JOIN affected_person p ON s.shelter_id = p.shelter_id " +
                "GROUP BY s.shelter_id, s.name, s.capacity ORDER BY s.shelter_id"));
            req.setAttribute("persons", DBConnection.query(con,
                "SELECT p.*, s.name AS shelter FROM affected_person p " +
                "JOIN shelter s ON p.shelter_id = s.shelter_id ORDER BY p.person_id DESC"));
            req.getRequestDispatcher("/persons.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int sid = Integer.parseInt(req.getParameter("shelter_id"));
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int capacity = -1;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT capacity FROM shelter WHERE shelter_id=? FOR UPDATE")) {
                    ps.setInt(1, sid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) capacity = rs.getInt("capacity");
                    }
                }

                int occupancy = 0;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT COUNT(*) FROM affected_person WHERE shelter_id=?")) {
                    ps.setInt(1, sid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) occupancy = rs.getInt(1);
                    }
                }

                boolean hasRoom = capacity >= 0 && occupancy < capacity;
                if (!hasRoom) {                       // capacity check
                    con.rollback();
                    req.setAttribute("error", "This shelter is full. Please choose another shelter.");
                    doGet(req, resp);
                    return;
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO affected_person (name, age, gender, contact, shelter_id) VALUES (?,?,?,?,?)")) {
                    ps.setString(1, req.getParameter("name"));
                    ps.setInt(2, Integer.parseInt(req.getParameter("age")));
                    ps.setString(3, req.getParameter("gender"));
                    ps.setString(4, req.getParameter("contact"));
                    ps.setInt(5, sid);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE shelter SET occupancy = " +
                        "(SELECT COUNT(*) FROM affected_person WHERE shelter_id=?) WHERE shelter_id=?")) {
                    ps.setInt(1, sid);
                    ps.setInt(2, sid);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
            resp.sendRedirect("persons");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}