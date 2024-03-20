import java.net.*;
import java.util.Scanner;
import java.io.*;
 
/**
 * This program demonstrates a simple TCP/IP socket client that reads input
 * from the user and prints echoed message from the server.
 *
 * @author www.codejava.net
 */
public class Client {
    Socket socket;
    static BufferedWriter writer;
    static BufferedReader reader;
    String username;
    
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
        
            InputStream input;
            input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            OutputStreamWriter outWriter = new OutputStreamWriter(output);
            writer = new BufferedWriter(outWriter);
            
            sendMessage(username);
        } catch (IOException e) {
            shutdown();
        }
        
        
    }
 
    public static void main(String[] args) {
        if (args.length < 2) return;
 
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        
        String username = System.console().readLine("Input username: ");
        
        try {
            Socket socket = new Socket(hostname, port);
            Client client = new Client(socket, username);
            client.listenIncoming();
            client.listenOutgoing();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("end");
    }
    
    public void sendMessage(String msg) {
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            shutdown();
        }
    }
    
    public void listenIncoming() {
        Runnable listener = new Runnable() {
            @Override
            public void run() {
                String incomingMessage;
                
                while (socket.isConnected()) {
                    try {
                        incomingMessage = reader.readLine();  
                        if (incomingMessage == null) break;
                        System.out.println(incomingMessage);
                              
                    } catch (IOException e) {
                        shutdown();
                        
                    }
                }
            }
        };
        new Thread(listener).start();
        
    }
    
    public void listenOutgoing() {
        Scanner scanner = new Scanner(System.in);
        while (socket.isConnected()) {
            try {
                String msgToSend = scanner.nextLine();
                sendMessage(msgToSend);                
            } catch (Exception e) {
                shutdown();
            }
        }
    }
    
    
    public void shutdown() {
        if (!socket.isClosed()) {
            try {
                socket.close();
                reader.close();
                writer.close();
                System.out.println("Logged out " + username);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }
    
    
}