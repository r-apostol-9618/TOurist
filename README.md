Progetto TOurist

Questo spazio github è dedicato al progetto TOurist.
L'Idea
TOurist è un progetto nato come esame di Android, l'idea è quella di dare una mano ai turisti che vengono a visitare la città di Torino.
Volevamo dare ai nostri utenti un resoconto veloce dei posti di loro interesse, in pochi passi, rapido, senza registrazioni o pagamenti in app. Ogni volta che si apre l'app vengono chieste 3 informazioni all'utente
• Selezionare il numero di persone
• Selezionare il budget a disposizione
• Selezionare il tempo a disposizione
Dopo che vengono inseriti i dati viene subito mostrata una mappa con i punti di interesse relativi e in alto come toolbar la temperatura in quel preciso istante, ogni scelta dell'utente nelle prime 3 schermate modificheranno il risultato finale.

La prima schermata quella relativa al numero di persone l'utente può selezionare tra 3 scelte: "Singolo", "In coppia", "In gruppo".
La seconda schermata quella relativa al budget a disposizione l'utente può selezionare tra 2 scelte "Gratuito" o può inserire quanti soldi vuole spendere tramite un "Range Picker"

Le prime 2 schermate sono strettamente collegate tra loro:
Ogni scelta condizionerà la schermata successiva. Per esempio se si seleziona "Singolo" allora la gestione della seconda schermata quella relativa al budget verrà trattata in modo diverso, mettere 100 euro come budget a disposizione per un "Singolo" darà un risultato diverso rispetto a selezionare 100 euro per un "Gruppo".

La terza schermata quella relativa al tempo a disposizione l'utente può selezionare il giorno tramite un calendario, se ha tutto il giorno a disposizione o solo un range di ore.

Parte la quarta e ultima schermata con la toolbar, la mappa e 3 pulsanti nella parte in basso, qua viene chiesto all'utente di attivare il gps, per la geolocalizzazione, se l'utente si troverà al di fuori di Torino verrà mostrato un pop up ma si potrà comunque usare l'app anche se non alle sue massime prestazioni. 
Nella toolbar viene visualizzato a sinitra il nome dell'app ed a destra i gradi in quel preciso momento.
Sulla mappa vengono mostrati i punti di interesse tramite dei marker in base alle scelte fatte fino ad ora, al click sull'marker che ci interessa vengono fuori le informazioni principali del posto selezionato tramite un fragment: titolo, via, rating, foto, se il rating o la foto non sono note verranno dei messaggi di attenzione all'interno di questo nuovo fragment.
La mappa come anche la toolbar cambieranno in base all'ora, se l'app viene aperta di notte lo stile della mappa e della toolbar cambieranno, abbiamo utilizzato 2 stili diversi per il giorno e la notte.
Nella parte inferiori vengono visualizzzti 3 pulsanti: musei, cinema, ristoranti, sempre tenendo conto delle informazioni inserite nelle prime 3 schermate vengono visualizzate queste 3 tipologie di posti.

Tecnologie principali

Maps SDK - per la gestione della mappa
Places SDK - per accedere al database di Google di informazioni sui posti attorno all'utente
OpenWeather - per la gestione dei dati meteorologici

