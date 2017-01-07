package pdservidor;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */
public class Database {
    
    private Connection c = null;
    private Statement stmt = null;
        
    public Database(String serverName)
            throws ClassNotFoundException, SQLException
    {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:" + serverName + "/clients.db");
        c.setAutoCommit(false);
        System.out.println("Opened database successfully!");
        createTables();
    }
    
    public void close() throws SQLException
    {
        c.close();
    }
    
    private void createTables()
            throws SQLException
    {
        stmt = c.createStatement();

        String sql = "CREATE TABLE  IF NOT EXISTS users " +
               "(id             INTEGER PRIMARY KEY   AUTOINCREMENT," +
               " name           TEXT    NOT NULL," + 
               " password       TEXT    NOT NULL)"; 
        stmt.executeUpdate(sql);

        stmt.close();
        c.commit();

        System.out.println("Tables created!");
    }
    
    public void addUser(String username, String password)
            throws SQLException
    {
        stmt = c.createStatement();

        String sql = "INSERT INTO users(name, password) VALUES('" +
                username + "','" + password + "')"; 
        stmt.executeUpdate(sql);

        stmt.close();
        c.commit();
    }
    
    public int checkLogin(String username, String password)
            throws SQLException
    {
        if(!checkUser(username))
            return Properties.ERROR_ACCOUNT_NOT_FOUND;
        
        Integer found = Properties.ERROR_WRONG_PASSWORD;
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM users WHERE name='" +
                username + "' AND password='" + password + "'");

        if(rs.next())
        {
            found = Properties.SUCCESS_LOGGED;
        }
        rs.close();
        stmt.close();

        return found;
    }
    
    public boolean checkUser(String username)
            throws SQLException
    {
        boolean found = false;
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM users WHERE name='" + username + "'");

        if(rs.next())
        {
            found = true;
        }
        rs.close();
        stmt.close();

        return found;
    }
}
