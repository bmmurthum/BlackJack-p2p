import java.util.*;

/**
 * This allows for an object that each thread on the Lobby-Server can interact
 * with.
 *
 * @author Brendon Murthum, Javier Ramirez
 *
 */
public class LobbyOnlinePlayers {

    /** Used to track unique users and ending threads. */
    private ArrayList<String> userNames;
    /** Used to keep track of users. */
    private ArrayList<String> userIPs;

    /** The current number of users. Including both in-game and out. */
    private int totalUserCount = 0;

    /** Tracks whether needed to send an update. */
    private boolean shouldSendUpdate = false;

    /** User to keep track of waiting users. */
    private ArrayList<String> usersWaiting;
    /** Used for users to connect to each other. */
    private ArrayList<String> usersHosting;
    /** Used for users to connect to each other. */
    private ArrayList<String> userPorts;

    /** The constructor. */
    public LobbyOnlinePlayers() {
        /* These for main data */
        userIPs = new ArrayList<String>();
        userNames = new ArrayList<String>();

        /* These for logistics */
        usersWaiting = new ArrayList<String>();

        /* These for allowing connections of games. These two are same size. */
        usersHosting = new ArrayList<String>();
        userPorts = new ArrayList<String>();
    }

    /**
     * This function allows for returning to the client the number of games.
     * @return INT - number of open games
     */
    public int numberOfOpenGames() {
        return usersHosting.size();
    }

    /**
     * Allows each thread to add to these variables.
     *
     * @param addThisToUserNames
     *            - A username to add.
     * @param addThisToUserIPs
     *            - That user's IP address.
     */
    public void newConnectionValues(final String addThisToUserNames,
                    final String addThisToUserIPs) {

        this.userNames.add(addThisToUserNames);
        this.userIPs.add(addThisToUserIPs);
        this.usersWaiting.add(addThisToUserNames);
        totalUserCount = totalUserCount + 1;
    }

    /**
     * Adds a new user to the list of players that are hosting games.
     * @param playerUsername - Player to add.
     * @param theirPort - The port this player is listening on.
     * @return (1) if successful,
     *         (-1) if denied because list is FULL.
     */
    public int newUserHosting(final String playerUsername,
                    final String theirPort) {
        /* Only allow ten available games to join */
        if (usersHosting.size() > 9) {
            return (-1);

        } else {
            /* Add the hosting-user's info to table for broadcasting out */
            usersHosting.add(playerUsername);
            userPorts.add(theirPort);

            /* Remove user from the waiting list */
            usersWaiting.remove(playerUsername);

            /* Tells the updater to send broadcast */
            shouldSendUpdate = true;

            /* Returns "SUCCESS" */
            return (1);
        }
    }

    /**
     * Allows the exit of a user as a host.
     * @param playerUsername - The user to remove from the host-list.
     */
    public void removeUserHosting(final String playerUsername) {
        /* Add the user back to the waiting list */
        usersWaiting.add(playerUsername);

        /* Remove the old hosting-player's name and port from the host-list */
        int i;
        for (i = 0; i < usersHosting.size(); i++) {
            if (playerUsername.equals(usersHosting.get(i))) {
                usersHosting.remove(i);
                userPorts.remove(i);
                /* Tells the updater to send broadcast */
                shouldSendUpdate = true;
                break;
            }
        }
    }

    /**
     * Removes all elements that belong to the client with a specific username.
     *
     * @param userName
     *            - The username to remove from all the lists.
     */
    public void remove(final String userName) {
        /* Remove "Brendon" from the general list */
        for (int i = 0; i < userNames.size(); i++) {
            if (userNames.get(i).equals(userName)) {
                userNames.remove(i);
                userIPs.remove(i);
                /* Tells the updater to send broadcast */
                shouldSendUpdate = true;
            }
        }
        totalUserCount = userNames.size();
        /* Remove "Brendon" from hosting lists too */
        for (int i = 0; i < usersHosting.size(); i++) {
            if (usersHosting.get(i).equals(userName)) {
                usersHosting.remove(i);
                userPorts.remove(i);
                /* Tells the updater to send broadcast */
                shouldSendUpdate = true;
            }
        }
        /* Removes "Brendon" from the waiting list */
        usersWaiting.remove(userName);
    }

