package server;

/**
 * Méthode qui sera appeler pour générer un évènement qui possède un nom de commande et un argument
 */
@FunctionalInterface

public interface EventHandler {
    /**
     * Traitement d'un évènement avec la commande et l'argument
     * @param cmd commande recue
     * @param arg arguement de la commande
     */
    void handle(String cmd, String arg);
}
