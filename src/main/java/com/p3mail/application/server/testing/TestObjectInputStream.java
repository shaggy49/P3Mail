package com.p3mail.application.server.testing;

import com.p3mail.application.client.model.Email;

import java.io.*;

/*
* classe che permette di leggere il contenuto dei file email nella cartella server
* */
public class TestObjectInputStream {
    public static void main(String[] args) {
        try {
            for (int i = 1; i <= 10; i++) {
                String path = String.format("." + File.separator + "server" + File.separator + "email_%d.dat", i);
                File file = new File(path);
                FileInputStream fos = new FileInputStream(file);
                ObjectInputStream inputStream = new ObjectInputStream(fos);

                Email email = (Email) inputStream.readObject();

                System.out.println(email.classictoString());

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
