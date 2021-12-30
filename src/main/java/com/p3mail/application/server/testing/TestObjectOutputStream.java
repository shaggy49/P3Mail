package com.p3mail.application.server.testing;

import com.p3mail.application.client.model.Email;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TestObjectOutputStream {
    public static void main(String[] args) {
        try {
            String path = "." + File.separator + "server" + File.separator + "emailProva.dat";
            File file = new File(path);
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream outputStream = new ObjectOutputStream(fos);

            List<String> receivers = new ArrayList<>(){{
                add("mf@unito.it");
            }};

            Email email = new Email("af@unito.it", receivers, "prova", "Questa è una mail di prova");

            outputStream.writeObject(email);

            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
