package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pool.Pool;

/**
 * @author Mikel and Sendoa
 * Thread to close the server from the console.
 */
public class ServerCloseThread extends Thread{
    private final ServerSocket sSkt;
    private static final Logger LOGGER = Logger.getLogger("ServerCloseThread.class");
    
    /**
     * @param sSkt Server socket that has to be closed.
     */
    public ServerCloseThread(ServerSocket sSkt) {
        this.sSkt = sSkt;
    }
    
    /**
     * This method keeps in a loop waiting for "kill" to be entered to shut down te server.
     */
    @Override
    public void run() {
        try {
            while(true){
                LOGGER.info("Write 'kill' to close the server.");
                Scanner sc= new Scanner(System.in);
                String s = sc.next();
                if(s.equalsIgnoreCase("kill")){
                    break;
                }
            }
            Pool.closeAllConnections();
            sSkt.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerCloseThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
