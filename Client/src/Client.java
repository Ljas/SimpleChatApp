import java.net.*;
import java.io.*;
 
/**
 * This program demonstrates a simple TCP/IP socket client that reads input
 * from the user and prints echoed message from the server.
 *
 * @author www.codejava.net
 */
public class Client {
 
    public static void main(String[] args) {
        if (args.length < 2) return;
 
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        
        String username = System.console().readLine("Input username: ");
 
        try (Socket socket = new Socket(hostname, port)) {
 
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
 
            Console console = System.console();
            String text;
            
            text = "/login " + username;  
            writer.println(text);
            
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
            String nameACK = reader.readLine();
            
            if (nameACK.equals("1")) {
                System.out.println("Name not available.");
                socket.close();
                reader.close();
                writer.close();
                return;
            }
 
            do {
                text = console.readLine("Enter text: ");
 
                writer.println(text);
 
                input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
 
                String time = reader.readLine();
                
                if (time.equals("/quit")) {
                    socket.close();
                    break;
                }
 
                System.out.println(time);
 
            } while (!text.equals("bye"));
 
            socket.close();
 
        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}