import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.SystemColor;
import javax.swing.JTextPane;

/**
 * This class generates all of the GUI for the initial connection phase.
 *
 * @author Brendon Murthum, Javier Ramirez
 *
 */
public class ConnectionGUI {

    /** The frame of the GUI. */
    private JFrame frmBlackjackGvsu;
    /** The text-field that all the elements go into. */
    private JTextField usernameInputTextField;

    /** Is the player currently connected to the main server? */
    private boolean connectedToServer = false;
    /** Is the player currently hosting a game? */
    private boolean hostingGame = false;
    /** Is the player currently playing a game? */
    private boolean playingGame = false;

    /** Holds the list of current hosts for the GUI. */
    private ArrayList<String> namesOfHostPlayers = new ArrayList<String>();
    /** Holds the list of current host-ports for the GUI. */
    private ArrayList<String> portsOfHostPlayers = new ArrayList<String>();

    /** The user's username. */
    private String username;

    /** This object allows for interaction with the lobbyServer. */
    private ConnectionManager connManager = new ConnectionManager();

    /**
     * Launch the application.
     *
     * @param args
     *            - Unused
     */
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ConnectionGUI window = new ConnectionGUI();
                    window.frmBlackjackGvsu.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public ConnectionGUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmBlackjackGvsu = new JFrame();
        frmBlackjackGvsu.setResizable(false);
        frmBlackjackGvsu.setTitle("BlackJack GVSU");
        frmBlackjackGvsu.getContentPane().setBackground(SystemColor.window);
        frmBlackjackGvsu.setBackground(UIManager.getColor("Button.shadow"));
        frmBlackjackGvsu.setForeground(UIManager.getColor("Button.highlight"));
        frmBlackjackGvsu.setBounds(100, 100, 540, 870);
        frmBlackjackGvsu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmBlackjackGvsu.getContentPane().setLayout(null);

        JPanel connectionPanel = new JPanel();
        connectionPanel.setBackground(UIManager.getColor("Button.highlight"));
        connectionPanel.setBorder(new LineBorder(
                        UIManager.getColor("MenuItem.disabledForeground")));
        connectionPanel.setBounds(15, 15, 510, 150);
        frmBlackjackGvsu.getContentPane().add(connectionPanel);
        connectionPanel.setLayout(null);

        usernameInputTextField = new JTextField();
        usernameInputTextField.setForeground(SystemColor.inactiveCaption);
        usernameInputTextField.setFont(new Font("Inconsolata", Font.PLAIN, 13));
        usernameInputTextField.setBounds(122, 48, 378, 28);
        connectionPanel.add(usernameInputTextField);
        usernameInputTextField.setColumns(10);

        JLabel lblUsername = new JLabel("Enter Username:");
        lblUsername.setForeground(SystemColor.controlShadow);
        lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
        lblUsername.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblUsername.setBounds(0, 44, 124, 28);
        connectionPanel.add(lblUsername);

