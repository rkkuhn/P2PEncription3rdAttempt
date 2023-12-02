import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Handler implements Runnable{
    
    public static ArrayList<Handler> clientHandlerList = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String clientUsername;

    public Handler(Socket socket){
        try {
            this.socket = socket;
            this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            
            // Read the username of the connected client
            Message usernameObject = (Message)this.objectInputStream.readObject();
            this.clientUsername = usernameObject.getSender();
            clientHandlerList.add(this);

            // Notify all clients about the new connection
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat");

        } catch (Exception e) {
            System.out.println("Exception in Handler() constructor: " + e.getMessage());
            removeHandler();
        }
    }

    @Override
    public void run() {
        try {
            while(socket.isConnected()){
                Message messageFromClient = (Message)objectInputStream.readObject();
                String decryptedMessage = CaesarCipher.decrypt(messageFromClient.getMessage()); // Decrypt the message
                broadcastMessage(clientUsername + ": " + decryptedMessage);

                if(decryptedMessage.equalsIgnoreCase("quit")){
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in run(): " + e.getMessage());
            removeHandler();
        }
    }

    // Method to send a message to all connected clients
    private void broadcastMessage(String message){
        for(Handler handler : clientHandlerList){
            try {
                String encryptedMessage = CaesarCipher.encrypt(message); // Encrypt the message
                handler.objectOutputStream.writeObject(new Message("SERVER", encryptedMessage));
                handler.objectOutputStream.flush();
            } catch (Exception e) {
                System.out.println("Exception in broadcastMessage(): " + e.getMessage());
                handler.removeHandler();
            }
        }
    }

    // Method to remove a handler (client disconnect)
    private void removeHandler(){
        clientHandlerList.remove(this);
        try {
            if(objectInputStream != null){
                objectInputStream.close();
            }
            if(objectOutputStream != null){
                objectOutputStream.close();
            }
            if(this.socket != null){
                this.socket.close();
            }
        } catch (Exception e) {
            System.out.println("Exception in removeHandler(): " + e.getMessage());
        }
    }
}
