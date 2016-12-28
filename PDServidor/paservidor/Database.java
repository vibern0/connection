package paservidor;

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
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + serverName + "/clients.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully!");
            createTables();
        }
        catch (ClassNotFoundException | SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void close()
    {
        try
        {
            c.close();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createTables()
    {
        try
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
        catch (SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean addUser(String username, String password)
    {
        try
        {
            stmt = c.createStatement();
            
            String sql = "INSERT INTO users(name, password) VALUES('" +
                    username + "','" + password + "')"; 
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            
            return true;
        }
        catch (SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public int checkLogin(String username, String password)
    {
        if(!checkUser(username))
            return Properties.ERROR_ACCOUNT_NOT_FOUND;
        
        try
        {
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
        catch (SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Properties.ERROR_WRONG_PASSWORD;
    }
    
    public boolean checkUser(String username)
    {
        try
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
        catch (SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
