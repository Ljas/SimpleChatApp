import java.io.*;
import java.net.*;
 
/**
 * Palvelin
 */
public class Server {
    ServerSocket serverSocket;
    int port;
    
    public Server(ServerSocket serverSocket, int port) {
        this.serverSocket = serverSocket;
        this.port = port;
    }
    
    public void StartServer() {
        System.out.println("Server listening on port " + port);
        try {
            while (!serverSocket.isClosed()) {
                // Pusketaan saapuvat yhteyden omiin säikeisiin
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                ServerThread client = new ServerThread(socket);
                Thread thread = new Thread(client);
                thread.start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    public static void main(String[] args) throws IOException {
        if (args.length < 1) return; // Suljetaan jos porttia ei ole määritelty
 
        int port = Integer.parseInt(args[0]);
        
        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket, port);
        server.StartServer();
 
        
    }
    
    public void closeSocket() {
        try {
            if (serverSocket != null) serverSocket.close();                
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

