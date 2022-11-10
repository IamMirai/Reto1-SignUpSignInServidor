package model;

import datatransferobject.Model;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The test of the DAOFactory class.
 * @author Haizea
 */
public class DAOFactoryTest {
    
    /**
     * Test of getModel method, of class DAOFactory.
     */
    @Test
    public void testGetModel() {
        Model result = DAOFactory.getModel();
        assertNotNull("Null model", result);
        assertTrue("Instance of model is not ModelImplementation", result instanceof DAO);
    }
    
}