        JButton btnConnect = new JButton("Connect");
        btnConnect.setForeground(UIManager
                        .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnConnect.setFont(new Font("Cantarell", Font.BOLD, 15));
        btnConnect.setBounds(10, 90, 240, 49);
        connectionPanel.add(btnConnect);

        JLabel lblConnectToLobby = new JLabel("Connect to Player Lobby");
        lblConnectToLobby.setHorizontalAlignment(SwingConstants.CENTER);
        lblConnectToLobby.setVerticalAlignment(SwingConstants.TOP);
        lblConnectToLobby.setFont(new Font("Cantarell", Font.PLAIN, 20));
        lblConnectToLobby.setBounds(0, 7, 512, 28);
        connectionPanel.add(lblConnectToLobby);

        JButton btnDisconnect = new JButton("Disconnect");
        btnDisconnect.setForeground(UIManager
                        .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnDisconnect.setFont(new Font("Cantarell", Font.BOLD, 15));
        btnDisconnect.setBounds(260, 90, 240, 50);
        btnDisconnect.setEnabled(false);
        connectionPanel.add(btnDisconnect);

        JPanel lobbyPanel = new JPanel();
        lobbyPanel.setBorder(new LineBorder(
                        UIManager.getColor("MenuItem.disabledForeground")));
        lobbyPanel.setBackground(UIManager.getColor("Button.light"));
        lobbyPanel.setBounds(15, 180, 510, 65);
        frmBlackjackGvsu.getContentPane().add(lobbyPanel);
        lobbyPanel.setLayout(null);

        JLabel lblLiveLobby = new JLabel("Live Lobby");
        lblLiveLobby.setVerticalAlignment(SwingConstants.TOP);
        lblLiveLobby.setHorizontalAlignment(SwingConstants.CENTER);
        lblLiveLobby.setFont(new Font("Cantarell", Font.PLAIN, 20));
        lblLiveLobby.setBounds(0, 7, 514, 33);
        lobbyPanel.add(lblLiveLobby);

        JLabel livePlayersLabel = new JLabel("Disconnected From Server...");
        livePlayersLabel.setForeground(
                        UIManager.getColor("MenuItem.disabledForeground"));
        livePlayersLabel.setVerticalAlignment(SwingConstants.TOP);
        livePlayersLabel.setHorizontalAlignment(SwingConstants.CENTER);
        livePlayersLabel.setFont(new Font("Cantarell", Font.PLAIN, 14));
        livePlayersLabel.setBounds(0, 35, 514, 22);
        lobbyPanel.add(livePlayersLabel);

        JPanel joinPanel = new JPanel();
        joinPanel.setBorder(new LineBorder(
                        UIManager.getColor("MenuItem.disabledForeground")));
        joinPanel.setBackground(UIManager.getColor("Button.light"));
        joinPanel.setBounds(15, 260, 248, 568);
        frmBlackjackGvsu.getContentPane().add(joinPanel);
        joinPanel.setLayout(null);

        JLabel lblHostedGames = new JLabel("Available Games");
        lblHostedGames.setVerticalAlignment(SwingConstants.TOP);
        lblHostedGames.setHorizontalAlignment(SwingConstants.CENTER);
        lblHostedGames.setFont(new Font("Cantarell", Font.PLAIN, 20));
        lblHostedGames.setBounds(0, 7, 250, 30);
        joinPanel.add(lblHostedGames);

        JLabel lblNumGamesAvailable = new JLabel("0 / 10 Games Available");
        lblNumGamesAvailable.setVerticalAlignment(SwingConstants.TOP);
        lblNumGamesAvailable.setHorizontalAlignment(SwingConstants.CENTER);
        lblNumGamesAvailable.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblNumGamesAvailable.setBounds(0, 35, 248, 25);
        joinPanel.add(lblNumGamesAvailable);

        JButton btnJoinGame1 = new JButton("Join");
        btnJoinGame1.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {

            }
        });
        btnJoinGame1.setVisible(false);
        btnJoinGame1.setForeground(
                  UIManager.getColor("MenuItem.acceleratorForeground"));
        btnJoinGame1.setFont(new Font("Cantarell", Font.BOLD, 15));
        btnJoinGame1.setBounds(143, 70, 90, 30);
        joinPanel.add(btnJoinGame1);

