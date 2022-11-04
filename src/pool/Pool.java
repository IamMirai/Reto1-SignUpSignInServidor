/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pool;

import exceptions.ConnectionErrorException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manage connections to the database
 * 
 * @author joana
 */
public class Pool {
    private ResourceBundle bundle = ResourceBundle.getBundle("pool.config");
    private final String url = bundle.getString("URL");
    private final String user = bundle.getString("USER");
    private final String password = bundle.getString("PASS");
    private final int maxConnections = Integer.parseInt(bundle.getString("MAX_CONNECTIONS"));
    private static Stack<Connection> usedConnections = new Stack<>();
    private static Stack<Connection> releasedConnections = new Stack<>();
    Connection connection = null;
    
    public synchronized Connection getConnection() throws ConnectionErrorException {
        if ((usedConnections.size() + releasedConnections.size()) > maxConnections) {
            throw new ConnectionErrorException("Maximum number of requests reached. Try it again later.");
        } else if (releasedConnections.empty()) {
            connection = createConnection();
            usedConnections.push(connection);
        } else {
            connection = releasedConnections.pop();
            usedConnections.push(connection);
        }
        return connection;
    }
    
    public void releaseConnection(Connection connection) {
        releasedConnections.push(connection);
        usedConnections.remove(connection);
    }
    
    public Connection createConnection() {
        try {
            connection = DriverManager.getConnection(url,user,password); 
        } catch (SQLException ex) {
            Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }
    
    public static void closeAllConnections() {
        for (int i = 0; i < releasedConnections.size(); i++) {
            try {
                releasedConnections.get(i).close();
            }catch(SQLException ex) {
                Logger.getLogger(Pool.class.getName()).log(Level.SEVERE,null,ex);
            }
        }
        for (int i = 0; i < usedConnections.size(); i++) {
            try {
                usedConnections.get(i).close();
            }catch(SQLException ex) {
                Logger.getLogger(Pool.class.getName()).log(Level.SEVERE,null,ex);
            }
        }
    }
}

