package model;

import com.mysql.jdbc.Connection;
import datatransferobject.Model;
import datatransferobject.User;
import datatransferobject.UserPrivilege;
import datatransferobject.UserStatus;
import exceptions.ConnectionErrorException;
import exceptions.InvalidUserException;
import exceptions.MaxConnectionExceededException;
import exceptions.TimeOutException;
import exceptions.UserExistException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pool.Pool;

/**
 * The test of the DAO class
 * @author Haizea
 */
public class DAOTest {
    private static PreparedStatement stmt;
    private static Connection con;
    private ResultSet rs;
    private static Model model;
    private static Pool pool;
    private final String deleteSignIn = "DELETE FROM signin WHERE user_id LIKE (SELECT user_id FROM user WHERE login LIKE ?)";
    private final String checkSignIn = "SELECT * FROM signIn WHERE user_id LIKE (SELECT user_id FROM user WHERE login LIKE ?)";
    private final String deleteUser = "DELETE FROM user WHERE login LIKE ?";
    private final String checkUser = "SELECT * FROM user WHERE login LIKE ?";
    private static final Logger LOGGER = Logger.getLogger("DAOTest.class");
    
    @BeforeClass
    public static void setUpClass() {
        model = DAOFactory.getModel();
        pool = new Pool();
        try {
            con = (Connection) pool.getConnection();
        } catch (ConnectionErrorException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
    }
    
    @AfterClass
    public static void setDownClass() {
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
    
    /**
     * Test of doSignIn method with a not registered user, of class DAO.
     */
    @Test(expected = InvalidUserException.class)
    public void testDoSignInInvalidUser() throws InvalidUserException, MaxConnectionExceededException, ConnectionErrorException, TimeOutException {
        User user = new User();
        user.setLogin("testAdrian");
        user.setPassword("contraseña");
        deleteSignIn(user);
        model.doSignIn(user);
    }
    
    /**
     * Test of doSignIn method with a registered user, of class DAO.
     */
    @Test
    public void testDoSignIn() throws InvalidUserException, MaxConnectionExceededException, ConnectionErrorException, TimeOutException, UserExistException {
        User user = new User("testAdrian","adrian.morales@gmail.com","Adrian Alvesito",UserStatus.ENABLED,UserPrivilege.ADMIN,"contraseña",new Timestamp(System.currentTimeMillis()));
        deleteUser(user);
        model.doSignUp(user);
        model.doSignIn(user);
        try {
            stmt = con.prepareStatement(checkSignIn);
            stmt.setString(1,user.getLogin());
            rs = stmt.executeQuery();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
        assertNotNull(rs);
        deleteUser(user);
    }
    
    /**
     * Test of doSignUp method with an already registered user, of class DAO.
     */
    @Test(expected = UserExistException.class)
    public void testDoSignUpUserExist() throws InvalidUserException, TimeOutException, MaxConnectionExceededException, ConnectionErrorException, UserExistException {
        User user = new User("userTest","userTest@gmail.com","userTest",UserStatus.ENABLED,UserPrivilege.ADMIN,"abcd*1234",new Timestamp(System.currentTimeMillis()));
        model.doSignUp(user);
    }
    
    /**
     * Test of doSignUp method with a not registered user, of class DAO.
     */
    @Test
    public void testDoSignUp() throws InvalidUserException, TimeOutException, MaxConnectionExceededException, ConnectionErrorException, UserExistException {
        User user = new User("testAdrian","adrian.morales@gmail.com","Adrian Alvesito",UserStatus.ENABLED,UserPrivilege.ADMIN,"contraseña",new Timestamp(System.currentTimeMillis()));
        deleteUser(user);
        model.doSignUp(user);
        try {
            stmt = con.prepareStatement(checkUser);
            stmt.setString(1,user.getLogin());
            rs = stmt.executeQuery();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
        assertNotNull(rs);
        deleteUser(user);
    }
    
    /**
     * Method to delete the signIns of the specific user.
     * @param user the user whose signins has to be deleted.
     */
    private void deleteSignIn(User user) {
        try {
            stmt = con.prepareStatement(checkSignIn);
            stmt.setString(1,user.getLogin());
            rs = stmt.executeQuery();
            if (rs.next()) {
                stmt = con.prepareStatement(deleteSignIn);
                stmt.setString(1,user.getLogin());
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
    }
    
    /**
     * Method to delete the specific user.
     * @param user the user that has to be deleted.
     */
    private void deleteUser(User user) {
        try {
            stmt = con.prepareStatement(checkUser);
            stmt.setString(1,user.getLogin());
            rs = stmt.executeQuery();
            if (rs.next()) {
                stmt = con.prepareStatement(deleteUser);
                stmt.setString(1,user.getLogin());
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
    }
    
}
