import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(5000);
            System.out.println("Server started. Listening for connections...");
        } catch (Exception e) {
            System.out.println("Exception in Server() constructor: " + e.getMessage());
        }
    }

    public void startServer() {
        try {
            while (!this.serverSocket.isClosed()) {
                Socket clientSocket = this.serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                Handler handler = new Handler(clientSocket);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.out.println("Exception in startServer(): " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
