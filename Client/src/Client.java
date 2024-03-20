import java.net.*;
import java.util.Scanner;
import java.io.*;
 
/**
 * Käyttäjän koodi
 */
public class Client {
    Socket socket;
    static BufferedWriter writer;
    static BufferedReader reader;
    String username;
    
    /**
     * @param socket Soketti
     * @param username Käyttäjänimi mikä kysytään mainissa
     */
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
        
            InputStream input;
            input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(output));
            
            sendMessage(username);
        } catch (IOException e) {
            shutdown();
        }
        
        
    }
 
    /**
     * main kysyy käyttäjältä nimen ja laittaa säikeet päälle.
     * @param args hostname ja portti kysytään argumenteissa
     */
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
    
    /**
     * Lähettää viestin rivinvaihdon kanssa, ja tyhjentää puskurin ettei viesti jää roikkumaan.
     * @param msg Viesti
     */
    public void sendMessage(String msg) {
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            shutdown();
        }
    }
    
    /**
     * Säie joka kuuntelee tulevia viestejä ja tulostaa ne.
     */
    public void listenIncoming() {
        Runnable listener = new Runnable() {
            @Override
            public void run() {
                String incomingMessage;
                
                while (socket.isConnected()) {
                    try {
                        incomingMessage = reader.readLine();  
                        if (incomingMessage == null) break; // random korjaus
                        System.out.println(incomingMessage);
                              
                    } catch (IOException e) {
                        shutdown();
                    }
                }
            }
        };
        // Säie määritellään yllä, ja tässä vasta käyntiin.
        new Thread(listener).start();
        
    }
    
    /**
     * Kuuntelee mitä käyttäjä kirjoittelee konsoliin ja lähettää eteenpäin
     */
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
        scanner.close();
    }
    
    
    /**
     * Shutdown sulkee kaikki mitä keksin sulkea.
     */
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