package pool;

import java.sql.Connection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The test of the pool class.
 * @author Julen
 */
public class PoolTest {
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
