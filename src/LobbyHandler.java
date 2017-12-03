import java.net.*;
import java.util.*;
import java.io.*;

/**
 * This class, running on the main-server, handles an individual players
 * communications with the main set of data. This class adds-to and deletes-from
 * the object "LobbyOnlinePlayers" which holds all of the main data that each
 * thread can pull from.
 *
 * @author Brendon Murthum, Javier Ramirez
 *
 */

public class LobbyHandler extends Thread {


    /** This handles the stream from the command-line of client. */
    private Scanner inFromClient;
    /** This handles the output stream by command-line to client. */
    private PrintWriter outToClient;

    /** This is used to grab bytes over the data-line. */
    private String recvMsg;

    /** This takes the user information from the host. */
    private String userName;
    /** The user's IP address. Grabbed from socket connection. */
    private String userIP;
    /** The user's port for another user to connect to. */
    private String userPort;

    /** This thread keeps its own client informed using this. */
    private boolean haveWeSentTheUpdate;

    /** This is for thread control. */
    private boolean endThread = false;

    /** This will point to the object that is the main list of user details. */
    private LobbyOnlinePlayers ptrOnlinePlayersList;

    /**
     * Beginning of thread. This constructor marks the beginning of a thread on
     * the server. Things here happen once, exclusively with THIS connected
     * client.
     * @param controlListen - The given socket for this thread to use.
     * @param onlinePlayersList - Passing the global arraylists by object.
     */
    public LobbyHandler(final Socket controlListen,
                    final LobbyOnlinePlayers onlinePlayersList) {

        /* Make this all... point to the total... */
        ptrOnlinePlayersList = onlinePlayersList;

        try {
            /* Setting up a threaded input control-stream */
            inFromClient = new Scanner(controlListen.getInputStream());
            /* Setting up a threaded output control-stream */
            outToClient = new PrintWriter(controlListen.getOutputStream());

            /* Get IP from the control socket for future connections */
            userIP = controlListen.getInetAddress().getHostAddress();

            /* For debugging */
            // System.out.println(" DEBUG: A new thread was successfully
            // setup.");
            // System.out.println("");

        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            System.out.println("  ERROR: Could not set up a "
                            + "threaded client input and output stream.");
        }
    }

