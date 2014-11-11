package x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A simple Swing-based client for the capitalization server.
 * It has a main frame window with a text field for entering
 * strings and a textarea to see the results of capitalizing
 * them.
 */
public class PortMapperClient {

    private BufferedReader in;
    private PrintWriter out;
   

    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Enter in the
     * listener sends the textfield contents to the server.
     */
    public PortMapperClient() {}

        // Layout GUI
      
            public void send() {
                out.println("register a_2_maxtrix");
                   String response;
                try {
                	System.out.println("waiting");
                    response = in.readLine();                    
                    if (response == null || response.equals("")) {
                          System.exit(0);
                      }
                    else System.out.println(response);
                } catch (IOException ex) {
                       response = "Error: " + ex;
                }
               
            }

    /**
     * Implements the connection logic by prompting the end user for
     * the server's IP address, connecting, setting up streams, and
     * consuming the welcome messages from the server.  The Capitalizer
     * protocol says that the server sends three lines of text to the
     * client immediately after establishing a connection.
     */
    public void connectToServer() throws IOException {

        // Make connection and initialize streams
        Socket socket = new Socket("127.0.0.1", 9898);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Runs the client application.
     */
    public static void main(String[] args) throws Exception {
        PortMapperClient client = new PortMapperClient();    
        client.connectToServer();
        client.send();
        Thread.sleep(5000);
    }
}