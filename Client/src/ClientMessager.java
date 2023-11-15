import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientMessager {

    String serverAddress;
    int serverPort;
    private MessageHandler messageHandler;
    private Random random;
    private int startTag;
    private int startIndex;
    private HashMap<String,Integer> indexPerContact;
    private HashMap<String, String> tagPerContact;
    private HashMap<String,SecretKey> keyPerContact;
    private MessageDigest messageDigest;

    JFrame frame = new JFrame("Client Messager");
    JTextField inputBox = new JTextField(50);
    JTextArea allMessages = new JTextArea(16, 50);
    JLabel inputLabel = new JLabel("INPUT: ");
    JLabel messageBoxLabel = new JLabel("MESSAGES: ");
    JButton sendButton = new JButton("SEND"); // Added send button
    JButton receiveButton = new JButton("RECEIVE");
    JButton sendKeyButton = new JButton("SEND KEY");
    JButton receiveKeyButton = new JButton("RECEIVE KEY");

    public ClientMessager(String serverAddress, int port) {
        random = new Random();
        tagPerContact = new HashMap<>();
        indexPerContact = new HashMap<>();
        tagPerContact.put("Test1", "4");
        indexPerContact.put("Test1", 2);
        keyPerContact = new HashMap<>();
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        this.serverAddress = serverAddress;
        this.serverPort = port;
        try{

            Registry myRegistry = LocateRegistry.getRegistry(serverAddress, port);
            messageHandler = (MessageHandler) myRegistry.lookup("MessageHandlerService");

            inputBox.setEditable(true);
            allMessages.setEditable(false);

            JPanel inputPanel = new JPanel();
            inputPanel.add(inputLabel);
            inputPanel.add(inputBox);
            inputPanel.add(sendButton);
            inputPanel.add(receiveButton);
            inputPanel.add(sendKeyButton);
            inputPanel.add(receiveKeyButton);


            JPanel messagesPanel = new JPanel(new BorderLayout());
            messagesPanel.add(messageBoxLabel, BorderLayout.WEST);
            messagesPanel.add(new JScrollPane(allMessages), BorderLayout.CENTER);

            //userNameList.setListData(new String[]{"Randal", "Maxime"});

            frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
            frame.getContentPane().add(messagesPanel, BorderLayout.CENTER);
            frame.pack();

            sendButton.addActionListener(e -> {
                sendMessage(inputBox.getText(), "Test1");
                inputBox.setText("");
            });
            sendKeyButton.addActionListener(e -> {
                try {
                    messageHandler.sendKey(createKey("Test1"));
                    SwingUtilities.invokeLater(() -> {
                        allMessages.append("Key send" + "\n");
                    });

                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            receiveKeyButton.addActionListener(e -> {
                try {
                    SecretKey key = messageHandler.getKey();
                    keyPerContact.put("Test1", key);
                    if (key != null){
                        SwingUtilities.invokeLater(() -> {
                            allMessages.append("Key received" + "\n");
                        });
                    }
                    else{
                        SwingUtilities.invokeLater(() -> {
                            allMessages.append("No key received" + "\n");
                        });
                    }

                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            });

            // send message directly when pressing enter (instead of using button)
            inputBox.addActionListener(e -> {
                sendMessage(inputBox.getText(), "Test1");
                inputBox.setText("");
            });

            receiveButton.addActionListener(e->{
                try{
                    String message = messageHandler.get(indexPerContact.get("Test1"), hashTag(tagPerContact.get("Test1")));
                    if (message != null){
                        SwingUtilities.invokeLater(() -> {
                            allMessages.append(readMessage(message, "Test1") + "\n");
                        });
                    }
                    else{
                        SwingUtilities.invokeLater(() -> {
                            allMessages.append("No new messages" + "\n");
                        });
                    }

                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void sendMessage(String message, String contact){
        String newTag = String.valueOf(random.nextInt());
        int newIndex = random.nextInt(0,4);
        String constructedMessage = message + "||" + newIndex + "||" + newTag;
        //TODO encrypt constructed message.
        String encryptedMessage = "";
        try {
            encryptedMessage = encryptMessage(constructedMessage,contact);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            messageHandler.add(indexPerContact.get(contact), encryptedMessage, hashTag(tagPerContact.get(contact)));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        indexPerContact.put(contact, newIndex);
        tagPerContact.put(contact, newTag);
    }

    public String encryptMessage(String message, String contact) throws Exception{
        SecretKey key = keyPerContact.get(contact);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE,key);
        return Base64.getEncoder().encodeToString(c.doFinal(message.getBytes("UTF-8")));
    }
    public String decryptMessage(String encryptedMessage, String contact) throws Exception{
        SecretKey key = keyPerContact.get(contact);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE,key);
        return new String(c.doFinal(Base64.getDecoder().decode(encryptedMessage)),"UTF-8");
    }

    public void testEncryption() throws Exception{
        String ogM = "Dit is een message";
        String msg = encryptMessage(ogM, "Test1");
        String dm = decryptMessage(msg, "Test1");
        System.out.println();
    }

    public String readMessage(String encryptedMessage, String contact){
        String decryptedMessage = "";
        try {
            decryptedMessage = decryptMessage(encryptedMessage,contact);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String[] split = decryptedMessage.split("\\|\\|");
        indexPerContact.put(contact, Integer.valueOf(split[1]));
        tagPerContact.put(contact,split[2]);
        return split[0];
    }
    public SecretKey createKey(String contact){
        KeyGenerator keyGenAES = null;
        try {
            keyGenAES = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenAES.init(256, new SecureRandom());
        SecretKey masterKey = keyGenAES.generateKey();
        keyPerContact.put(contact,masterKey);
        return masterKey;


        //DerivationFunction derivationFunction = new


    }
    private String hashTag(String tag){
        byte[] bytes = tag.getBytes();
        byte[] hashed = messageDigest.digest(bytes);
        return Base64.getEncoder().encodeToString(hashed);
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