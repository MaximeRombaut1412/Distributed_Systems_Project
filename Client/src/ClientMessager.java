import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class ClientMessager {

    String serverAddress;
    int serverPort;
    private MessageHandler messageHandler;
    private Random random;
    private int startTag;
    private int startIndex;

    JFrame frame = new JFrame("Client Messager");
    JTextField inputBox = new JTextField(50);
    JTextArea allMessages = new JTextArea(16, 50);
    JLabel inputLabel = new JLabel("INPUT: ");
    JLabel messageBoxLabel = new JLabel("MESSAGES: ");
    JButton sendButton = new JButton("SEND"); // Added send button
    JButton receiveButton = new JButton("RECEIVE");

    public ClientMessager(String serverAddress, int port) {
        random = new Random();
        startTag = 0;
        startIndex = 1;

        this.serverAddress = serverAddress;
        this.serverPort = port;
        try{

            Registry myRegistry = LocateRegistry.getRegistry(serverAddress, port);
            messageHandler = (MessageHandler) myRegistry.lookup("MessageHandlerService");

            inputBox.setEditable(true);
            allMessages.setEditable(false);

            JPanel inputPanel = new JPanel(new BorderLayout());
            inputPanel.add(inputLabel, BorderLayout.WEST);
            inputPanel.add(inputBox, BorderLayout.CENTER);
            inputPanel.add(sendButton, BorderLayout.EAST);
            inputPanel.add(receiveButton, BorderLayout.NORTH);

            JPanel messagesPanel = new JPanel(new BorderLayout());
            messagesPanel.add(messageBoxLabel, BorderLayout.WEST);
            messagesPanel.add(new JScrollPane(allMessages), BorderLayout.CENTER);

            //userNameList.setListData(new String[]{"Randal", "Maxime"});

            frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
            frame.getContentPane().add(messagesPanel, BorderLayout.CENTER);
            frame.pack();

            sendButton.addActionListener(e -> {
                try {
                    messageHandler.add(2, new ValueTagPair("Dit is een message", 0).getMessage()); //TODO index moet random zijn
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
                inputBox.setText("");
            });

            // send message directly when pressing enter (instead of using button)
            inputBox.addActionListener(e -> {
                try {
                    messageHandler.add(2, new ValueTagPair("Dit is een message ||" + random.nextInt() + "||" + random.nextInt(), 0).getMessage()); //TODO index moet random zijn

                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
                inputBox.setText("");
            });

            receiveButton.addActionListener(e->{
                try{
                    String pair = messageHandler.get(2, 0);

                    SwingUtilities.invokeLater(() -> {
                        allMessages.append(pair + "\n");
                    });
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void constructMessage(String message){
        int randomInt = random.nextInt();

    }

    private void run() throws IOException {

    }

    public static void main(String[] args) throws Exception {

        var client = new ClientMessager("localhost", 1099);

        //client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        client.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        client.frame.pack();
        client.frame.setLocationRelativeTo(null);   //center screen on startup
        client.frame.setVisible(true);
        client.run();
    }
}