#Consegna bitches ehehehe

Si sviluppi un’applicazione java con GUI implementata usando JavaFXML, e basata sui pattern MVC+Observer Observable 
(con o senza binding di properties), che simuli alcune funzionalità di un client di posta elettronica 
(non si sviluppi la parte server che gestisce le caselle di posta elettronica degli utenti).

La casella di posta elettronica contiene una lista (ObservableList) eventualmente vuota di messaggi e rappresenta il model dell'applicazione. 
I messaggi di posta elettronica sono istanze di una classe Email.
La classe Email specifica ID, mittente,  destinatario, argomento, testo etc..
La vista sia una tipica finestra di client di mail (es. Thunderbird), implementata con JavaFXML, e offre le seguenti funzionalità:
    - vedere il nome dell'account di posta elettronica - per ora assumiamo che l'account sia una costante nell'applicazione (non sviluppiamo l'autenticazione utente);
    - visualizzare la lista dei messaggi memorizzati nella casella di posta, utilizzando una ObservableList o classi simili - la lista sia ordinata per data dai messaggi più recenti ai meno recenti; per la visualizzazione usare ListView (o TableView a seconda del tipo di layout che volete dare);
    - visualizzare un messaggio della casella di posta selezionandolo dalla lista dei messaggi;
    - rimuovere un messaggio dalla casella di posta elettronica e vedere la lista dei messaggi aggiornata.

Si inizializzi internamente all'applicazione la casella di posta elettronica con una decina di messaggi 
da utilizzare per provare le funzionalità dell'applicativo.

NB: La casella non potrà ricevere messaggi in quanto non è connessa ad alcun sistema di gestione della posta elettronica. 
Inoltre, per ora non si implementi l'invio di messaggi.