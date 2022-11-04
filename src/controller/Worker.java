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
    private final Socket skt;
    private User user;
    
    public Worker(Socket skt) {
        this.skt = skt;
    }
    
    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(skt.getInputStream());
            Model model = DAOFactory.getModel();
            
            pack = (Package) ois.readObject();
            if (pack.getMessage().equals(MessageEnum.RE_SIGNIN)) {
                user = model.doSignIn(pack.getUser());
                pack.setUser(user);
            } else if (pack.getMessage().equals(MessageEnum.RE_SIGNUP)) {
                model.doSignUp(pack.getUser());
            }
            pack.setMessage(MessageEnum.AN_OK);
        } catch (IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidUserException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            pack.setMessage(MessageEnum.AN_INVALIDUSER);
        } catch (TimeOutException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MaxConnectionExceededException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            pack.setMessage(MessageEnum.AN_MAXCONNECTION);
        } catch (UserExistException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            pack.setMessage(MessageEnum.AN_USEREXIST);
        } catch (ConnectionErrorException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(skt.getOutputStream()); 
                oos.writeObject(pack);
                oos.close();
                Application.removeConnection();
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
