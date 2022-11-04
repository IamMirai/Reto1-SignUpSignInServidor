/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import controller.Worker;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 2dam
 */
public class Application {
    private ServerSocket scktServer;
    private Socket scktClient;
    private final ResourceBundle bundle = ResourceBundle.getBundle("pool.config");
    private final Integer MAX_CONNECTIONS = Integer.parseInt(bundle.getString("MAX_CONNECTIONS"));
    private static Integer connections = 0;
    private static final Logger LOGGER = Logger.getLogger("Application");

    public Application() {
        try {
            scktServer = new ServerSocket(Integer.parseInt(bundle.getString("PORT")));
            LOGGER.log(Level.INFO, "Waiting for connection...\nPORT: {0}", scktServer.getLocalPort());
            while (true) {
                if (connections <= MAX_CONNECTIONS) {
                    scktClient = scktServer.accept();
                    Worker worker = new Worker(scktClient);
                    worker.start();
                    connections++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public static synchronized void removeConnection() {
        connections--;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Application();
    }

}
