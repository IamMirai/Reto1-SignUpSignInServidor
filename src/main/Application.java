/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import controller.Worker;
import exceptions.MaxConnectionExceededException;
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
    Worker worker;
    private final ResourceBundle bundle = ResourceBundle.getBundle("pool.config");
    private final Integer MAX_CONNECTIONS = Integer.parseInt(bundle.getString("MAX_CONNECTIONS"));
    private static Integer connections = 0;
    private static final Logger LOGGER = Logger.getLogger("Application");

    public Application() {
        try {
            scktServer = new ServerSocket(Integer.parseInt(bundle.getString("PORT")));
            while (true) {
                scktClient = scktServer.accept();
                    if (connections < MAX_CONNECTIONS) {
                        worker = new Worker(scktClient,false);
                    } else {
                        worker = new Worker(scktClient,true);
                    }
                worker.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public static synchronized void removeConnection() {
        connections--;
    }
    
    public static synchronized void addConnection() {
        connections++;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Application();
    }

}
