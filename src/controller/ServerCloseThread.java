/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pool.Pool;

/**
 *
 * @author Mikel
 */
public class ServerCloseThread extends Thread{
    private final ServerSocket sSkt;

    public ServerCloseThread(ServerSocket sSkt) {
        this.sSkt = sSkt;
    }

    @Override
    public void run() {
        try {
            while(true){
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
