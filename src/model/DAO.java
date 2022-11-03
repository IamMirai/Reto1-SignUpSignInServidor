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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author haize
 */
public class DAO implements Model {
    
    /**
     * the value of the connection is given
     * at the constructor. should be used with
     * every database usage in this class.
     */

    private final Connection con;

    /**
     * to the database is necessary to give a connection
     * to this database.
     * 
     * @param pConnection will be used by every module.
     */

    public DAO(Connection pConnection) {
        con = pConnection;
    }

    /**
     * selects a user from the table using
     * the values of the login and the password.
     * theoretically, the user should have only,
     * and this means ONLY, the login and the password.
     * 
     * @param pUser object that contains the credentials.
     * @return a new User with all it's data.
     * if there's nothing found, an empty (null) value.
     */


    /**
     * @param pID is the ID of the user whose logins
     *            will be searched.
     * @return a List of Timestamps, chronologically sorted.
     */

    private List<Timestamp> selectLastSignIns(int pID) {
        List<Timestamp> l = 
            new ArrayList<Timestamp>();

        try {
            stmt = con.prepareStatement(lastSignIns);
            stmt.setInt(0, pID);
                ResultSet rs = stmt.executeQuery();

            while (rs.next())
                l.add(rs.getTimestamp(1));

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        l.stream()
                .sorted((t1, t2) -> t1.compareTo(t2));

        return l;
    }

    /**
     * this method generates the ID 
     * correspondant to the new user to be written
     * to this database. in any case, it's an ID
     * that IT'S NOT BEING USED YET.
     * 
     * @return a new ID.
     */
    
    public int generateID() {
        List <Integer> l = 
            new ArrayList<Integer>();

        try {

            /**
             * note that the PreparedStatement is not being used.
             * if we did, we would modify the prepared statement
             * from the function calling this code and this could
             * give errors.
             */

            ResultSet rs = con.prepareStatement(everyID)
                    .executeQuery();

            while (rs.next())
                l.add(rs.getInt(0));

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        if (l.isEmpty())
            return 1;

        else if (l.contains(l.size()))
            return l.size() + 1;

        /**
         * in case of not having a "perfect" case
         * of IDs, we give the new one.
         */
        return l.stream()
            .max((i1, i2) -> i1 - i2).get() + 1;

    }   

    /**
     * just a simple getter.
     * @return the connection being 
     * used in this class.
     */

    public Connection getConnection() {
        return con;
    }

    // we do not have to create in every module.
    private PreparedStatement stmt;

    private final String count = 
        "SELECT COUNT(*) FROM user";

    private final String everyID = 
        "SELECT ID from user";

    private final String lastSignIns =
         "SELECT lastSignIn from signIn WHERE id = ?";

    private final String signUp = 
        "INSERT INTO USER VALUES (?, ?, ?, ?, ?, ?, ?, ?), COUNT(*)";

    private final String signIn = 
        "SELECT login, password FROM user WHERE login = ? AND password = ?";
    @Override
    public Package doSignIn(User user) {
        try {
            stmt = con.prepareStatement(signIn);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();

            /**
             * the values of the last SignIns are obtained
             * by using the ID to seach it.
             */
            User user1;
            if (rs.next())
                 user1= new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getTimestamp(6),
                        rs.getInt(7),
                        rs.getInt(8),
                        selectLastSignIns(rs.getInt(0)));
                return new Package(user, MessageEnum.AN_OK);

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } catch (NullPointerException npe) {
            // this should never happen.
            npe.printStackTrace();
        }

        return null;
    }

    @Override
    public Package doSignUp(User user) {
        try {
            stmt = con.prepareStatement(signUp);
            stmt.setInt(0, generateID());
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPassword());
            stmt.setTimestamp(5, user.getLastPasswordChange());
            
            stmt.setInt(6,
                    (user.getPrivilege() == UserPrivilege.ADMIN) ? 1 : 0);

            stmt.setInt(7,
                    (user.getStatus() == UserStatus.ENABLED) ? 1 : 0);

            stmt.executeUpdate();
            Package pack = new Package(user, MessageEnum.AN_OK);
            return pack;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }
    
}
