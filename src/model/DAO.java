package model;

import datatransferobject.Model;
import datatransferobject.User;
import datatransferobject.UserPrivilege;
import datatransferobject.UserStatus;
import exceptions.ConnectionErrorException;
import exceptions.InvalidUserException;
import exceptions.MaxConnectionExceededException;
import exceptions.TimeOutException;
import exceptions.UserExistException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pool.Pool;

/**
 * @author Sendoa, Haizea, Julen y Mikel
 * This class is the implementation of the model which perfoms the actions with the database.
 */
public class DAO implements Model {

    private Connection con;
    Pool pool = new Pool();
    private PreparedStatement stmt;
    private final String signUp = "INSERT INTO USER VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";
    private final String signIn = "SELECT u.* FROM user u WHERE login = ? AND password = ?";
    private final String insertSignIn = "INSERT INTO signin (user_id, lastSignIn) SELECT user_id, CURRENT_TIME() FROM user WHERE login = ?";
    private static final Logger LOGGER = Logger.getLogger("DAO.class");
    
    /**
     * Method to do the sign in of a client
     *
     * @param user the user that has to be checked if it exists
     * @return the user if it finds one
     * @throws InvalidUserException the specified user does not exist
     * @throws ConnectionErrorException a connection error ocurred while trying
     * to connect to the DB
     * @throws TimeOutException can't connect to the DB
     * @throws MaxConnectionExceededException the maximum connection number was
     * exceeded
     */
    @Override
    public User doSignIn(User user) throws InvalidUserException, TimeOutException, MaxConnectionExceededException, ConnectionErrorException {

        try {
            con = pool.getConnection();
            stmt = con.prepareStatement(signIn);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();

            User userN = null;
            if (rs.next()) {
                userN = new User();

                userN.setLogin(rs.getString("login"));
                userN.setEmail(rs.getString("email"));
                userN.setFullName(rs.getString("fullName"));
                if (UserStatus.valueOf(rs.getString("status")).equals(UserStatus.ENABLED)) {
                    userN.setStatus(UserStatus.ENABLED);
                } else {
                    userN.setStatus(UserStatus.DISABLED);
                }
                if (UserPrivilege.valueOf(rs.getString("privilege")).equals(UserPrivilege.USER)) {
                    userN.setPrivilege(UserPrivilege.USER);
                } else {
                    userN.setPrivilege(UserPrivilege.ADMIN);
                }
                userN.setPassword(rs.getString("password"));
                userN.setLastPasswordChange(rs.getTimestamp("lastPasswordChange"));
                
                stmt = con.prepareStatement(insertSignIn);
                stmt.setString(1, user.getLogin());
                stmt.executeUpdate();
            }

            if (userN == null || !userN.getPassword().equals(user.getPassword())) {
                throw new InvalidUserException();
            }

            return userN;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
            throw new ConnectionErrorException("Connection error with the database. Try again later.");
        } finally {
            closeConnection();
        }
    }

    /**
     * Method to register a new user
     *
     * @param user the user that is going to be saved in the DB
     * @throws UserExistException the specified user already exists
     * @throws ConnectionErrorException a connection error ocurred while trying
     * to connect to the DB
     * @throws TimeOutException can't connect to the DB
     * @throws MaxConnectionExceededException the maximum connection number was
     * exceeded
     */
    @Override
    public void doSignUp(User user) throws UserExistException, TimeOutException, MaxConnectionExceededException, ConnectionErrorException {

        try {
            con = pool.getConnection();
            stmt = con.prepareStatement(signUp);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            if (user.getStatus().equals(UserStatus.ENABLED)) {
                stmt.setInt(4, 1);
            } else {
                stmt.setInt(4, 2);
            }
            if (user.getPrivilege().equals(UserPrivilege.USER)) {
                stmt.setInt(5, 1);
            } else {
                stmt.setInt(5, 2);
            }
            stmt.setString(6, user.getPassword());
            stmt.setTimestamp(7, user.getLastPasswordChange());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
            throw new UserExistException();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
            throw new ConnectionErrorException("Connection error with the database. Try again later.");
        } finally {
            closeConnection();
        }
    }
    
    /**
     * This method closes the preparedStatement and releases the connection if they are not null.
     */
    public void closeConnection() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                pool.releaseConnection(con);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
    }

}
