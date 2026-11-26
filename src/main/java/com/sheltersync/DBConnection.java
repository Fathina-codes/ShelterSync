package com.sheltersync;

import java.sql.*;
import java.util.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sheltersync";
    private static final String USER = "root";
    private static final String PASS = "*********";

    public static Connection getConnection() throws SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new SQLException(e); }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Runs a SELECT and returns rows as a list of maps (column name -> value)
    public static List<Map<String, Object>> query(Connection con, String sql) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                for (int i = 1; i <= md.getColumnCount(); i++)
                    m.put(md.getColumnLabel(i), rs.getObject(i));
                list.add(m);
            }
        }
        return list;
    }
}