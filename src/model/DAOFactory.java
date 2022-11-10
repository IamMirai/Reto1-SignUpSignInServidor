package model;

import datatransferobject.Model;

/**
 * @author Haizea
 * This class is the Data Factory of the DAO.
 */
public class DAOFactory {
        private static Model model;
    /**
     * This is the method which creates the implementation.
     * @return the model implementation (DAO). 
     */
    public static Model getModel() {
        if (model == null) {
            model = new DAO();
        }
        return model;
    }
}
