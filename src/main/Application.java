/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import controller.Worker;
import datatransferobject.MessageEnum;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import datatransferobject.Package;

/**
 *
 * @author 2dam
 */
public class Application {  
    ServerSocket scktServer;
    static Socket scktClient;
    static ObjectOutputStream oos;
    static ObjectInputStream ois;
    ResourceBundle bundle = ResourceBundle.getBundle("config.properties");
    final Integer PORT = Integer.parseInt(bundle.getString("PORT"));    
    final Integer MAX_CONNECTIONS = Integer.parseInt(bundle.getString("MAX_CONNECTIONS"));
    static Integer connections = 0;
    Package pack;
    
    public Application() {
        try {
            scktServer = new ServerSocket(PORT);
            while (true) {
                scktClient = scktServer.accept();
                connections++;
                ois = new ObjectInputStream(scktClient.getInputStream());
                oos = new ObjectOutputStream(scktClient.getOutputStream());
                if (connections > MAX_CONNECTIONS) {
                    pack.setMessage(MessageEnum.AN_MAXCONNECTION);
                    oos.writeObject(pack);
                    oos.close();
                    ois.close();
                    scktClient.close();
                    break;
                }
                pack = (Package) ois.readObject();
                Worker worker = new Worker(pack);
                worker.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static synchronized void closeWorker(Package pack) {
        try {
            oos.writeObject(pack);
            oos.close();
            ois.close();
            scktClient.close();
            
            connections--;
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Application();
    }
    
}
