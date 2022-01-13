package com.p3mail.application.server.util;

import com.p3mail.application.connection.model.Email;
import com.p3mail.application.server.model.RegisteredClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.FileUtils;

/*
* classe che si occupa di creare la cartella server e metterci dentro i file con la mail
* degli utenti
* */
public class MailAccountInitializer {

    public static final int INITIAL_MAIL_NUMBER = 10;

    public static void main(String[] args) {

        RegisteredClient registeredClient = new RegisteredClient();
        List<String> registeredMailAccounts = registeredClient.getRegisteredMails();
        String[] objects = new String[] {
                "Importante", "Mi sono dimenticato", "Allegato busta paga",
                "Urgente!", "Domani scuola", "Lista spesa", "Resoconto scolastico", "Pagella secondo anno", "Votazione esame programmazione III" };
        String[] texts = new String[] {
                "È necessario che ci parliamo di persona, per mail rischiamo sempre fraintendimenti",
                "Ricordati di comprare il latte tornando a casa",
                "L'appuntamento è per domani alle 9, ci vediamo al solito posto",
                "Ho sempre pensato valesse 42, tu sai di cosa parlo",
                "Alla fine all'esame di programmazione III ho preso 30",
                "Ma sei riuscito a passare l'esame di guida?",
                "L'ultima volta che ci siamo visti mi sa che ho dimenticato le chiavi da te",
                "Ci siete domani su discord?",
                "Va bene, fammi sapere",
                "Ce l'abbiamo fatta!!"
        };

        try {
            FileUtils.deleteDirectory(new File("." + File.separator + "server"));

            for (String account : registeredMailAccounts) {
                ObjectOutputStream outputStream = null;
                int i;
                for (i = 0; i < INITIAL_MAIL_NUMBER; i++) {
                    Random r = new Random();
                    Email email = new Email(
                            i,
                            registeredMailAccounts.get(r.nextInt(registeredMailAccounts.size())),
                            List.of(account),
                            objects[r.nextInt(objects.length)],
                            texts[r.nextInt(texts.length)]);

                    String path = String.format("." + File.separator + "server" + File.separator + account + File.separator + "email_%d.dat", i);
                    File file = new File(path);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    outputStream = new ObjectOutputStream(fos);

                    outputStream.writeObject(email);

                }

                String path = String.format("." + File.separator + "server" + File.separator + account + File.separator + "info.dat");

                File file = new File(path);

                FileOutputStream fos = new FileOutputStream(file);
                outputStream = new ObjectOutputStream(fos);

                outputStream.writeObject(i);

                if(outputStream != null) {
                    outputStream.close();
                }
            }


            System.out.println("Generated random email and info files, check server folder");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
