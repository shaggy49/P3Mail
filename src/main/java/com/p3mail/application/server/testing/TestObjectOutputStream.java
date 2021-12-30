package com.p3mail.application.server.testing;

import com.p3mail.application.client.model.Email;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
* classe che si occupa di creare la cartella server e metterci dentro i file con la mail
* degli utenti
* TODO: add specific folder for each registered user
* */
public class TestObjectOutputStream {
    public static void main(String[] args) {
        try {
            String[] people = new String[] {"Paolo", "Alessandro", "Enrico", "Giulia", "Gaia", "Simone"};
            String[] objects = new String[] {
                    "Importante", "A proposito della nostra ultima conversazione", "Tanto va la gatta al lardo",
                    "Non dimenticare...", "Domani scuola" };
            String[] texts = new String[] {
                    "È necessario che ci parliamo di persona, per mail rischiamo sempre fraintendimenti",
                    "Ricordati di comprare il latte tornando a casa",
                    "L'appuntamento è per domani alle 9, ci vediamo al solito posto",
                    "Ho sempre pensato valesse 42, tu sai di cosa parlo"
            };

            for (int i = 0; i < 10; i++) {
                Random r = new Random();
                Email email = new Email(
                        people[r.nextInt(people.length)],
                        List.of(people[r.nextInt(people.length)]),
                        objects[r.nextInt(objects.length)],
                        texts[r.nextInt(texts.length)]);

                String path = String.format("." + File.separator + "server" + File.separator + "email_%d.dat", email.getId());
                File file = new File(path);
                file.getParentFile().mkdirs();
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream outputStream = new ObjectOutputStream(fos);

                outputStream.writeObject(email);

                outputStream.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
