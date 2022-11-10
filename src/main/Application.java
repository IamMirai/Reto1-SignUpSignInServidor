package main;

import controller.ServerCloseThread;
import controller.Worker;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Sendoa, Mikel, Julen y Haizea
 * The main class of the application that manages the connection with the clients.
 */
public class Application {

    private ServerSocket scktServer;
    private Socket scktClient;
    Worker worker;
    ServerCloseThread serverClose;
    private final ResourceBundle bundle = ResourceBundle.getBundle("pool.config");
    private final Integer MAX_CONNECTIONS = Integer.parseInt(bundle.getString("MAX_CONNECTIONS"));
    private static Integer connections = 0;
    private static final Logger LOGGER = Logger.getLogger("Application");
    
    
    public Application() {
        try {
            scktServer = new ServerSocket(Integer.parseInt(bundle.getString("PORT")));
            serverClose = new ServerCloseThread(scktServer);
            serverClose.start();
            while (true) {
                scktClient = scktServer.accept();
                if (connections < MAX_CONNECTIONS) {
                    worker = new Worker(scktClient, false);
                } else {
                    worker = new Worker(scktClient, true);
                }
                worker.start();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
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