    /**
     * Tells a thread on the LobbyServer if the username is already taken.
     *
     * @param userName
     *            - The username to check.
     * @return TRUE, if username is taken. FALSE, if not.
     */
    public boolean isUsernameTaken(final String userName) {
        return userNames.contains(userName);
    }

    /**
     * Returns the number of connected users.
     * @return INT - number of connected users.
     *         (-1) - if there has been no update.
     */
    public int currentConnectedUsers() {
        return userNames.size();
    }

    /**
     * Tells us how many users are waiting in the lobby.
     * @return INT - how many users are waiting in the lobby.
     */
    public int currentAvailableUsers() {
        return usersWaiting.size();
    }

    /**
     * This allows for threads to each handle their own client's updates.
     * @return BOOLEAN - should the thread update it's client.
     */
    public boolean shouldWeUpdateClients() {
        return shouldSendUpdate;
    }

    /**
     * This generates a string from the global tables to send to client.
     * @return String - The list of users and ports of host-players.
     */
    public String currentHostsAndPorts() {
        /* This string returned is dependent on hosts being added/removed */
        String stringToReturn = "";

        for (int i = 0; i < usersHosting.size(); i++) {
            stringToReturn = stringToReturn
                               + " " + usersHosting.get(i)
                               + " " + userPorts.get(i);
        }

        /* Example stringToReturn: "_Brendon_1236_Dave_1237" */
        return (stringToReturn);
    }

    /** Displays some information about the current state of the live users. */
    public void showData() {

        System.out.println("Showing Current Server Data...");

        if (totalUserCount > 1) {
            System.out.println(" Total Current Users: " + totalUserCount);

            /* Display all connected usernames */
            System.out.print(" Users: [");
            for (int j = 0; j < userNames.size() - 1; j++) {
                System.out.print(userNames.get(j) + ", ");
            }
            System.out.print(userNames.get(totalUserCount - 1) + "]\n");

            /* Display all user IPs */
            System.out.print(" Users IPs: [");
            for (int j = 0; j < userIPs.size() - 1; j++) {
                System.out.print(userIPs.get(j) + ", ");
            }
            System.out.print(userIPs.get(totalUserCount - 1) + "]\n");

            /* Display the waiting users */
            if (usersWaiting.size() > 1) {
                System.out.print(" Waiting Users: [");
                for (int j = 0; j < usersWaiting.size() - 1; j++) {
                    System.out.print(usersWaiting.get(j) + ", ");
                }
                System.out.print(usersWaiting.get(usersWaiting.size() - 1)
                                + "]\n");

            } else if (usersWaiting.size() == 1) {
                System.out.println(" Waiting User: " + usersWaiting.get(0));
            } else {
                System.out.println(" Waiting Users: " + "0");
            }

        } else if (totalUserCount == 1) {
            System.out.println(" Total Current Users: " + totalUserCount);
            System.out.println(" Username: " + userNames.get(0));
            System.out.println(" IP Address: " + userIPs.get(0));

            /* Display the waiting users */
            if (usersWaiting.size() > 1) {
                System.out.print(" Waiting Users: [");
                for (int j = 0; j < usersWaiting.size() - 1; j++) {
                    System.out.print(usersWaiting.get(j) + ", ");
                }
                System.out.print(usersWaiting.get(usersWaiting.size() - 1)
                                + "]\n");

            } else if (usersWaiting.size() == 1) {
                System.out.println(" Waiting User: " + usersWaiting.get(0));
            } else {
                System.out.println(" Waiting Users: " + "0");
            }

        } else {
            System.out.println(" Total Current Users: " + totalUserCount);
        }
        System.out.print("\n");

    }

}
