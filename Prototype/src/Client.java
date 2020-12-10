import java.net.*;
import java.io.*;

public class Client {

    public static String hostName ="localhost";
    public static int portNumber = 4444;
    public static boolean print = true;

    public static void main(String[] args){

        System.out.println("Launching client");

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
           BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer, fromUser;

            while ((fromServer = in.readLine()) != null) { // Wait for the server to answer
                if (fromServer.equals("Connected to server")) { // Init of connection
                    System.out.println("Connected to server");
                    System.out.println("Client is ready");
                } else { // Request answer
                    if (print) {
                        System.out.println(fromServer.replace("@@@", "\n")); // Print answer
                    }
                }
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    out.println(fromUser);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }


}
