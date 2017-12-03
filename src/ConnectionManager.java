import java.net.*;
import java.util.*;
import java.io.*;
import java.awt.*;

/**
 * @author Brendon Murthum; Javier Ramirez
 *
 */
public class ConnectionManager {

    /** To handle connecting to Central-Server. */
    private static Socket server;

    /** User's dealer port for others to connect to. */
    private String dealerWelcomePort;

    /** Central-Server's IP. Hard-coded. */
    private String lobbyServerIP = "127.0.0.1";
    /** Central-Server's Port. Hard-coded. */
    private String lobbyServerPort = "1234";

    /** User's username. */
    private String userName;

    /** Socket to communicate to the main lobby-server. */
    private Socket controlSocket = null;
    /** This handles the control-line out stream. */
    private PrintWriter outToServerControl = null;
    /** This handles the control-line input stream. */
    private Scanner inFromServerControl = null;

    /** Checks for the user being already connected. */
    private boolean isConnected = false;
    /** If in communication with server. BEFORE sign-in. */
    private boolean hasCommunicationWithServer = false;

    /** Tracks if user is hosting currently. May be used for GAMEGUI. */
    private boolean isCurrentlyHosting = false;

    /**
     * Tells the client-GUI if the lobby-server is running.
     * @return TRUE - if server is running
     *         FALSE - if no communication with server
     */
    public boolean hasConnectionToServer() {
        return hasCommunicationWithServer;
    }

    /**
     * Establishes socket and stream for communication with server.
     * This can be called again and again by GUI until the server is running.
     * Good for re-establishing connection after server-disconnect.
     * @return TRUE - if successfully established connection.
     *         FALSE - if failure to establish connection.
     */
    public boolean establishConnectionToServer() {

        /* Establish a TCP connection with the server */
        try {
            controlSocket = new Socket(lobbyServerIP,
                            Integer.parseInt(lobbyServerPort));

        } catch (Exception p) {
            System.out.println("  ERROR: Did not find server's socket!");
            hasCommunicationWithServer = false;
            isConnected = false;
            return (false);
        }

        /* Set-up the control-stream. Handle errors. */
        try {
            inFromServerControl
                = new Scanner(controlSocket.getInputStream());
            outToServerControl
                = new PrintWriter(controlSocket.getOutputStream());

        } catch (Exception e) {
            System.out.println("  ERROR: Did not connect to server!");
            hasCommunicationWithServer = false;
            isConnected = false;
            return (false);
        }

        hasCommunicationWithServer = true;

        /* The client now has established TCP connection with server */
        return (true);
    }

    /**
     * This method, initiated by the GUI, handles connecting to the lobbyServer.
     *
     * @param typedUserName
     *            - The user's typed username.
     * @return - Returns 1 on success. Returns a variety of negative values on
     *         errors.
     */
    public int connectToServer(final String typedUserName) {

        /* Taking the parameters from the GUI */
        this.userName = typedUserName;

        /* Only DO connect if NOT already connected */
        if (!isConnected && hasCommunicationWithServer) {

            /** Information to send to the Lobby-Server. */
            String newUserInformation = userName;

            /* For debugging. View the sent filenames and keys. */
            // System.out.print(" DEBUG: Sent String To Connect: "
            // + newUserInformation);

            outToServerControl.println(newUserInformation);
            outToServerControl.flush();

            String recvMsg;
            try {
                /* Wait for response from server */
                recvMsg = inFromServerControl.nextLine();

                if (recvMsg.equals("GOOD-USERNAME")) {

                    /* For debugging */
                    // System.out.println(" DEBUG: Good username!");

                    isConnected = true;
                    return (1);

                } else if (recvMsg.equals("BAD-USERNAME")) {

                    /* For debugging */
                    // System.out.println(" DEBUG: Bad username!");

                    isConnected = false;
                    return (-1);

                } else {

                    /* For debugging */
                    // System.out.println(" DEBUG: Corrupted response "
                    //                      + "from server!");
                    isConnected = false;
                    return (-3);
                }

            } catch (Exception e) {

                /* For debugging */
                // System.out.println(" DEBUG: Failed "
                //                    + "response from server!");

                hasCommunicationWithServer = false;
                isConnected = false;
                return (-4);
            }

            /* Ends of if(isConnected == false) */
        } else {
            /* For debugging */
            System.out.println("  DEBUG: Failure to connect to Central"
                            + "-Server!");
            return (-7);
        }

        /* End of connectToServer() */
    }

    /**
     * Disconnects the user from the entire service by sending the Lobby-Server
     * the command "Disconnect".
     */
    public void disconnectFromServer() {

        outToServerControl.println("DISCONNECT");
        outToServerControl.flush();

        try {
            controlSocket.close();
            outToServerControl.close();
            inFromServerControl.close();
        } catch (Exception e) {
            System.out.println("  ERROR: Failure in closing sockets/streams");
        }
        hasCommunicationWithServer = false;
        isConnected = false;
    }

    /**
     * Sets up the client to be logged as hosting a game.
     * @return INT - integer represents confirmation/errors
     */
    public int initiateHostingGame(String portNumber) {

        /* TODO - Open a welcome port for another player */
        /* TODO - Establish a flexible socket-port being opened here. */
    	
        /* TODO - This temporarily holds a sample "dealerWelcomePort" */
        outToServerControl.println("HOSTGAME" + " " + portNumber);
        outToServerControl.flush();

        /* Wait for confirmation. To disallow over 10 connects, or errors. */

        String recvMsg;
        try {
            /* Wait for response from server */
            recvMsg = inFromServerControl.nextLine();

            if (recvMsg.equals("CONFIRMED")) {
                /* TODO - This variable may be used with the new gameGUI */
                isCurrentlyHosting = true;
                return (1);

            } else if (recvMsg.equals("DECLINED")) {
                return (-1);

            } else {
                return (-2);
            }
        } catch (Exception e) {
            /* Connection may have broken */
            hasCommunicationWithServer = false;
            return (-3);
        }

        /* TODO - Change the user's GUI */
    }

    /**
     * Sends a request to the lobbyServer to retrieve updated information.
     * @return String- returns a message of error/confirmation
     */
    public String getUpdate() {
        if (hasCommunicationWithServer) {
            if (isConnected) {
                try {
                    outToServerControl.println("GETUPDATE");
                    outToServerControl.flush();

                    String recvMsg;

                    /* Wait for response from server */
                    recvMsg = inFromServerControl.nextLine();
                    return recvMsg;

                } catch (Exception e) {
                    /* Connection may have broken */
                    isConnected = false;
                    hasCommunicationWithServer = false;
                    try {
                        controlSocket.close();
                        outToServerControl.close();
                        inFromServerControl.close();
                    } catch (Exception f) {
                        System.out.println("  ERROR: Failure in closing "
                                        + "sockets/streams");
                    }
                    return ("LINEERROR");
                }

            }
            return ("WAITING");

        } else {
            /* No communication with server */
            isConnected = false;
            return ("NOCOMM");
        }
    }
}
