package com.p3mail.application.server.util;

import com.p3mail.application.connection.model.Email;

import java.io.*;
import java.util.Scanner;

/*
* classe che permette di leggere il contenuto dei file email nella cartella server
* */
public class ReadMailAccountMailFiles {
    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println("usage: type \"af\", \"ff\" or \"mc\", different to exit");
                System.out.print("Account: ");
                Scanner keyboard = new Scanner(System.in);
                String account = keyboard.nextLine();
                if(account.equalsIgnoreCase("af"))
                    account = "af@unito.it";
                else if(account.equalsIgnoreCase("ff"))
                    account = "ff@unito.it";
                else if(account.equalsIgnoreCase("mc"))
                    account = "mc@unito.it";
                else {
                    System.exit(1);
                }
                for (int i = 0; i < 10; i++) {
                    String path = String.format("." + File.separator + "server" + File.separator + account +  File.separator + "email_%d.dat", i);
                    File file = new File(path);
                    FileInputStream fos = new FileInputStream(file);
                    ObjectInputStream inputStream = new ObjectInputStream(fos);

                    Email email = (Email) inputStream.readObject();

                    System.out.println(email.classictoString());

                }
                System.out.println();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
