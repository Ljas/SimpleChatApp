import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * ServerThread on henkilökohtainen säie käyttäjälle
 */
public class ServerThread implements Runnable {
    private Socket socket;
    /**
     * Käyttäjät pidetään staattisessa listassa jotta niihin pääsee helposti käsiksi.
     */
    public static ArrayList<ServerThread> clients = new ArrayList<>();
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    
    private String message = "";
    
 
    /**
     * Konstruktorissa käynnistellään lukijat ja kirjoittajat,
     * sekä luetaan ensimmäinen viesti jossa pitää olla käyttäjänimi
     * Jos käyttäjänimi löytyy jo listasta, suljetaan koko höskä.
     * @param socket Palvelimen socket
     */
    public ServerThread(Socket socket) {
        try {
            this.socket = socket;
            InputStream input;
            input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(output));
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
            shutdown();
            e.printStackTrace();
        }

        
        clients.add(this);
    }
 
    @Override
    public void run() {

            while (socket.isConnected()) {
                try {
                    // Laitetaan viesti kaikille nimen kanssa
                    message = reader.readLine();
                    broadcastMessage(username + ": " + message);
                } catch (IOException e) {
                    shutdown();
                    break;
                }
            }

    }

    /**
     * Poistetaan käyttäjä listalta ja suljetaan kaikki
     */
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
    
    /**
     * Pusketaan viesti kaikille muille paitsi itselle
     * @param msg Lähetettävä viesti
     */
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
    

    
    /**
     * Poistaa käyttäjän listalta jotta sille ei yritetä viestittää.
     */
    public void removeClient() {
        clients.remove(this);
        broadcastMessage("SERVER: " + username + " has left.");
    }
}