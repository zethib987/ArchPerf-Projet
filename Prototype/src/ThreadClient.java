import java.net.*;
import java.io.*;
import java.util.Random;

/**
 * ThreadClient simulates a reel client that sends and reads simultaneously requests and answers to/from the server.
 * The time between two send of request is computed according to a Poisson distribution law with the lambda parameter.
 * ThreadClient launches a reading and a making request thread that operates independently In order to best simulate
 * the behaviour of a real customer. When all requests have been sent and they have received their answers, the class
 * computes the statistics of time for the measurements.
 *
 * @author  Baptiste and Thibault
 * @version 1.0
 * @since   2020-12
 */
public class ThreadClient extends Thread{

    // Some useful variables
    public static String hostName = "localhost"; // Address of the server
    public static int portNumber = 4444; // Port number
    public static String[] regex; // List of the requests
    public static double lambda; // Request rate: mean number of request by s for one client
    public static boolean printOutput; // Print the output on stdOut
    public long[] times; // used for the statistics
    public long[] finishs; // used for the statistics
    public long[] starts; // used for the statistics

    public ThreadClient(long[] times) {
        super("ThreadClient");
        ThreadClient.regex = LaunchTest.regex;
        this.times = times;
        ThreadClient.lambda = LaunchTest.lambda;
        ThreadClient.printOutput = LaunchTest.printOutput;
    }

    public void run(){
        if (printOutput) {
            System.out.println("Launching client");
        }
        try (
                // Create the client socket and init the input and output streams
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {

            // Small random sleep to avoid that all customers start at the same time
            Random ran = new Random();
            try {
                Thread.sleep(ran.nextInt(50));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Some variable initialization
            String fromServer;
            finishs = new long[times.length];
            starts = new long[times.length];

            if ((fromServer = in.readLine()) != null) { // Wait for the server to answer
                if (fromServer.equals("Connected to server")) { // Init of connection
                    if (printOutput) {
                        System.out.println("Connected to server");
                        System.out.println("Client is ready");
                    }
                    // Create the reading and request thread
                    MakeRequestThread request = new MakeRequestThread(out, starts);
                    ReadDataThread read = new ReadDataThread(in, finishs);
                    // Starting
                    read.start();
		            request.start();
                    // Waiting
		            read.join();
                    request.join();
                    for (int i = 0; i < times.length; i++) {
                        times[i] = finishs[i] - starts[i]; // Compute the statistics
                    }
                } else { // Not connected
                    System.out.println("Problem with the connection to server");
                }
            }

        } catch (UnknownHostException e) { // Errors
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

/**
 * ReadDataThread reads the answer of the requests from the clients. When an answer is received stops the timer.
 * The execution is finished when all the requests has received an answer from the server.
 */
class ReadDataThread extends Thread {

    // Some useful variables
    public BufferedReader in; // Input stream
    public int regex_ind; // Number of the request
    public long[] finishs; // used for the statistics
    public boolean printOutput; // Print the output on stdOut

    ReadDataThread(BufferedReader in, long[] finishs) {
        super("ReadDataThread");
        this.in = in;
        this.regex_ind = 0; // Set to 0 by default
        this.finishs = finishs;
        this.printOutput = ThreadClient.printOutput;
    }

    public void run(){
        String fromServer;
        try {
            while ((fromServer = in.readLine()) != null) { // While all the requests has not an answer from the server
                finishs[regex_ind] = System.currentTimeMillis(); // Stop the counter of the associated request
                if (printOutput) {
                    System.out.println(fromServer.replace("@@@", "\n")); // Print answer
                }
                regex_ind ++; // Increment the request counter
                if (!(regex_ind < finishs.length)){ // If all the request has an answer
                    in.close(); // Finish the execution and close the stream
                    break;
                }
            }
        } catch (IOException e) { // Error
            System.err.println(e);
        }
    }

}

/**
 * MakeRequestThread read the regex structure containing all the requests and send randomly according to the Poisson
 * distribution law a request to the server and starts the timer. Finishes the execution when all the regex are sent.
 */
class MakeRequestThread extends Thread {

    // Some useful variables
    private static double lambda; // Request rate: mean number of request by s for one client
    public static String[] regex; // List of the requests
    public PrintWriter out; // Output stream
    public int regex_ind; // Number of the request
    public long[] starts; // used for the statistics

    MakeRequestThread(PrintWriter out, long[] starts) {
        super("MakeRequestThread");
        this.out = out;
        this.regex_ind = 0; // Set to 0 by default
        this.starts = starts;
        MakeRequestThread.regex = ThreadClient.regex;
        MakeRequestThread.lambda = ThreadClient.lambda;
    }

    public void run(){

        int NRequest_by_s;

        while (regex_ind < regex.length) { // While all the requests are not sent
            NRequest_by_s = poisson_wait(); // Waiting time according to the Poisson law
            try {
                Thread.sleep(NRequest_by_s);
            } catch (InterruptedException e) { // Error
                e.printStackTrace();
            }
            starts[regex_ind] = System.currentTimeMillis(); // Start timer
            out.println(regex[regex_ind]); // Send the request to the server
            regex_ind++; // Increment the request counter
        }

    }

    /**
     * Simulates the time to sleep based on the number of requests sent by the client per second according
     * to the lambda parameter and a Poisson distribution.
     */
    public int poisson_wait() {
        int r = 0; // Number of request

        Random random = new Random();
        double a = random.nextDouble();
        double p = Math.exp(-lambda);

        while (a > p) { // Simulate the Poisson law
            r++;
            a = a - p;
            p = p * lambda / r;
        }
        if (r == 0) { // If no requests are supposed to be sent
	    try {
                Thread.sleep(1000); // sleep for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return poisson_wait(); // Restart the function
        } else { // Waiting time is simply 1/#request
            return 1000 / r;
        }
    }

}
