import java.net.*;
import java.io.*;
import java.util.concurrent.Semaphore;


/**
 * BasicServer runs a multi threaded server (one BasicMultiServerThread by client) that read requests from
 * the client, performs the search in the database and send back the answer to the client.
 * This class first load the database structure and then takes care of managing the connection of
 * the server with the clients.
 *
 * @author  Baptiste and Thibault
 * @version 1.0
 * @since   2020-12
 */
public class BasicServer {

    // Settings for the user
    public static String FILEPATH = "/media/sf_src/dbdata.txt"; // Path to the dbdata.txt
    public static int portNumber = 4444; // Number of the port to listen
    public static int NSearchSimultaneity = 1000; // Number of search the server can perform simultaneously
    public static boolean print = false; // Print the requests on stdOut

    // Some useful variables
    public static Semaphore semData; // Used to limit the number of simultaneous BasicMultiServerThread.raw_search()
    public static String [] types; // data structure
    public static String [] sentences; // data structure

    public static void main(String []args){

        System.out.println("Launching server");
        semData = new Semaphore(NSearchSimultaneity);

        System.out.println("Loading database");
        try{ // Load database
            raw_dbLoad(FILEPATH);
        } catch (Exception e){ // Handle exceptions
            System.out.println(e);
        }
        System.out.println("Database loaded");
        System.out.println("BasicServer is ready");

        boolean listening = true;
        // Launch a multi server thread for each client
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { // Create socket
            while (listening) { // Always listen (unless there is a problem)
                // Create a new thread for each new client that logs in to the server
                new BasicMultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) { // Error on port listening
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
        catch (Exception e){ // Other error
            System.err.println("Error while initiating the multi server: " + e);
            System.exit(-1);
        }

    }

    /**
     * Compute the number of lines in the file 'fileName'
     */
    public static int howManyLines (String fileName){
        BufferedReader br;
        int nLines=0;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            while (line != null) {
                nLines++;
                line = br.readLine();
            }
            br.close();
        } catch(Exception e) {
            System.err.println("Error in howManyLines(): " + e);
            System.exit(-1);
        }
        return nLines;
    }

    /**
     * Load the data base into types and sentences structures which respectively contains the type of the sentence and
     * the sentence associated to it (add them one by one by reading the file)
     */
    public static void raw_dbLoad(String fileName) {
        BufferedReader br;
        int nLines = howManyLines(fileName);
        types = new String[nLines];
        sentences = new String[nLines];

        try {
            br = new BufferedReader(new FileReader(fileName));
            String line;
            String[] line_split;
            for (int i = 0; i < nLines; i++) {
                line = br.readLine();
                line_split = line.split("@@@", 2);
                types[i] = line_split[0];
                sentences[i] = line_split[1];
            }
            br.close();
        } catch (Exception e) { // Error while reading database
            System.err.println("Error while reading database: " + e);
            System.exit(-1);
        }
    }

}


