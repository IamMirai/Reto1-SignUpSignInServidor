/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pool;

import java.sql.Connection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 2dam
 */
public class PoolTest {
    
    public PoolTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getConnection method, of class Pool.
     */
    @Test
    public void testGetConnection() throws Exception {
        Pool pool = new Pool();
        Connection expResult = null;
        Connection result = pool.getConnection();
        assertNotEquals(expResult, result);
    }

    /**
     * Test of releaseConnection method, of class Pool.
     */
    @Test
    public void testReleaseConnection() {
        boolean releaseConnectionsOrNot = false;
        Connection connection = null;
        Pool pool = new Pool();
        releaseConnectionsOrNot = pool.releaseConnection(connection);
        assertTrue(releaseConnectionsOrNot);
    }

    /**
     * Test of createConnection method, of class Pool.
     */
    @Test
    public void testCreateConnection() {
        Pool pool = new Pool();
        Connection expResult = null;
        Connection result = pool.createConnection();
        assertNotEquals(expResult, result);
    }

    /**
     * Test of closeAllConnections method, of class Pool.
     */
    @Test
    public void testCloseAllConnections() {
        boolean closedConnectionsOrNot = false;
        closedConnectionsOrNot = Pool.closeAllConnections();
        assertTrue(closedConnectionsOrNot);
    }
    
}
