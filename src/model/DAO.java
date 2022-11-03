/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import datatransferobject.MessageEnum;
import datatransferobject.Model;
import datatransferobject.Package;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sendoa, Haizea, Julen y Mikel
 */
public class DAO implements Model {

    private final Connection con;

    public DAO(Connection pConnection) {
        con = pConnection;
    }

    public Connection getConnection() {
        return con;
    }

    private PreparedStatement stmt;

    private final String signUp
            = "INSERT INTO USER VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";

    private final String signIn
            = "SELECT u.* FROM user u WHERE login = ? AND password = ?";
    
    private final String insertSignIn 
            = "INSERT INTO signin VALUES (?, CURRENT_TIME())";

    
    @Override
    public User doSignIn(User user) throws InvalidUserException, ConnectionErrorException, TimeOutException, MaxConnectionExceededException {
        try {
            stmt = con.prepareStatement(signIn);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();

            User user1 = null;
            if (rs.next()) {
                user1 = new User();

                user.setId(rs.getInt("user_id"));
                user.setLogin(rs.getString("login"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("fullName"));
                if (rs.getInt("status") == 1) {
                    user.setStatus(UserStatus.ENABLED);
                } else {
                    user.setStatus(UserStatus.DISABLED);
                }
                if (rs.getInt("privilege") == 1) {
                    user.setPrivilege(UserPrivilege.USER);
                } else {
                    user.setPrivilege(UserPrivilege.ADMIN);
                }
                user.setPassword(rs.getString("password"));
                user.setLastPasswordChange(rs.getTimestamp("lastPasswordChange"));
            }

            stmt = con.prepareStatement(insertSignIn);
            stmt.setInt(1, user1.getId());
            stmt.executeUpdate();
            
            return user;

        } catch (SQLException sqle) {
            return null;
        } 
    }

    @Override
    public User doSignUp(User user) throws UserExistException, ConnectionErrorException, TimeOutException, MaxConnectionExceededException {
        try {
            stmt = con.prepareStatement(signUp);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            if (user.getStatus().equals(UserStatus.ENABLED)){
                stmt.setInt(4, 1);
            } else {
                stmt.setInt(4, 2);
            }
            if (user.getPrivilege().equals(UserPrivilege.USER)){
                stmt.setInt(5, 1);
            } else {
                stmt.setInt(5, 2);
            }
            stmt.setString(6, user.getPassword());
            stmt.setTimestamp(7, user.getLastPasswordChange());

            stmt.executeUpdate();
            return user;
        } catch (SQLException sqle) {
            return null;
        }
    }

}
