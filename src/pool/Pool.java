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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manage connections to the database
 * 
 * @author Mikel
 */
public class Pool {

	private Stack<Connection> freeConnections = new Stack<Connection>();

	private Stack<Connection> usedConnections = new Stack<Connection>();

	protected ResourceBundle configFile = ResourceBundle.getBundle("dataAccess.config");
	protected String url = configFile.getString("URL"),
			user = configFile.getString("USER"),
			pass = configFile.getString("PASSWORD");

	protected int connectionLimit = Integer.parseInt(configFile.getString("CONNECTION_LIMIT"));

	/**
	 * Create the Pool object
	 *
	 * @param n number of freeConnections
	 * @throws exceptions.ConnectionErrorException
	 */
	public Pool(int n) throws ConnectionErrorException {
		this.createConnections(n);
	}

	public Pool() {
	}

	public int getFreeConnectionCount() {
		return freeConnections.size();
	}

	public int getUsedConnectionCount() {
		return usedConnections.size();
	}

	/**
	 * Creates connections, always respecting the connection limit
	 * 
	 * @param n
	 * @throws ConnectionErrorException
	 */
	private void createConnections(int n) throws ConnectionErrorException {
		// Create the requested new connections
		for (int i = 0; i < n; i++) {
			try {
				Connection newCon = DriverManager.getConnection(url, user, pass);
				freeConnections.push(newCon);
				if (freeConnections.size() + usedConnections.size() >= connectionLimit)
					throw new ConnectionErrorException();
			}catch (SQLException e) {
				// TODO Exception parametrization
				// error creating connection
				throw new ConnectionErrorException();
			}
                    // TODO Exception parametrization
                    // connection limit surpassed
                    
		}
	}

	/**
	 * Returns a connection to the database,
	 * if there aren't any free connections it creates one,
	 * the maximum amount of connections there can be is defined
	 * by the CONNECTION_LIMIT property
	 * 
	 * @return Connection a connection to be used
	 * @throws ConnectionErrorException
	 */
	public Connection getConnection() throws ConnectionErrorException {
		try {
			// Check if there are any free connections
			// create one if the stack is empty
			if (freeConnections.empty())
				this.createConnections(1);
			// Move connection from free to used and return it
			Connection con = freeConnections.pop();
			if (con == null)
				throw new ConnectionErrorException();
			usedConnections.add(con);
			return con;
		} catch (ConnectionErrorException e) {
			// TODO Exception parametrization
			// invalid connection
			throw new ConnectionErrorException();
		}
	}

	/**
	 * Receives a connection that's not being used anymore
	 * and stores it to be used later
	 * 
	 * @param Connection
	 */
	public void returnConnection(Connection con) throws ConnectionErrorException {
		try {
			if(con.isClosed())
				// TODO Exception parametrization
				// invalid connection
				throw new ConnectionErrorException();
			usedConnections.remove(con);
			freeConnections.push(con);
		} catch (SQLException e) {
			// TODO Exception parametrization
			// invalid connection
			throw new ConnectionErrorException();
		}
	}
        
        /**
	 * Kill all connections
	 * 
	 * @throws ConnectionErrorException
	 */
	public void killAllConnections() throws ConnectionErrorException {
		// Get all open connections
		List<Connection> allCons = this.getAllConnections();
		// Iterate over all connections and close them
		allCons.stream()
				.forEach(con -> {
					try {
						con.close();
					} catch (SQLException ex) {
						Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
					}
				});
		cleanClosedConnections();
	}

	/**
	 * Returns a list of all the open connections
	 * 
	 * @return
	 */
	private List<Connection> getAllConnections() {
		List<Connection> allCons = new ArrayList<Connection>();
		freeConnections.stream()
				.forEach(con -> allCons.add(con));
		usedConnections.stream()
				.forEach(con -> allCons.add(con));
		return allCons;
	}

	/**
	 * Removes all the closed connections from both
	 * free and used collections
	 */
	private void cleanClosedConnections() {
		// Remove closed free connections
		freeConnections.forEach((con) -> {
			try {
				if (con.isClosed())
					freeConnections.remove(con);
			} catch (SQLException ex) {
				Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		// Remove closed used connections
		usedConnections.forEach((con) -> {
			try {
				if (con.isClosed())
					usedConnections.remove(con);
			} catch (SQLException ex) {
				Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
	}
}

