import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerThread implements Runnable {
    private Socket socket;
    public static ArrayList<ServerThread> clients = new ArrayList<>();
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    
    private String message = "";
    
 
    public ServerThread(Socket socket) {
        try {
            this.socket = socket;
            InputStream input;
            input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            OutputStreamWriter outWriter = new OutputStreamWriter(output);
            writer = new BufferedWriter(outWriter);
            username = reader.readLine();
            for (ServerThread client : clients) {
                if (client.username.equals(username)) {
                    broadcastMessage("SERVER: Duplicate username: " + username + ". Login rejected.");
                    this.writer.write("SERVER: Duplicate username: " + username + ". Login rejected.");
                    shutdown();
                    return;
                }
            }
            broadcastMessage("SERVER: " + username + " has entered chat.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
        
        clients.add(this);
    }
 
    @Override
    public void run() {

            while (socket.isConnected()) {
                try {
                    message = reader.readLine();
                    broadcastMessage(username + ": " + message);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    shutdown();
                    break;
                }
            }

    }

    private void shutdown() {
        removeClient();
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void broadcastMessage(String msg) {
        for (ServerThread client : clients) {
            try {
                if (!client.username.equals(username)) {
                    //asda
                    client.writer.write(msg);
                    client.writer.newLine();
                    client.writer.flush();
                } 
            } catch (IOException e) {
                shutdown();
            }
        }
    }
    
    public void sendMessage(String msg) {
        for (ServerThread client : clients) {
            try {
                if (client.username.equals(username)) {
                    //asda
                    client.writer.write(msg);
                    client.writer.newLine();
                    client.writer.flush();
                } 
            } catch (IOException e) {
                shutdown();
            }
        }
    }
    
    public void removeClient() {
        clients.remove(this);
        broadcastMessage("SERVER: " + username + " has left.");
    }
}