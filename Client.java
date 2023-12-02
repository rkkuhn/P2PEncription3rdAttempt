import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    
    // Socket and streams for communication
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String username;

    // Constructor to initialize client connection
    public Client(String username){
        this.username = username;

        try {
            // Establishing connection to server
            this.socket = new Socket("localhost", 5000);
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());

            // Send username to server
            Message usernameObject = new Message(username, "username");
            this.objectOutputStream.writeObject(usernameObject); 

        } catch (Exception e) {
            System.out.println("Exception in Client() constructor: " + e.getMessage());
        }
    }

    // Main method to start client
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username for the chat: ");
        String username = scanner.nextLine();
        Client client = new Client(username);

        // Start listening and sending messages
        client.listenForMessage();
        client.sendMessage();
        scanner.close();
    }

    // Method to send messages to server
    public void sendMessage(){
        Scanner scanner = new Scanner(System.in);
        
        try {
            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                String encryptedMessage = CaesarCipher.encrypt(messageToSend); // Encrypt the message
                Message messageToServer = new Message(username, encryptedMessage);
                objectOutputStream.writeObject(messageToServer);

                if(messageToSend.equalsIgnoreCase("quit")){
                    break;
                }
                
                objectOutputStream.flush();
            }
        } catch (Exception e) {
            System.out.println("Exception in sendMessage(): " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    // Method to listen for messages from server
    public void listenForMessage(){
        new Thread(() -> {
            while(socket.isConnected()){
                try {
                    Message messageFromServer = (Message) objectInputStream.readObject();
                    String decryptedMessage = CaesarCipher.decrypt(messageFromServer.getMessage()); // Decrypt the message
                    System.out.println(messageFromServer.getSender() + ": " + decryptedMessage);
                } catch (Exception e) {
                    System.out.println("Exception in listenForMessage(): " + e.getMessage());
                    break;
                }
            }
        }).start();
    }
}
