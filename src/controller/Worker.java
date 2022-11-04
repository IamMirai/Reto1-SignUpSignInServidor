/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import datatransferobject.MessageEnum;
import datatransferobject.Model;
import datatransferobject.Package;
import datatransferobject.User;
import exceptions.ConnectionErrorException;
import exceptions.InvalidUserException;
import exceptions.MaxConnectionExceededException;
import exceptions.TimeOutException;
import exceptions.UserExistException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Application;
import model.DAOFactory;


/**
 *
 * @author haize
 */
public class Worker extends Thread {
    private Package pack;
    private ObjectOutputStream dos;
    private ObjectInputStream dis;
    private Socket sckt;
    private User user = null;
    
    public Worker(Socket sckt) {
        this.sckt = sckt;
    }
    
    public void run() {
        try {
            dos = new ObjectOutputStream(sckt.getOutputStream());
            dis = new ObjectInputStream(sckt.getInputStream());
            Model model = DAOFactory.getModel();
            
            pack = (Package) dis.readObject();
            if (pack.getMessage().equals(MessageEnum.RE_SIGNIN)) {
                user = model.doSignIn(pack.getUser());
            } else if (pack.getMessage().equals(MessageEnum.RE_SIGNUP)) {
                user = model.doSignUp(pack.getUser());
            }
            pack.setMessage(MessageEnum.AN_OK);
        } catch (IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidUserException ex) {
            pack.setMessage(MessageEnum.AN_INVALIDUSER);
        } catch (ConnectionErrorException ex) {
            pack.setMessage(MessageEnum.AN_CONNECTIONERROR);
        } catch (TimeOutException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MaxConnectionExceededException ex) {
            pack.setMessage(MessageEnum.AN_MAXCONNECTION);
        } catch (UserExistException ex) {
            pack.setMessage(MessageEnum.AN_USEREXIST);
        } finally {
            try {
                pack.setUser(user);
                dos.writeObject(pack);
                Application.removeConnection();
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}