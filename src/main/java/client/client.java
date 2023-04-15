package client;

import javafx.util.Pair;
import server.Server;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class client {
    private final String host;
    private final int port;
    private Socket socket ;
    private static ObjectOutputStream outputStream;
    private static ObjectInputStream inputStream;
    public client(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void connect() throws IOException {
        this.socket = new Socket(host, port);
        this.outputStream= new ObjectOutputStream(socket.getOutputStream());
        this.inputStream =  new ObjectInputStream(socket.getInputStream());
    }
    public void disconnect() throws IOException {
        this.inputStream.close();
        this.outputStream.close();
        this.socket.close();
    }
    public static void sendCommand(String command, String arg) throws IOException {
        String fullCommand = command + " " + arg;
        outputStream.writeObject(fullCommand);
        outputStream.flush();
    }
    public static Pair<String,String> receiveResponse() throws IOException, ClassNotFoundException {
        String line;
        if ((line = inputStream.readObject().toString()) != null) {
            return processCommandLine(line);
        }

        return null;
    }
    private static Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" , ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    public static ArrayList<Course> loadClass(String arg) throws IOException, ClassNotFoundException {
        sendCommand(Server.LOAD_COMMAND, arg);
        ArrayList<Course> courseArrayList = new ArrayList<Course>();
        Object response = inputStream.readObject();
        if (response instanceof ArrayList){
            ArrayList<Course> courses = (ArrayList<Course>) response;
            for (Course course : courses){
                String name = course.getName();
                String code = course.getCode();
                String session = course.getSession();
                Course courseFinal= new Course(name,code,session);
                courseArrayList.add(courseFinal);
            }
        }
        return courseArrayList;
    }

    public static void registration(RegistrationForm registrationForm) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localHost", 1337);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
        outputStream.writeObject(Server.REGISTER_COMMAND);
        outputStream.flush();
        outputStream.writeObject(registrationForm);
        outputStream.flush();

        BufferedReader bf = new BufferedReader(inputStream);
        String line;
        String course = String.valueOf(registrationForm.getCourse().getCode());
        String result = course.substring(0,3).toUpperCase()+ "-" + course.substring(3);
        if ((line= bf.toString()) != null){
           System.out.println("Félicitations! Inscription réussie de" + registrationForm.getPrenom() + "au cours "
                   + result);
       }

        BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/server/data/inscription.txt", true));
        writer.write(String.valueOf(registrationForm.getCourse().getSession()) + "\t"
                + registrationForm.getCourse().getCode() + "\t"
                + registrationForm.getMatricule()+  "\t"
                + registrationForm.getPrenom() + "\t"
                +registrationForm.getNom()+ "\t"
                +registrationForm.getEmail());
        writer.newLine();
        writer.close();

        outputStream.close();
        inputStream.close();
        socket.close();

            }

    public static void main(String[] arg) throws IOException, ClassNotFoundException {
        String firstName = "";
        String lastName = "";
        String email = "";
        String matricule = "";
        String code = " ";
        RegistrationForm registrationForm = new RegistrationForm(firstName, lastName, email, matricule, null);

        try {
            client client = new client("localhost", 1337);
            client.connect();
            String session = "";
            ArrayList<Course> courseList = new ArrayList<>();
            System.out.println("***Bienvenue au portail d'inscription de cours de l'UDEM***");
            System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste de cours:");
            System.out.println("1. Automne");
            System.out.println("2. Hiver");
            System.out.println("3. Ete");
            Scanner scanner = new Scanner(System.in);
            String s = scanner.nextLine();
            while (true) {
                if (s.equals("1")) {
                    session = "automne";
                    break;
                } else if (s.equals("2")) {
                    session = "hiver";
                    break;
                } else if (s.equals("3")) {
                    session = "ete";
                    break;
                } else {
                    System.out.println("Veuillez entrer un chiffre entre 1 et 3. Merci");
                    s = scanner.nextLine();
                    break;
                }

            }
            courseList = loadClass(session);
            System.out.println("Les cours offerts pendant la session " + session + " sont :");
            for (Course course : courseList) {
                System.out.println(course.getCode() + "\t"+ course.getName()+"\t"+  course.getSession());

            }
            System.out.println("> Choix : " +s);
            System.out.println("1. Consulter les cours offert pour une autre session");
            System.out.println("2. Inscription à un cours");
            Scanner choice = new Scanner(System.in);
            String number = choice.nextLine();
            while (true) {
                if (number.equals("1")) {
                    break;
                } else if (number.equals("2")) {

                   Scanner information = new Scanner(System.in);
                    System.out.println("Veuillez saisir votre prénom: ");
                    firstName = information.nextLine();
                    if( firstName.matches("[a-zA-z]+")){
                        firstName = firstName;
                    }else {
                        System.out.println("Entrez un nom valide");
                    }
                    System.out.println("Veuillez saisir votre nom: ");
                    lastName = information.nextLine();
                        System.out.println("Veuillez saisir votre email: ");

                        email = information.nextLine();
                        if (!email.matches("\\w+\\.\\w+@umontreal\\.ca")) {
                            System.out.println("Entrez une adresse courriel valide");
                            email= information.nextLine();
                        }
                        System.out.println("Veuillez saisir votre matricule: ");
                        matricule = information.nextLine();
                        if (matricule.matches("[0-9]+") && matricule.length() ==8) {
                            matricule = matricule;
                        }else {
                            System.out.println("Veuillez entrez un numéro d'étudiant valide");
                            matricule = information.nextLine();
                        }
                        System.out.println("Veuillez saisir le code du cours :  ");
                        code = information.nextLine();

                        registrationForm.setPrenom(firstName);
                        registrationForm.setNom(lastName);
                        registrationForm.setMatricule(matricule);
                        registrationForm.setEmail(email);
                        boolean courseFound = false;
                        for (Course course : courseList) {
                            if (code.equalsIgnoreCase(course.getCode())) {
                                courseFound = true;
                                registrationForm.setCourse(course);
                                registration(registrationForm);
                            }
                            if (!courseFound){
                                System.out.println("Veuillez choisir un cours valide");
                            }
                        }

                        information.close();
                        System.exit(0);

                }else {
                    System.out.println("Veuillez entrer un chiffre entre 1 et 2. Merci");
                    number = scanner.nextLine();
                    break;
                }
            }
            scanner.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}



