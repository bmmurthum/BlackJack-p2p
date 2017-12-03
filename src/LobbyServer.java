    import java.net.*;
import java.io.*;

/**
 * This is the "Central Server" of this system. It keeps track of things.
 *
 * @author Brendon Murthum Javier Ramirez
 *
 */
public class LobbyServer {

    /** This object holds all of the current player data. */
    private static LobbyOnlinePlayers playerList = new LobbyOnlinePlayers();

    /** The port that the users connect to. This is hard-coded into the GUI. */
    private static final int WELCOMEPORT = 1234;
    /** The socket that the server listens for new users. */
    private static ServerSocket welcomeSocket;

    /**
     * Runs the "Lobby Server," the main server that helps guide users to one
     * another. This is necessary for the game to work properly.
     * @param args - Unused.
     */
    public static void main(final String[] args) {

        /* Show server starting status */
        System.out.println(" ");
        System.out.println("Server initialized. Waiting for " + "connections.");

        /* Initialize the welcome socket */
        try {
            welcomeSocket = new ServerSocket(WELCOMEPORT);

        } catch (IOException ioEx) {
            System.out.println(" ");
            System.out.println("  ERROR: Unable to set up port!");
            System.exit(1);
        }

        /* Perform a loop to wait for new connections */
        do {

            try {
                /* Wait for client... */
                Socket connectionSocket = welcomeSocket.accept();

                System.out.println("\nNew Client Connected to Server!");

                /* Shows new users. */
                System.out.println(" New Connection's IP: "
                                + connectionSocket.getInetAddress());

                /*
                 * Create a thread to handle communication with this client and
                 * pass the constructor for this thread a reference to the
                 * relevant socket and user IP.
                 */
                LobbyHandler handler = new LobbyHandler(connectionSocket,
                                playerList);

                /* Start a new thread for this client */
                handler.start();

            } catch (Exception e) {
                /* Error in accepting new connection through welcomeSocket. */
                System.out.println(
                                "  ERROR: Error in accepting new connection.");
            }

        } while (true);

        /* End of main() */
    }

}
