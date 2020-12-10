import java.net.*;
import java.io.*;
import java.util.Random;

public class ThreadClient extends Thread{

    public static String hostName ="localhost";
    public static int portNumber = 4444;

    public static String [] regex;
    public static boolean printOutput;
    public volatile long[] times;
    public static int minWait;
    public static int boundWait;

    public ThreadClient(String[] regex, int minWait, int boundWait, boolean printOutput) {
        super("ThreadClient");
        this.regex = regex;
        this.minWait = minWait;
        this.boundWait = boundWait;
        this.printOutput = printOutput;
    }

    public void run(){
        if (printOutput) {
            System.out.println("Launching client");
        }
        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {

            String fromServer;
            int regex_ind = 0, random_wait = 0;
            long start = 0, finish = 0;
            Random ran = new Random();
            times = new long[regex.length];

            while ((fromServer = in.readLine()) != null) { // Wait for the server to answer
                if (fromServer.equals("Connected to server")) { // Init of connection
                    if (printOutput) {
                        System.out.println("Connected to server");
                        System.out.println("Client is ready");
                    }
                } else { // Request answer
                    if (printOutput) {
                        System.out.println(fromServer.replace("@@@", "\n")); // Print answer
                    }
                    finish = System.currentTimeMillis();
                    times[regex_ind - 1] = finish - start;
                    if (printOutput) {
                        System.out.println("Request " + (regex_ind - 1) + " (" + regex[regex_ind - 1] + ") " + "lasted: "
                                + times[regex_ind - 1] + " ms.");
                    }
                } // Wait for a request from User
                if (regex_ind < regex.length) { // Use file
                    random_wait = ran.nextInt(boundWait) + minWait;
                    Thread.sleep(random_wait);
                    start = System.currentTimeMillis();
                    out.println(regex[regex_ind]);
                    regex_ind++;
                } else {
                    socket.close();
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public long[] get_times(){
        return times;
    }

}
