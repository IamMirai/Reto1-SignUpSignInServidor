/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import datatransferobject.MessageEnum;
import datatransferobject.Model;
import datatransferobject.Package;
import main.Application;
import model.DAOFactory;


/**
 *
 * @author haize
 */
public class Worker extends Thread {
    Package pack;

    public Worker(Package pack) {
        this.pack = pack;
    }
    
    public void run() {
        Model model = DAOFactory.getModel();
        if (pack.getMessage().equals(MessageEnum.RE_SIGNIN)) {
            pack = model.doSignIn(pack.getUser());
        } else if (pack.getMessage().equals(MessageEnum.RE_SIGNUP)) {
            pack = model.doSignUp(pack.getUser());
        }
        Application.closeWorker(pack);
    }
    
}
