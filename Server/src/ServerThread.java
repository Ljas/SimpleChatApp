import java.io.*;
import java.net.*;

/**
 * This thread is responsible to handle client connection.
 *
 * @author www.codejava.net
 */
public class ServerThread extends Thread {
    private Socket socket;
    private String username;
 
    public ServerThread(Socket socket) {
        this.socket = socket;
    }
 
    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
 
 
            String text;
 
            do {
                text = reader.readLine();
                if (text.length() != 0 && text.startsWith("/login")) {
                    try {
                        String[] split = text.split(" ");
                        if (split[1].length() < 4) {
                            writer.println("1");
                        }
                        else {
                            writer.println("0");
                            username = split[1];
                        }
                    }
                    catch (Exception E) {
                        writer.println("1");
                    }
                }
                String reverseText = new StringBuilder(text).reverse().toString();
                writer.println(username + ": " + reverseText);
 
            } while (!text.equals("bye"));
 
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}