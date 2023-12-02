import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Handler implements Runnable {
    public static ArrayList<Handler> clientHandlerList = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String clientUsername;

    public Handler(Socket socket) {
        try {
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());

            Message usernameObject = (Message) this.objectInputStream.readObject();
            this.clientUsername = usernameObject.getSender();
            clientHandlerList.add(this);

            broadcastMessage("SERVER: " + clientUsername + " has entered the chat");

        } catch (Exception e) {
            System.out.println("Exception in Handler() constructor: " + e.getMessage());
            removeHandler();
        }
    }

    @Override
    public void run() {
        try {
