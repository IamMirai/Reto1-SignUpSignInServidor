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
 * This class manages connections with the database.
 * @author Haizea and Julen
 */
public class Pool {
    private final ResourceBundle bundle = ResourceBundle.getBundle("pool.config");
    private final String url = bundle.getString("URL");
    private final String user = bundle.getString("USER");
    private final String password = bundle.getString("PASS");
    private final int maxConnections = Integer.parseInt(bundle.getString("MAX_CONNECTIONS"));
    private static Stack<Connection> usedConnections = new Stack<>();
    private static Stack<Connection> releasedConnections = new Stack<>();
    Connection connection = null;
    private static final Logger LOGGER = Logger.getLogger("Pool.class");
    
    /**
     * This method returns a connection with the database. If there are no free connections and the maximum has not been reached it creates a new one.
     * @return the connection with the database.
     * @throws ConnectionErrorException This exception is thrown if the database requests exceed.
     */
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
    
    /**
     * This method releases a connection.
     * @param connection the connection that has to be released.
     * @return a boolean that checks if the method went well.
     */
    public boolean releaseConnection(Connection connection) {
        boolean releaseConnectionsOrNot = false;
        releasedConnections.push(connection);
        usedConnections.remove(connection);
        if(!releasedConnections.isEmpty()){
            releaseConnectionsOrNot = true;
        }
        return releaseConnectionsOrNot;
    }
    
    /**
     * This method creates a new connection.
     * @return the new connection.
     */
    public Connection createConnection() {
        try {
            connection = DriverManager.getConnection(url,user,password); 
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
        return connection;
    }
    
    /**
     * This method closes all connections.
     * @return a boolean that checks if the method went well.
     */
    public static boolean closeAllConnections() {
        boolean closedConnectionsOrNot = false;
        for (int i = 0; i < releasedConnections.size(); i++) {
            try {
                releasedConnections.get(i).close();
            }catch(SQLException ex) {
                LOGGER.log(Level.SEVERE,ex.getMessage());
            }
        }
        for (int i = 0; i < usedConnections.size(); i++) {
            try {
                usedConnections.get(i).close();
            }catch(SQLException ex) {
                LOGGER.log(Level.SEVERE,ex.getMessage());
            }
        }
        if(releasedConnections.isEmpty() && usedConnections.isEmpty()){
            closedConnectionsOrNot = true;
        }
        return closedConnectionsOrNot;
    }
}
