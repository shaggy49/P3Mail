package com.p3mail.application.server;

import java.util.Scanner;

public class MailServer {
    public static void main(String[] args) {
        System.out.println("Finestra del server: ");
        HandleMailConnection handleSocketsConnections = new HandleMailConnection("handleConnections");
        handleSocketsConnections.start();
        Scanner keyboard = new Scanner(System.in);
        while (!keyboard.nextLine().trim().equalsIgnoreCase("quit")) ;
        System.exit(0);
    }

}


