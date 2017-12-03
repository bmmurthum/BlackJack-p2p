import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class popUpWindow {

    private JFrame frame;
    private static String messageToDisplay;

    /**
     * Launch the application.
     */
    public static void newScreen(String mssg) {
        messageToDisplay = mssg;

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    popUpWindow window = new popUpWindow();
                    window.frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public popUpWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.getContentPane().setBackground(
                        UIManager.getColor("MenuItem.acceleratorForeground"));
        frame.getContentPane().setLayout(null);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Liberation Sans", Font.BOLD, 14));
        textArea.setText(messageToDisplay);
        textArea.setEditable(false);
        textArea.setBackground(UIManager.getColor("Menu.selectionBackground"));
        textArea.setBounds(22, 12, 244, 82);
        frame.getContentPane().add(textArea);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        btnClose.setBounds(85, 105, 117, 25);

        frame.getContentPane().add(btnClose);
        frame.setBounds(100, 100, 294, 172);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
