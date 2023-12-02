import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String username;

    public Client(String username){
        this.username = username;

        try {
            this.socket = new Socket("localhost", 5000);
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());

            Message usernameObject = new Message(username, "username");
            this.objectOutputStream.writeObject(usernameObject);

        } catch (Exception e) {
            System.out.println("Exception in Client() constructor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username for the chat: ");
        String username = scanner.nextLine();
        Client client = new Client(username);

        client.listenForMessage();
        client.sendMessage();
        scanner.close();
    }

    public void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (Exception e) {
            System.out.println("Exception in sendMessage(): " + e.getMessage());
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                try {
                    Message messageFromServer = (Message) objectInputStream.readObject();
                    System.out.println("Enter the shift for decryption:");
                    int shift = scanner.nextInt();

                    if (shift != CaesarCipher.SHIFT) {
                        System.out.println("Incorrect shift was entered. Logging off from the server.");
                        sendMessage(new Message(username, "quit"));
                        break;
                    } else {
                        System.out.println("Congratulations, that is the correct shift.");
                        String decryptedMessage = CaesarCipher.decryptWithShift(messageFromServer.getMessage(), shift);
                        System.out.println(messageFromServer.getSender() + ": " + decryptedMessage);
                    }
                } catch (Exception e) {
                    System.out.println("Exception in listenForMessage(): " + e.getMessage());
                    break;
                }
            }
            scanner.close();
        }).start();
    }
}
