/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import controller.Worker;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class WorkerTest {
    
    private Socket skt;
    
    private ResourceBundle bundle = ResourceBundle.getBundle("pool.config");
    public WorkerTest() {
        
    }
    /**
     * Test of run method, of class Worker.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        ServerSocket scktServer;
        try {
            scktServer = new ServerSocket(Integer.parseInt(bundle.getString("PORT")));
            Socket scktClient = scktServer.accept();
            Worker instance = new Worker(scktClient);
            instance.run();
        } catch (IOException ex) {
            Logger.getLogger(WorkerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
