import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

import java.rmi.RemoteException;

public class ClientMessager {
    String serverAddress;
    int serverPort;
    private MessageHandler messageHandler;
    private Random random;
    private HashMap<String,Integer> indexPerContactSend;
    private HashMap<String,Integer> indexPerContactReceive;
    private HashMap<String, String> tagPerContactSend;
    private HashMap<String, String> tagPerContactReceive;
    private HashMap<String,SecretKey[]> keyPerContact;
    private HashMap<String,SecretKey[]> derivedKeyPerContact;
    private MessageDigest messageDigest;
    private int boardSize;

    JFrame frame = new JFrame("Client Messager");
    JTextField inputBox = new JTextField(50);
    JTextArea allMessages = new JTextArea(16, 50);
    JLabel inputLabel = new JLabel("INPUT: ");
    JLabel messageBoxLabel = new JLabel("MESSAGES: ");
    JButton sendButton = new JButton("SEND"); // Added send button
    JButton receiveButton = new JButton("RECEIVE");
    JButton sendKeyButton = new JButton("SEND BUMP");
    JButton receiveKeyButton = new JButton("RECEIVE BUMP");

    public ClientMessager(String serverAddress, int port) {
        random = new Random();
        tagPerContactSend = new HashMap<>();
        indexPerContactSend = new HashMap<>();
        tagPerContactReceive = new HashMap<>();
        indexPerContactReceive = new HashMap<>();
        //tagPerContactSend.put("Test1", "4");
        //indexPerContactSend.put("Test1", 2);
        keyPerContact = new HashMap<>();
        derivedKeyPerContact = new HashMap<>();
        boardSize = 20;
        //createKey("Test1");
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

            frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
            frame.getContentPane().add(messagesPanel, BorderLayout.CENTER);
            frame.pack();

            sendButton.addActionListener(e -> {
                //receiveHandler(true, "Test1");
                sendMessage(inputBox.getText(), "Test1");
                inputBox.setText("");
            });
            sendKeyButton.addActionListener(e -> {
                try {
                    createKey("Test1");
                    String contact = "Test1";
//                    System.out.println("SEND");
//                    System.out.println("Tag: " + tagPerContactSend.get(contact));
//                    System.out.println("Index: " + indexPerContactSend.get(contact));
//                    System.out.println("Key: " + keyPerContact.get(contact)[0]);

                    Bump bump = new Bump(tagPerContactSend.get(contact), indexPerContactSend.get(contact), keyPerContact.get(contact)[0]);
                    messageHandler.sendBump(bump);
                    SwingUtilities.invokeLater(() -> {
                        allMessages.append("Bump send" + "\n");
                    });
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            receiveKeyButton.addActionListener(e -> {
                try {
                    String contact = "Test1";
                    Bump bump = messageHandler.getBump();
//                    System.out.println("RECEIVE");
//                    System.out.println("Tag: " + bump.getTag());
//                    System.out.println("Index: " + bump.getIndex());
//                    System.out.println("Key: " + bump.getMasterkey());
                    if (keyPerContact.containsKey(contact)){
                        SecretKey[] keys = keyPerContact.get("Test1");
                        keys[1] = bump.getMasterkey();
                        keyPerContact.put("Test1", keys);
                        derivedKeyPerContact.put("Test1",keys);

                    }
                    else{
                        SecretKey[] keys = new SecretKey[2];
                        keys[1] = bump.getMasterkey();
                        keyPerContact.put("Test1", keys);
                        derivedKeyPerContact.put("Test1",keys);
                    }
                    indexPerContactReceive.put(contact,bump.getIndex());
                    tagPerContactReceive.put(contact, bump.getTag());
                    //keyPerContact.put("Test1", key);
                    //derivedKeyPerContact.put("Test1",key);
                    if (bump != null){
                        SwingUtilities.invokeLater(() -> {
                            allMessages.append("Bump received" + "\n");
                        });
                    }
                    else{
                        SwingUtilities.invokeLater(() -> {
                            allMessages.append("No bump received" + "\n");
                        });
                    }
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            });
            inputBox.addActionListener(e -> {
                //receiveHandler(true, "Test1");
                sendMessage(inputBox.getText(), "Test1");
                inputBox.setText("");
            });
            receiveButton.addActionListener(e->{
                receiveHandler(false, "Test1");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void receiveHandler(boolean isSend,String contact){
        try{
            String message = messageHandler.get(indexPerContactReceive.get(contact), hashTag(tagPerContactReceive.get(contact)));
            if (message != null){
                String toDisplayMessage = readMessage(message, contact);
                SwingUtilities.invokeLater(() -> {
                    allMessages.append("Not you: " + toDisplayMessage + "\n");
                });
                boolean controller = true;
                while(controller){
                    controller = checkForMessages(contact);
                }
            }
            else if(!isSend){
                SwingUtilities.invokeLater(() -> {
                    allMessages.append("No new messages" + "\n");
                });
            }
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }
    public boolean checkForMessages(String contact) throws RemoteException {
        String message = messageHandler.get(indexPerContactReceive.get(contact), hashTag(tagPerContactReceive.get(contact)));
        if (message != null){
            String toDisplayMessage = readMessage(message, contact);
            SwingUtilities.invokeLater(() -> {
                allMessages.append("Not you: " + toDisplayMessage + "\n");
            });
            return true;
        }
        return false;
    }
    public void sendMessage(String message, String contact){
        String newTag = String.valueOf(random.nextInt());
        int newIndex = random.nextInt(0,boardSize - 1);
        String constructedMessage = message + "||" + newIndex + "||" + newTag;
        String encryptedMessage = "";
        try {
            encryptedMessage = encryptMessage2(constructedMessage,contact);
            SecretKey[] keys = derivedKeyPerContact.get(contact);
            keys[0] = deriveKey(contact,true);
            derivedKeyPerContact.put(contact, keys);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            messageHandler.add(indexPerContactSend.get(contact), encryptedMessage, hashTag(tagPerContactSend.get(contact)));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        indexPerContactSend.put(contact, newIndex);
        tagPerContactSend.put(contact, newTag);
        SwingUtilities.invokeLater(() -> {
            allMessages.append("You: "+ message + "\n");
        });
    }
    public String encryptMessage2(String message, String contact) throws Exception{
        SecretKey derivedKey = derivedKeyPerContact.get(contact)[0];
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE,derivedKey);
        return Base64.getEncoder().encodeToString(c.doFinal(message.getBytes("UTF-8")));
    }
    public String decryptMessage(String encryptedMessage, String contact) throws Exception{
        SecretKey key = derivedKeyPerContact.get(contact)[1];
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE,key);
        return new String(c.doFinal(Base64.getDecoder().decode(encryptedMessage)),"UTF-8");
    }
    public String readMessage(String encryptedMessage, String contact){
        String decryptedMessage = "";
        try {
            decryptedMessage = decryptMessage(encryptedMessage,contact);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String[] split = decryptedMessage.split("\\|\\|");
        indexPerContactReceive.put(contact, Integer.valueOf(split[1]));
        tagPerContactReceive.put(contact,split[2]);
        SecretKey[] keys = derivedKeyPerContact.get(contact);
        keys[1] = deriveKey(contact,false);
        derivedKeyPerContact.put(contact,keys);
        return split[0];
    }
    public void createKey(String contact){
        KeyGenerator keyGenAES = null;
        try {
            keyGenAES = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenAES.init(256, new SecureRandom());
        SecretKey masterKey = keyGenAES.generateKey();
        String newTag = String.valueOf(random.nextInt());
        int newIndex = random.nextInt(0,boardSize - 1);
        indexPerContactSend.put(contact, newIndex);
        tagPerContactSend.put(contact, newTag);
        if (keyPerContact.containsKey(contact)) {
            SecretKey[] secretKeys = keyPerContact.get(contact);
            secretKeys[0] = masterKey;
            keyPerContact.put(contact,secretKeys);
            derivedKeyPerContact.put(contact,secretKeys);
        }
        else{
            SecretKey[] secretKeys = new SecretKey[2];
            secretKeys[0] = masterKey;
            keyPerContact.put(contact,secretKeys);
            derivedKeyPerContact.put(contact,secretKeys);
        }
//        SecretKey[] secretKeys = new SecretKey[2];
//        secretKeys[0] = masterKey;


        //return masterKey;
    }
    private String hashTag(String tag){
        byte[] bytes = tag.getBytes();
        byte[] hashed = messageDigest.digest(bytes);
        return Base64.getEncoder().encodeToString(hashed);
    }

    public SecretKey deriveKey(String contact, boolean isSend){
        try {
            SecretKey masterKey = null;
            SecretKey salt = null;
            if (isSend){
                masterKey = keyPerContact.get(contact)[0];
                salt = derivedKeyPerContact.get(contact)[0];
            }
            else{
                masterKey = keyPerContact.get(contact)[1];
                salt = derivedKeyPerContact.get(contact)[1];
            }
//            SecretKey masterKey = keyPerContact.get(contact);
//            SecretKey salt = derivedKeyPerContact.get(contact);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(masterKey.toString().toCharArray(), salt.getEncoded(),65536,256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            return secret;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }


    private void run() throws IOException {

    }

    public static void main(String[] args) throws Exception {

        var client = new ClientMessager("localhost", 1099);

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