/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import pool.Pool;

/**
 *
 * @author Sendoa, Haizea, Julen y Mikel
 */
public class DAO implements Model {

    private Connection con;
    Pool pool = new Pool();
    private PreparedStatement stmt;
    private final String signUp = "INSERT INTO USER VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";
    private final String signIn = "SELECT u.* FROM user u WHERE login = ? AND password = ?";
    private final String insertSignIn = "INSERT INTO signin (user_id, lastSignIn) SELECT user_id, CURRENT_TIME() FROM user WHERE login = ?";

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
            }

            stmt = con.prepareStatement(insertSignIn);
            stmt.setString(1, user.getLogin());
            stmt.executeUpdate();

            if (userN == null || !userN.getPassword().equals(user.getPassword())) {
                throw new InvalidUserException();
            }

            return user;

        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new ConnectionErrorException("Connection error with the database. Try again later.");
        } finally {
            closeConnection();
        }
    }

    /**
     * Method to register a new user
     *
     * @param user the user that is going to be saved in the DB
     * @return the user if there is no error otherwise returns null
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
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserExistException();
        } catch (Exception ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new ConnectionErrorException("Connection error with the database. Try again later.");
        } finally {
            closeConnection();
        }
    }

    public void closeConnection() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                pool.releaseConnection(con);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

}
