package server;

/**
 *Permet de lancer le serveur
 */
public class ServerLauncher {
    /**
     * Le numéro de port utilisé par le serveur
     */
    public final static int PORT = 1337;

    /**
     *Creation d'instance de la classe Serveur avec le port définie plus
     * tot et lance le serveur
     * @param args la ligne de commande
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// TODO: CREATE A JAR FILE