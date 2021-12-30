package com.p3mail.application.server.testing;

import com.p3mail.application.client.model.Email;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestObjectInputStream {
    public static void main(String[] args) {
        try {
            String path = "." + File.separator + "server" + File.separator + "emailProva.dat";
            File file = new File(path);
            FileInputStream fos = new FileInputStream(file);
            ObjectInputStream inputStream = new ObjectInputStream(fos);

            Email email = (Email) inputStream.readObject();

            System.out.println(email.classictoString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
