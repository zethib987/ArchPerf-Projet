import java.io.*;

/**
 * LaunchTest launches nClients ThreadClient objects that each individually emulates a client that performs a
 * mean of lambda requests/s. The requests are the one specified in the FILEPATH file (which should contain a
 * valid request by line). Once all the ThreadClient have joined, this function computes and output some
 * statistics about the time of the requests for each client.
 *
 * @author  Baptiste and Thibault
 * @version 1.0
 * @since   2020-12
 */
public class LaunchTest {

    // Settings for the user
    public static int nClients = 25; // Number of clients to launch
    public static String FILEPATH = "/media/sf_src/regexEasy.txt"; // Path to the file containing the regex requests
    public static double lambda = 0.20; // Request rate: mean number of request by s for one client
    public static boolean printDetails = false; // Print all the computed statistics on stdOut
    public static boolean printOutput = false; // Print the outputs of the requests on stdOut

    // Some useful variables
    public static ThreadClient[] clients; // Table containing all the clients thread
    public static String[] regex; // All the requests from FILEPATH
    public static long[][] times; // used for the statistics
    public static long[] means; // used for the statistics
    public static long[] totals; // used for the statistics
    public static long[] mins; // used for the statistics
    public static long[] maxs; // used for the statistics
    public static long[] vars; // used for the statistics

    public static void main(String[] args) throws InterruptedException {

        // Load the requests into regex
        loadRegex(FILEPATH);
        // Initialization of the variables
        clients = new ThreadClient[nClients];
        times = new long[nClients][regex.length];
        means = new long[nClients];
        totals = new long[nClients];
        mins = new long[nClients];
        maxs = new long[nClients];
        vars = new long[nClients];

        for (int i = 0; i < nClients; i++) { // Creating the ThreadClient objects
            clients[i] = new ThreadClient(times[i]);
        }
        // Launching the ThreadClient objects
        System.out.println("Launching " + nClients + " clients");
        for (int i = 0; i < nClients; i++) {
            clients[i].start();
        }
        // Waiting the ThreadClient objects
        long start = System.currentTimeMillis(); // For stats purpose
        for (int i = 0; i < clients.length; i++) {
            System.out.println("Waiting client " + i);
            clients[i].join();
        }

        long finish = System.currentTimeMillis(); // For stats purpose

        // Compute the mean, min, max, variance and std requests time for each client
        for (int i = 0; i < clients.length; i++) {
            long total = 0;
            mins[i] = Long.MAX_VALUE;
            for (int j = 0; j < times[i].length; j++) {
                total += times[i][j];
                if (mins[i] > times[i][j]) {
                    mins[i] = times[i][j];
                }
                if (maxs[i] < times[i][j]) {
                    maxs[i] = times[i][j];
                }
            }
            totals[i] = total;
            means[i] = total/regex.length;
            for (int j = 0; j < times[i].length; j++) {
                vars[i] += Math.pow((times[i][j] - means[i]), 2);
            }
            vars[i] /= (times[i].length - 1);
            if (printDetails) { // Print all the details by client
                System.out.println("Details of client " + i + ": ");
                System.out.println("Mean time " + means[i] + " ms");
                System.out.println("Min time " + mins[i] + " ms");
                System.out.println("Max time " + maxs[i] + " ms");
                System.out.println("Variance " + vars[i] + " ms");
                System.out.println("Std " + Math.sqrt(vars[i]) + " ms");
            }
        }

        // Compute the means of the mean, min, max, variance and std requests time for all the client
        long mean = 0, min = 0, max = 0, var = 0;
        for (int i = 0; i < clients.length; i++) {
            mean += means[i];
            min += mins[i];
            max += maxs[i];
            var += vars[i];
        }
        mean /= clients.length;
        min /= clients.length;
        max /= clients.length;
        var /= clients.length;

        // Print the statistics results
        System.out.println("===========================");
        System.out.println("Means for all the clients :");
        System.out.println("Mean time " + mean + " ms");
        System.out.println("Min time " + min + " ms");
        System.out.println("Max time " + max + " ms");
        System.out.println("Variance " + var + " ms");
        System.out.println("Std " + Math.sqrt(var) + " ms");
        System.out.println("===========================");
        System.out.println("Total running time " + (finish - start) + " ms");

    }

    /**
     * Compute the number of lines in the file 'fileName'
     */
    public static int howManyLines(String fileName){
        BufferedReader br;
        int nLines = 0;
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
     * Load all the requests in the file 'fileName' into the regex structure by reading each line of the file
     * and adding it to the structure
     */
    public static void loadRegex(String fileName) {
        BufferedReader br;
        int nLines = howManyLines(fileName);
        regex = new String[nLines];
        try {
            br = new BufferedReader(new FileReader(fileName));
            for (int i = 0; i < nLines; i++) {
                regex[i] = br.readLine();
            }
            br.close();
        } catch(Exception e) { // Error while reading database
            System.err.println("Error while reading database: " + e);
            System.exit(-1);
        }
    }

}
