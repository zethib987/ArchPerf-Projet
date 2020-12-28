import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * AdvancedServer runs a multi threaded server by starting one AdvancedMultiServerThread by client.
 * This class only does the pre-processing computing, ie the loading and the sorting of the database.
 * It's the AdvancedMultiServerThread class that is in charge of the requests managing
 *
 * @author  Baptiste and Thibault
 * @version 1.0
 * @since   2020-12
 */
public class AdvancedServer {

    // Settings for the user
    static String FILEPATH = "/media/sf_src/dbdata.txt"; // Path to the dbdata.txt
    static int portNumber = 4444; // Number of the port to listen

    // Global variables
    static Semaphore sem = new Semaphore(2);// Used to limit the number of simultaneous AdvancedMultiServerThread.raw_search()
    static String data [][]; // data structure used to store the database
    static int NLINES = 0; // global variable to store the number of lines in the database file
    static int NTPT = Runtime.getRuntime().availableProcessors();// number of processors available for the program
    static int indexes[]; // store the indexes of each section of the data array (one section per type)

    public static void main(String []args) throws InterruptedException {

        System.out.println("Launching server");
        System.out.println("Loading database");

        try{ // Load database
            smart_dbLoad(FILEPATH);
        } catch (Exception e){ // Handle exception
            System.out.println(e);
        }

        System.out.println("Database loaded");
        QuickSort.quickSort(); // use quicksort to sort the database by type
        indexes = startIndexes(); // compute the starts of each sections and store them into this array
        System.out.println("AdvancedServer is ready");

       boolean listening = true;
        // Launch a multi server thread for each client
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {// Always listen (unless there is a problem)
                // Create a new thread for each new client that logs in to the server
                new AdvancedMultiServerThread(serverSocket.accept()).start();
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
     * Compute the number of lines in the file 'fileName'.
     */
    public static int howManyLines (String fileName){
        BufferedReader br;
        int nLines=0; // store the number of lines in the file
        try {
            br = new BufferedReader(new FileReader(fileName)); // launch a buffer reader
            String line = br.readLine(); // read a line form the file
            while (line != null) { // loops until there's no more line to read
                nLines++;
                line = br.readLine();
            }
            br.close(); // close the buffer reader
        } catch(Exception e) {
            System.err.println("Error in howManyLines(): " + e);
            System.exit(-1);
        }
        return nLines;
    }

    /**
     * Load the data base from 'fileName' before receiving requests from clients.
     */
    public static void smart_dbLoad(String fileName) throws IOException {
        BufferedReader br;
        int nLines = howManyLines(fileName); // store the number of line in the file "fileName"
        NLINES = nLines; // store the number of line in the global variable that will e accessed by other functions
        data = new String[2][nLines]; // initialise the database array

        try {
            br = new BufferedReader(new FileReader(fileName));
            String line;
            String[] line_split;
            for (int i = 0; i < nLines; i++) {
                line = br.readLine();
                line_split = line.split("@@@", 2);
                data[0][i] = line_split[0]; // stores the type in the first column
                data[1][i] = line_split[1]; // stores the sentence in the second column
            }
            br.close();
        } catch (Exception e) { // Error while reading database
            System.err.println("Error while reading database: " + e);
            System.exit(-1);
        }

    }

    /**
     * Define where the different type's sections begin.
     * Stores the indexes in an array that will be accessed by concurrent threads
     * to performs efficient multi-threaded search throught the database.
     */
    public static int [] startIndexes(){
        ArrayList<Integer> liste = new ArrayList<>(); // creates a array list
        int current = Integer.parseInt(data[0][0]); // type of the first database's entry
        liste.add(0);
        for(int i=0;i<data[0].length;i++){ // iterates throught the entire database
            if(Integer.parseInt(data[0][i])!=current){ // check if the type is different form the current type
                current = Integer.parseInt(data[0][i]); // stores the new current type
                liste.add(i); // stores the index of the start of the new current type
            }
        }
        liste.add(data[0].length-1); // add the end of the database's array
        Integer [] indexes= new Integer[liste.size()];
        indexes = liste.toArray(indexes);// transforms the array list into an array of Integer
        int res [] = new int [indexes.length];
        for(int i =0; i<res.length;i++){ // cast the entries of the indexes array into simple int and stores them into the res array
            res[i]=indexes[i];
        }
        return res;

    }



}

