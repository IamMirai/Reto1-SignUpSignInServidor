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

/**
 *
 * @author Mikel
 */
public class ServerCloseThread extends Thread{
    private final ServerSocket sSkt;
    private final Socket skt;

    public ServerCloseThread(ServerSocket sSkt, Socket skt) {
        this.sSkt = sSkt;
        this.skt = skt;
    }

    @Override
    public void run() {
        try {
            while(true){
                Scanner sc= new Scanner(System.in);
                String s = sc.nextLine();
                if(s.equalsIgnoreCase("kill")){
                    break;
                }
            }
            skt.close();
            sSkt.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerCloseThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