        JButton btnJoinGame2 = new JButton("Join");
        btnJoinGame2.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        });
        btnJoinGame2.setForeground(UIManager
                        .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnJoinGame2.setFont(new Font("Cantarell", Font.BOLD, 15));
        btnJoinGame2.setBounds(143, 120, 90, 30);
        joinPanel.add(btnJoinGame2);
        btnJoinGame2.setVisible(false);

        JButton btnJoinGame3 = new JButton("Join");
        btnJoinGame3.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        });
        btnJoinGame3.setForeground(UIManager
                        .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnJoinGame3.setFont(new Font("Cantarell", Font.BOLD, 15));
        btnJoinGame3.setBounds(143, 170, 90, 30);
        joinPanel.add(btnJoinGame3);
        btnJoinGame3.setVisible(false);

        JButton btnJoinGame4 = new JButton("Join");
        btnJoinGame4.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        });
        btnJoinGame4.setForeground(UIManager
                        .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnJoinGame4.setFont(new Font("Cantarell", Font.BOLD, 15));
        btnJoinGame4.setBounds(143, 220, 90, 30);
        joinPanel.add(btnJoinGame4);

        JButton btnJoinGame5 = new JButton("Join");
        btnJoinGame5.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        });
        btnJoinGame5.setForeground(UIManager
                        .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnJoinGame5.setFont(new Font("Cantarell", Font.BOLD, 15));
        btnJoinGame5.setBounds(143, 270, 90, 30);
        joinPanel.add(btnJoinGame5);

        JButton btnJoinGame6 = new JButton("Join");
        btnJoinGame6.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        });
        btnJoinGame6.setForeground(UIManager
                        .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnJoinGame6.setFont(new Font("Cantarell", Font.BOLD, 15));
        btnJoinGame6.setBounds(143, 320, 90, 30);
        joinPanel.add(btnJoinGame6);

        JButton btnJoinGame7 = new JButton("Join");
        btnJoinGame7.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        });
        btnJoinGame7.setForeground(UIManager
                        .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnJoinGame7.setFont(new Font("Cantarell", Font.BOLD, 15));
        btnJoinGame7.setBounds(143, 370, 90, 30);
        joinPanel.add(btnJoinGame7);

        JButton btnJoinGame8 = new JButton("Join");
        btnJoinGame8.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        });
        btnJoinGame8.setForeground(UIManager
           .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnJoinGame8.setFont(
                          new Font("Cantarell", Font.BOLD, 15));
        btnJoinGame8.setBounds(143, 420, 90, 30);
        joinPanel.add(btnJoinGame8);

        JButton btnJoinGame9 = new JButton("Join");
        btnJoinGame9.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        });
        btnJoinGame9.setForeground(UIManager
           .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnJoinGame9.setFont(
                          new Font("Cantarell", Font.BOLD, 15));
        btnJoinGame9.setBounds(143, 470, 90, 30);
        joinPanel.add(btnJoinGame9);

        JButton btnJoinGame10 = new JButton("Join");
        btnJoinGame10.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        });
        btnJoinGame10.setForeground(UIManager
           .getColor("CheckBoxMenuItem.acceleratorForeground"));
        btnJoinGame10.setFont(
                          new Font("Cantarell", Font.BOLD, 15));
        btnJoinGame10.setBounds(143, 520, 90, 30);
        joinPanel.add(btnJoinGame10);

        JLabel lblHost1 = new JLabel("Bob");
        lblHost1.setHorizontalAlignment(SwingConstants.CENTER);
        lblHost1.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblHost1.setBounds(0, 70, 148, 30);
        joinPanel.add(lblHost1);
        lblHost1.setVisible(false);

        JLabel lblHost2 = new JLabel("Charles");
        lblHost2.setHorizontalAlignment(SwingConstants.CENTER);
        lblHost2.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblHost2.setBounds(0, 120, 148, 30);
        joinPanel.add(lblHost2);
        lblHost2.setVisible(false);

        JLabel lblHost3 = new JLabel("Dave");
        lblHost3.setHorizontalAlignment(SwingConstants.CENTER);
        lblHost3.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblHost3.setBounds(0, 169, 148, 30);
        joinPanel.add(lblHost3);
        lblHost3.setVisible(false);

        JLabel lblHost4 = new JLabel("Pamela");
        lblHost4.setHorizontalAlignment(SwingConstants.CENTER);
        lblHost4.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblHost4.setBounds(0, 219, 148, 30);
        joinPanel.add(lblHost4);

        JLabel lblHost5 = new JLabel("James");
        lblHost5.setHorizontalAlignment(SwingConstants.CENTER);
        lblHost5.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblHost5.setBounds(0, 269, 148, 30);
        joinPanel.add(lblHost5);

        JLabel lblHost6 = new JLabel("Zach");
        lblHost6.setHorizontalAlignment(SwingConstants.CENTER);
        lblHost6.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblHost6.setBounds(0, 319, 148, 30);
        joinPanel.add(lblHost6);

        JLabel lblHost7 = new JLabel("Louis");
        lblHost7.setHorizontalAlignment(SwingConstants.CENTER);
        lblHost7.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblHost7.setBounds(0, 369, 148, 30);
        joinPanel.add(lblHost7);

        JLabel lblHost8 = new JLabel("Javier");
        lblHost8.setHorizontalAlignment(SwingConstants.CENTER);
        lblHost8.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblHost8.setBounds(0, 419, 148, 30);
        joinPanel.add(lblHost8);

        JLabel lblHost9 = new JLabel("Timothy");
        lblHost9.setHorizontalAlignment(SwingConstants.CENTER);
        lblHost9.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblHost9.setBounds(0, 469, 148, 30);
        joinPanel.add(lblHost9);

        JLabel lblHost10 = new JLabel("Samuel");
        lblHost10.setHorizontalAlignment(SwingConstants.CENTER);
        lblHost10.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblHost10.setBounds(0, 519, 148, 30);
        joinPanel.add(lblHost10);

        JPanel hostPanel = new JPanel();
        hostPanel.setBorder(new LineBorder(
                        UIManager.getColor("MenuItem.disabledForeground")));
        hostPanel.setBackground(UIManager.getColor("Button.highlight"));
        hostPanel.setBounds(277, 340, 248, 335);
        frmBlackjackGvsu.getContentPane().add(hostPanel);
        hostPanel.setLayout(null);

        JButton btnHostGame = new JButton("Host Game");
        btnHostGame.setFont(new Font("Cantarell", Font.BOLD, 15));
        btnHostGame.setEnabled(false);
        btnHostGame.setBounds(12, 12, 224, 263);
        hostPanel.add(btnHostGame);

        JTextPane txtpnClickHereTo = new JTextPane();
        txtpnClickHereTo.setFont(new Font("Cantarell", Font.PLAIN, 14));
        txtpnClickHereTo.setEditable(false);
        txtpnClickHereTo.setText(
                 "Click here to create a game that another player could join!");
        txtpnClickHereTo.setBounds(10, 278, 224, 46);
        hostPanel.add(txtpnClickHereTo);

        /* Allowing for "Enter" key to submit the username. */
        frmBlackjackGvsu.getRootPane().setDefaultButton(btnConnect);

        JPanel availablePanel = new JPanel();
        availablePanel.setBorder(new LineBorder(
                        UIManager.getColor("MenuItem.disabledForeground")));
        availablePanel.setBackground(UIManager.getColor("Button.light"));
        availablePanel.setBounds(277, 690, 248, 138);
        frmBlackjackGvsu.getContentPane().add(availablePanel);
        availablePanel.setLayout(null);

        JLabel lblInfo = new JLabel("More Information");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblInfo.setBounds(0, 0, 248, 138);
        availablePanel.add(lblInfo);

        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(new LineBorder(
                        UIManager.getColor("MenuItem.disabledForeground")));
        infoPanel.setBackground(UIManager.getColor("Button.light"));
        infoPanel.setBounds(277, 260, 248, 65);
        frmBlackjackGvsu.getContentPane().add(infoPanel);
        infoPanel.setLayout(null);

        JLabel lblAvailablePlayers = new JLabel("Available Players");
        lblAvailablePlayers.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvailablePlayers.setVerticalAlignment(SwingConstants.TOP);
        lblAvailablePlayers.setFont(new Font("Cantarell", Font.PLAIN, 20));
        lblAvailablePlayers.setBounds(0, 7, 248, 31);
        infoPanel.add(lblAvailablePlayers);

        JLabel lblNumPlayersAvailable = new JLabel("Disconnected...");
        lblNumPlayersAvailable.setForeground(UIManager.getColor(
                        "MenuItem.disabledForeground"));
        lblNumPlayersAvailable.setVerticalAlignment(SwingConstants.TOP);
        lblNumPlayersAvailable.setHorizontalAlignment(SwingConstants.CENTER);
        lblNumPlayersAvailable.setFont(new Font("Cantarell", Font.PLAIN, 14));
        lblNumPlayersAvailable.setBounds(0, 35, 248, 25);
        infoPanel.add(lblNumPlayersAvailable);

        /* Set the GUI list of available games INVISIBLE by initial default */
        btnJoinGame1.setVisible(false);
        lblHost1.setVisible(false);
        btnJoinGame2.setVisible(false);
        lblHost2.setVisible(false);
        btnJoinGame3.setVisible(false);
        lblHost3.setVisible(false);
        btnJoinGame4.setVisible(false);
        lblHost4.setVisible(false);
        btnJoinGame5.setVisible(false);
        lblHost5.setVisible(false);
        btnJoinGame6.setVisible(false);
        lblHost6.setVisible(false);
        btnJoinGame7.setVisible(false);
        lblHost7.setVisible(false);
        btnJoinGame8.setVisible(false);
        lblHost8.setVisible(false);
        btnJoinGame9.setVisible(false);
        lblHost9.setVisible(false);
        btnJoinGame10.setVisible(false);
        lblHost10.setVisible(false);

        /** Listener for "Connect" */
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {

                username = usernameInputTextField.getText();
                if (username.isEmpty()) {
                    popUpWindow.newScreen("Please input a username.");

                } else if (!username.matches("[A-Za-z0-9]+")) {
                    popUpWindow.newScreen("Incorrect username. "
                                + "Please enter alphanumeric characters only.");

                } else {
                    String userName = usernameInputTextField.getText();
                    int result = connManager.connectToServer(userName);
                    if (result == 1) {
                        connectedToServer = true;

                    } else if (result == -1) {
                        popUpWindow.newScreen("Username is already in use! "
                                        + "Please enter another username.");

                    } else {
                        popUpWindow.newScreen(
                                        "Error in connection!" + " Try again!");
                    }
                }

                if (connectedToServer) {
                    btnConnect.setEnabled(false);
                    btnDisconnect.setEnabled(true);
                    btnHostGame.setEnabled(true);
                    usernameInputTextField.setEditable(false);

                }
            }
        });

        /** Listener for "Disconnect" */
        btnDisconnect.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {

                connManager.disconnectFromServer();

                connectedToServer = false;
                btnConnect.setEnabled(true);
                btnDisconnect.setEnabled(false);
                btnHostGame.setEnabled(false);
                usernameInputTextField.setEditable(true);
                livePlayersLabel.setForeground(
                             UIManager.getColor("MenuItem.disabledForeground"));
                livePlayersLabel.setText("Disconnected From Server...");
                lblNumPlayersAvailable.setText("Disconnected...");
                lblNumPlayersAvailable.setForeground(
                     UIManager.getColor("MenuItem.disabledForeground"));
                lblNumGamesAvailable.setText("0 / 10 Games Available");
                lblNumGamesAvailable.setForeground(UIManager.getColor(
                                "MenuItem.disabledForeground"));
                /* Set the GUI list of available games INVISIBLE */
                btnJoinGame1.setVisible(false);
                lblHost1.setVisible(false);
                btnJoinGame2.setVisible(false);
                lblHost2.setVisible(false);
                btnJoinGame3.setVisible(false);
                lblHost3.setVisible(false);
                btnJoinGame4.setVisible(false);
                lblHost4.setVisible(false);
                btnJoinGame5.setVisible(false);
                lblHost5.setVisible(false);
                btnJoinGame6.setVisible(false);
                lblHost6.setVisible(false);
                btnJoinGame7.setVisible(false);
                lblHost7.setVisible(false);
                btnJoinGame8.setVisible(false);
                lblHost8.setVisible(false);
                btnJoinGame9.setVisible(false);
                lblHost9.setVisible(false);
                btnJoinGame10.setVisible(false);
                lblHost10.setVisible(false);
            }
        });

        /** Listener for "Host Game" */
        btnHostGame.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            	
            	String portToHost = findAvailablePort();
                /* Initiate our player as a host to another player */
                int result = connManager.initiateHostingGame(portToHost);
                if (result == 1) {
                    hostingGame = true;
                    
                    /* TODO - This could initiate a window change */
                    btnHostGame.setEnabled(false);

                } else {
                    /* There was some error in initiating the hosted-game */
                    hostingGame = false;

                }
            }
        });

        /** This does an action in the GUI on a timer */
        ActionListener timedPerformer = new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {

                String undigestedReport = connManager.getUpdate();

                /* For debugging */
                System.out.println("  REPORT: " + undigestedReport);

                /* If stream setup with the server */
                if (undigestedReport.equals("NOCOMM")) {
                    /* Establish sockets and stream to server before username */
                    btnConnect.setEnabled(false);
                    connManager.establishConnectionToServer();

                } else if (undigestedReport.equals("WAITING")) {
                    /* Validate the username dynamically */
                    String tempUsername = usernameInputTextField.getText();
                    if (!tempUsername.matches("[A-Za-z0-9]+")
                                    || tempUsername.length() > 10
                                    || tempUsername.isEmpty()) {

                        btnConnect.setEnabled(false);

                    } else {
                        btnConnect.setEnabled(true);
                    }
                }

                /* Grab updates from server when connected */
                if (connectedToServer && !undigestedReport.equals("NOCOMM")) {

                    if (undigestedReport.equals("LINEERROR")) {
                        /* For debugging */
                        System.out.println("  DEBUG: LineError...");

                        /* No response from server. Disconnected! */
                        connectedToServer = false;
                        btnConnect.setEnabled(true);
                        btnDisconnect.setEnabled(false);
                        btnHostGame.setEnabled(false);
                        usernameInputTextField.setEditable(true);

                        livePlayersLabel.setForeground(
                             UIManager.getColor("MenuItem.disabledForeground"));
                        livePlayersLabel.setText("Disconnected From Server...");

                        lblNumPlayersAvailable.setText("Disconnected...");
                        lblNumPlayersAvailable.setForeground(
                             UIManager.getColor("MenuItem.disabledForeground"));
                        lblNumGamesAvailable.setText("0 / 10 Games Available");
                        lblNumGamesAvailable.setForeground(UIManager.getColor(
                                        "MenuItem.disabledForeground"));
                        /* Set the GUI list of available games INVISIBLE */
                        btnJoinGame1.setVisible(false);
                        lblHost1.setVisible(false);
                        btnJoinGame2.setVisible(false);
                        lblHost2.setVisible(false);
                        btnJoinGame3.setVisible(false);
                        lblHost3.setVisible(false);
                        btnJoinGame4.setVisible(false);
                        lblHost4.setVisible(false);
                        btnJoinGame5.setVisible(false);
                        lblHost5.setVisible(false);
                        btnJoinGame6.setVisible(false);
                        lblHost6.setVisible(false);
                        btnJoinGame7.setVisible(false);
                        lblHost7.setVisible(false);
                        btnJoinGame8.setVisible(false);
                        lblHost8.setVisible(false);
                        btnJoinGame9.setVisible(false);
                        lblHost9.setVisible(false);
                        btnJoinGame10.setVisible(false);
                        lblHost10.setVisible(false);

                    } else {
                        /* Grab the values from the string sent by the server */
                        StringTokenizer tokens;
                        String numberOfTotalUsers;
                        String numberOfWaitingUsers;
                        int numberOfAvailableGames;
                        try {
                            /** For token handling */
                            tokens = new StringTokenizer(undigestedReport);
                            /** Server's reported number of connected users. */
                            numberOfTotalUsers = tokens.nextToken();
                            /** Server's reported number of waiting users. */
                            numberOfWaitingUsers = tokens.nextToken();
                            /** Server's reported number of open games. */
                            numberOfAvailableGames = Integer.parseInt(
                                            tokens.nextToken());

                            /* Set the GUI with these new numbers */
                            if (numberOfTotalUsers != "1") {
                                livePlayersLabel.setText(numberOfTotalUsers 
                                           + " Players Connected to Server...");
                            } else {
                                livePlayersLabel.setText(numberOfTotalUsers 
                                            + " Player Connected to Server...");
                            }
                            if (Integer.parseInt(numberOfTotalUsers) > 1) {
                                livePlayersLabel.setForeground(
                                                UIManager.getColor(
                                 "OptionPane.questionDialog.titlePane.shadow"));
                            } else {
                                /* Otherwise, make it gray */
                                livePlayersLabel.setForeground(
                                                UIManager.getColor(
                                                "MenuItem.disabledForeground"));
                            }

                            /* If available players > 0, make text green */
                            if (Integer.parseInt(numberOfWaitingUsers) > 0) {
                                lblNumPlayersAvailable.setForeground(
                                                UIManager.getColor(
                                 "OptionPane.questionDialog.titlePane.shadow"));
                            } else {
                                /* Otherwise, make it gray */
                                lblNumPlayersAvailable.setForeground(
                                                UIManager.getColor(
                                                "MenuItem.disabledForeground"));
                            }

                            if (numberOfWaitingUsers != "1") {
                                lblNumPlayersAvailable.setText(
                                                numberOfWaitingUsers 
                                                + " Waiting Users...");
                            } else {
                                lblNumPlayersAvailable.setText(
                                    numberOfWaitingUsers + " Waiting User...");
                            }

                            /* Update the GUI's "Num of Available Games" */
                            if (numberOfAvailableGames > 0) {
                                lblNumGamesAvailable.setText(
                                                      numberOfAvailableGames
                                                     + " / 10 Games Available");
                                lblNumGamesAvailable.setForeground(
                                                      UIManager.getColor(
                                 "OptionPane.questionDialog.titlePane.shadow"));
                            } else {
                                lblNumGamesAvailable.setText(
                                                "0 / 10 Games Available");
                                lblNumGamesAvailable.setForeground(UIManager
                                      .getColor("MenuItem.disabledForeground"));
                            }

                            /* Empty the list, to rebuild it */
                            if (tokens.hasMoreTokens()) {
                                namesOfHostPlayers.clear();
                                /* Clear the GUI list */
                                btnJoinGame1.setVisible(false);
                                lblHost1.setVisible(false);
                                btnJoinGame2.setVisible(false);
                                lblHost2.setVisible(false);
                                btnJoinGame3.setVisible(false);
                                lblHost3.setVisible(false);
                                btnJoinGame4.setVisible(false);
                                lblHost4.setVisible(false);
                                btnJoinGame5.setVisible(false);
                                lblHost5.setVisible(false);
                                btnJoinGame6.setVisible(false);
                                lblHost6.setVisible(false);
                                btnJoinGame7.setVisible(false);
                                lblHost7.setVisible(false);
                                btnJoinGame8.setVisible(false);
                                lblHost8.setVisible(false);
                                btnJoinGame9.setVisible(false);
                                lblHost9.setVisible(false);
                                btnJoinGame10.setVisible(false);
                                lblHost10.setVisible(false);
                            }
                            /* To iterate on which GUI to be updated */
                            int joinLinksAdded = 0;
                            /* Grab all of the users and ports */
                            while (tokens.hasMoreTokens()) {
                                try {
                                    String tmpUser = tokens.nextToken();
                                    String tmpPort = tokens.nextToken();

                                    namesOfHostPlayers.add(tmpUser);
                                    portsOfHostPlayers.add(tmpPort);
                                    joinLinksAdded++;
                                    /* TODO - Add this user to the GUI */

                                } catch (Exception e) {
                                    break;
                                }
                            }

                            /* Update the GUI list */
                            if (numberOfAvailableGames > 0) {
                                btnJoinGame1.setVisible(true);
                                lblHost1.setText(namesOfHostPlayers.get(0));
                                lblHost1.setVisible(true);
                            }
                            if (numberOfAvailableGames > 1) {
                                btnJoinGame2.setVisible(true);
                                lblHost2.setText(namesOfHostPlayers.get(1));
                                lblHost2.setVisible(true);
                            }
                            if (numberOfAvailableGames > 2) {
                                btnJoinGame3.setVisible(true);
                                lblHost3.setText(namesOfHostPlayers.get(2));
                                lblHost3.setVisible(true);
                            }
                            if (numberOfAvailableGames > 3) {
                                btnJoinGame4.setVisible(true);
                                lblHost4.setText(namesOfHostPlayers.get(3));
                                lblHost4.setVisible(true);
                            }
                            if (numberOfAvailableGames > 4) {
                                btnJoinGame5.setVisible(true);
                                lblHost5.setText(namesOfHostPlayers.get(4));
                                lblHost5.setVisible(true);
                            }
                            if (numberOfAvailableGames > 5) {
                                btnJoinGame6.setVisible(true);
                                lblHost6.setText(namesOfHostPlayers.get(5));
                                lblHost6.setVisible(true);
                            }
                            if (numberOfAvailableGames > 6) {
                                btnJoinGame7.setVisible(true);
                                lblHost7.setText(namesOfHostPlayers.get(6));
                                lblHost7.setVisible(true);
                            }
                            if (numberOfAvailableGames > 7) {
                                btnJoinGame8.setVisible(true);
                                lblHost8.setText(namesOfHostPlayers.get(7));
                                lblHost8.setVisible(true);
                            }
                            if (numberOfAvailableGames > 8) {
                                btnJoinGame9.setVisible(true);
                                lblHost9.setText(namesOfHostPlayers.get(8));
                                lblHost9.setVisible(true);
                            }
                            if (numberOfAvailableGames > 9) {
                                btnJoinGame10.setVisible(true);
                                lblHost10.setText(namesOfHostPlayers.get(9));
                                lblHost10.setVisible(true);
                            }

                        } catch (Exception e) {
                            /* Error where there shouldn't be */
                        }

                    }

                } else {
                    livePlayersLabel.setText("Disconnected From Server...");

                }
            }
        };
        Timer timer = new Timer(200, timedPerformer);
        timer.setRepeats(true);
        timer.start();
        }
    
    /**
     * Function that will find a port that is available 
     * 
     * @return int Available port
     */
    private String findAvailablePort() {
    	
    	String defaultPort = "1234";
    	boolean found = false;
    	
    	while(!found) {
    		if(portsOfHostPlayers.contains(defaultPort)) {
    			int curr = Integer.parseInt(defaultPort);
    			curr++;
    			defaultPort = String.valueOf(curr);
    		} 
    		else {
    			found = true;
    		}
    	}
    	
    	String portToSend = defaultPort;
    	
    	return portToSend;
    }
}