    /******************************************************************
     *
     * Beginning of main thread code. This method marks the threaded area that
     * this client receives commands and handles. When this receives "QUIT" from
     * the client, the thread closes.
     *
     ******************************************************************/
    public void run() {

        /** Keeps tracks of when to close the thread */
        boolean stayAlive = true;
        /** Gets user information from the host */
        StringTokenizer userTokens;
        /** Username read from the client's data sent over the line. */
        String tmpUserName = "";

        try {
            /* This grabs the string of data from the client */
            // Ex: "Alice"
            String userInformation = inFromClient.nextLine();

            /* For Debugging. This shows what is read from control-line. */
            // System.out.println(" DEBUG: Read-In: " + userInformation);

            /* Make the string parseable by tokens */
            userTokens = new StringTokenizer(userInformation);

            /* Initialize and display the new user's shown username */
            tmpUserName = userTokens.nextToken();

            /*
             * If username taken, throw error, return a message, and stop
             * thread.
             */
            if (ptrOnlinePlayersList.isUsernameTaken(tmpUserName)) {
                System.out.println("  ERROR: \"" + tmpUserName + "\" [" + userIP
                                + "] must"
                                + " pick another username to connect!");

                /* Send notification to client to choose another username */
                outToClient.println("BAD-USERNAME");
                outToClient.flush();

                endThread = true;
                throw new EmptyStackException();

            } else {
                /* Send confirmation of username choice */
                outToClient.println("GOOD-USERNAME");
                outToClient.flush();

                /* Set this thread's userName variable for use */
                userName = tmpUserName;

                /* Add this player to the main list that other threads use */
                ptrOnlinePlayersList.newConnectionValues(tmpUserName, userIP);
            }

            System.out.println(" Username: " + tmpUserName);

        } catch (Exception e) {
            System.out.println("  ERROR: Failure to read initial user info.");
        }

        /* End the thread */
        if (endThread) {
            System.out.println("  ERROR: Ending user's thread");

            /* Show the server data on user-leave */
            System.out.println(" ");
            ptrOnlinePlayersList.showData();
            System.out.println(" ");

            return;
        }

        /* For show. To separate user-logins. */
        System.out.println(" ");

        /* For show. View the server's main data points on each new entrance! */
        ptrOnlinePlayersList.showData();

        /* For show. To separate information. */
        System.out.println(" ");

        /* The controlling loop that keeps the user thread alive */
        while (stayAlive) {

            /* This reads the command from the client */
            try {
                recvMsg = inFromClient.nextLine();

            } catch (Exception e) {
                /* Client left early, or otherwise */
                System.out.println("Client " + userName + " [" + userIP
                                + "] left early!");

                /* Remove all rows with this username */
                ptrOnlinePlayersList.remove(userName);

                /* Show the server data on user-leave */
                System.out.println(" ");
                ptrOnlinePlayersList.showData();
                System.out.println(" ");

                break;
            }

            /* For token handling */
            StringTokenizer tokens = new StringTokenizer(recvMsg);

            /* Client command, "UPDATE" or another. */
            String commandFromClient = tokens.nextToken();

            

            /* For server log printing */
            if (commandFromClient.equals("DISCONNECT")) {
                System.out.println("Client " + userName + " [" + userIP
                                + "] disconnected!");

                /* Remove all rows with this username */
                ptrOnlinePlayersList.remove(userName);
                
                /* Show the new data */
                ptrOnlinePlayersList.showData();

                stayAlive = false;

            } else if (commandFromClient.equals("HOSTGAME")) {
                /* For server log printing */
                System.out.println(
                                "Client " + userName + " [" + userIP + "]: ");
                System.out.println("Ran Command: HOSTGAME");

                /* TODO - Send an acknowledgement? */

                /* Grab the user's sent open port for new players to connect */
                String usersGamePort = tokens.nextToken();

                /* Adds user to hosting-list, to be broadcast out. */
                int result = 
                  ptrOnlinePlayersList.newUserHosting(userName, usersGamePort);
                
                /* Send the response of confirmation/denial to the client */
                String toSendToClient;
                if (result == 1) {
                    toSendToClient = "CONFIRMED";

                } else {
                    toSendToClient = "DECLINED";
                }
                outToClient.println(toSendToClient);
                outToClient.flush();

                /* Show the new data */
                ptrOnlinePlayersList.showData();

            } else if (commandFromClient.equals("NOTHOSTING")) {
                /* For server log printing */
                System.out.println(
                                "Client " + userName + " [" + userIP + "]: ");
                System.out.println("Ran Command: NOTHOSTING");
                
                /* Removes user from hosting-list, to be broadcast out. */
                ptrOnlinePlayersList.removeUserHosting(userName);

                /* Show the new data */
                ptrOnlinePlayersList.showData();
                

            } else if (commandFromClient.equals("JOINGAME")) {
                /* For server log printing */
                System.out.println(
                                "Client " + userName + " [" + userIP   + "]: ");
                
                System.out.println("Ran Command: JOINGAME");
                /* TODO - Send an acknowledgement? */

            } else if (commandFromClient.equals("GETUPDATE")) {

                // Get the correct data to send back to client.
                String connectedUsers = Integer.toString(ptrOnlinePlayersList
                                                      .currentConnectedUsers());
                String waitingUsers = Integer.toString(ptrOnlinePlayersList
                                                      .currentAvailableUsers());
                String numberOfOpenGames = Integer.toString(ptrOnlinePlayersList
                                                          .numberOfOpenGames());
                String currentHostsAndPorts = "";
                if (ptrOnlinePlayersList.shouldWeUpdateClients()) {
                    haveWeSentTheUpdate = false;
                }
                if (!haveWeSentTheUpdate) {
                    currentHostsAndPorts
                                  = ptrOnlinePlayersList.currentHostsAndPorts();
                    haveWeSentTheUpdate = true;
                }

                /* Sample Sent Strings:
                 * "3 1 2"
                 * This is the case of no current hosts.
                 * 
                 * "5 2 3  Brendon 1236 Kyle 1237 Jim 1238"
                 * 5, is the total number of connected users
                 * 2, is the number of waiting users
                 * 3, is the number of open games
                 * Brendon 1236, is a username and its available port
                 * Kyle 1237, is another username and its available port
                 * ...
                 * 
                 * This string is potentially sent to 10+ users two times per
                 * second. It only sends longer versions (with hosts)
                 * of this string on new updates to save bandwidth.
                 */
                
                String toSendToClient = connectedUsers + " " + waitingUsers
                                + " " + numberOfOpenGames 
                                + " " + currentHostsAndPorts;

                /* Send the update to the client! */
                outToClient.println(toSendToClient);
                outToClient.flush();
            }

        }

    }

}
