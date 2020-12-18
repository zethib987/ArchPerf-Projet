import java.net.*;
import java.io.*;
import java.util.concurrent.Semaphore;

/**
 * BasicMultiServerThread first creates the input and output stream to allow the communication with the client
 * When it is done, send a message of connection to the client and wait for the client to send requests.
 * When a request is received, the thread begins to search in the database and compute the result.
 * The result is then sent to the client and the server starts waiting again for a request.
 *
 * @author  Baptiste and Thibault
 * @version 1.0
 * @since   2020-12
 */
public class BasicMultiServerThread extends Thread{

    // Some useful variables
    public final Socket socket; // The new socket of the client
    public static Semaphore semData; // Used to limit the number of simultaneous raw_search()
    public static String[] types; // data structure
    public static String[] sentences; // data structure
    public static boolean print; // Print the requests on stdOut

    public BasicMultiServerThread(Socket socket) {
        super("BasicMultiServerThread");
        this.socket = socket;
        BasicMultiServerThread.semData = BasicServer.semData;
        BasicMultiServerThread.types = BasicServer.types;
        BasicMultiServerThread.sentences = BasicServer.sentences;
        BasicMultiServerThread.print = BasicServer.print;
    }

    public void run() {

        try (
                // Create the input and output streams based on the socket of the client
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            String inputLine, outputLine;
            // Approve the connection to the server by sending a connection message to the client
            outputLine = "Connected to server";
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) { // while socket is busy
                semData.acquire(1); // wait for a raw_search opportunity
                if (print) {
                    System.out.println("Request: " + inputLine);
                }
                outputLine = raw_search(inputLine); // Perform the search request
                semData.release(1); // free the sem
                out.println(outputLine); // send to the client the answer
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    /**
     * Perform a very simple search by iterating through the type structures and sentences by simply
     * checking if it matches these two criteria. Once the conditions are satisfied it adds the sentence to a
     * string buffer which is returned at the end when the whole array has been browsed.
     */
    public static String raw_search(String request) {

        // Handle the request
        String[] split_request = request.split(";",2);
        if (split_request.length == 2) { // The request has a valid format
            String[] request_types;
            if (split_request[0].length() != 0) {
                request_types = split_request[0].split(",");
            } else { // If no <types> specified then all
                request_types = new String[] {"0", "1", "2", "3", "4", "5"};
            }
            String regex = split_request[1];

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < types.length; i++) { // Browse all the structure
                for (String request_type: request_types) { // Check for all the request types
                    if (types[i].equals(request_type) && sentences[i].matches(regex)) { // if match:
                        sb.append(sentences[i] + "@@@"); // Add to the string buffer
                    }
                }
            }

            String output = sb.toString(); // Handle the string buffer into one output
            if (output.length() == 0) // No match
                return "No match found";
            else // Match
                return output;
        } else { // Invalid format of request
            return "Usage: <types>;<regex>";
        }

    }
}
