package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * La classe serveur implémente le serveur qui écoute les commandes du client
 * grace au sockets. Ce code permet de charger les cours d'une session spécifique
 * ainsi que de s'y inscrire
 */
public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;


    /**
     * Constructeur de la classe Server qui insialise le socket serveur
     * sur le port entré en argument
     *
     * @param port Le port sur lequel le serveur écoute
     * @throws IOException s'il y a une erreur lors de la création du socket
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Ajout de l'objet "EventHandler" au gestionnaire d'événements
     *
     * @param h l'objet Eventhandler a ajouté à la liste
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Appel la méthode "handle" des gestionnaires d'évènement avec les
     * arguments (arg)
     *
     * @param cmd commande recu
     * @param arg l'arguent de la commande
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Boucle infinie qui permet d'attendre les connexions entrantes et traiter les commandes recues
     * Ouverture des flux d'entrée et de sorties du client pour l'envoie et la reseption des données
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Lis la commande envoyée par le client a travers le flux d'entrée  et la traite
     *
     * @throws IOException            s'il y a une erreur lors de la lecture ou l'écreture du flux d'entrée
     * @throws ClassNotFoundException s'il y a une erreur lors de la convertion en chaine
     *                                (string) de caractère de l'objet
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Divise la ligne de commande en deux parties: la commande et les arguments
     *
     * @param line la ligne de commande
     * @return objet PAir qui contient la commande et les arguments
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * ferme les flux et déconnection avec le client
     *
     * @throws IOException s'il y a une erreur lors de la fermeture des flux
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Traite les évènements recus du client
     *
     * @param cmd commande recue du client
     * @param arg l'argument spécifier
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     * Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     * La méthode filtre les cours par la session spécifiée en argument.
     * Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet
     * dans le flux.
     *
     * @param arg la session pour laquelle on veut récupérer la liste des cours
     * @return
     */
    public ArrayList<Course> handleLoadCourses(String arg) {
        String session = arg;
        ArrayList<Course> courses = new ArrayList<Course>();
        File coursesFile = new File("src/main/java/server/data/cours.txt");
        try (Scanner scanner = new Scanner(coursesFile)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] feature = line.split("\t");
                Course course = new Course(feature[1], feature[0], feature[2]);
                if (feature[2].equalsIgnoreCase(arg)) {
                    courses.add(course);

                }
                System.out.println(courses);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            objectOutputStream.writeObject(courses);
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            objectOutputStream.close();
            objectInputStream.close();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return courses;
    }

    /**
     * Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un
     * fichier texte et renvoyer un message de confirmation au client.
     * La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier
     * ou dans le flux de sortie.
     */
    public void handleRegistration() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());

            RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();

            FileWriter file = new FileWriter("src/main/java/server/data/inscription.txt", true);

            file.write(String.valueOf(registrationForm.getCourse().getSession()) + "\t"
                    + registrationForm.getCourse().getCode() + "\t"
                    + registrationForm.getMatricule() + "\t"
                    + registrationForm.getPrenom() + "\t"
                    + registrationForm.getNom() + "\t"
                    + registrationForm.getEmail());
            file.write(System.lineSeparator());

            objectOutputStream.writeObject("Félicitations! Inscription réussie de" + registrationForm.getPrenom()
                    + "au cours " + registrationForm.getCourse());
            objectOutputStream.flush();
            file.close();
        } catch (IOException e) {
            System.out.println("Une erreur s'est produite.");
        } catch (ClassNotFoundException e) {
            System.out.println("Il n'y a pas de classe a ce nom");
        } finally {
            try {
                objectInputStream.close();
                objectOutputStream.close();
                client.close();

            } catch (IOException e) {
                System.out.println("Incapable de fermer le flux.");
            }

        }
    }
}


