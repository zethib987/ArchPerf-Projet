import java.net.*;
import java.io.*;
import java.util.Random;

public class ThreadClient extends Thread{

    public static String hostName = "localhost";
    public static int portNumber = 4444;

    // DON'T TOUCH
    public static String[] regex;
    public static boolean printOutput;
    public volatile long[] times;
    public static long[] finishs;
    public static long[] starts;
    public static int minWait;
    public static int boundWait;

    public ThreadClient(String[] regex, int minWait, int boundWait, boolean printOutput) {
        super("ThreadClient");
        this.regex = regex;
        this.times = new long[regex.length];
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
            // DON'T TOUCH
            String fromServer;
            int regex_ind = 0, random_wait = 0;
            long start = 0, finish = 0;
            Random ran = new Random();
            finishs = new long[times.length];
            starts = new long[times.length];

            /* !!!!!! TO KEEP !!!!!!
            while ((fromServer = in.readLine()) != null) { // Wait for the server to answer
                if (fromServer.equals("Connected to server")) { // Init of connection
                    if (printOutput) {
                        System.out.println("Connected to server");
                        System.out.println("Client is ready");
                    }
                    // lancer un thread de lecture des reponses
                    Thread read = new ReadDataThread(in, finishs, printOutput);
                    read.start();
                    // lancer un thread d'envoie de requetes
                    Thread request = new MakeRequestThread(out, regex, starts, boundWait, minWait);
                    request.start();
                } else { // Request answer
                    if (printOutput) {
                        System.out.println(fromServer.replace("@@@", "\n")); // Print answer
                    }
                    finish = System.currentTimeMillis();
                    times[regex_ind - 1] = finish - start;
                }
                // Wait for a request from User
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
            */

            if ((fromServer = in.readLine()) != null) { // Wait for the server to answer
                if (fromServer.equals("Connected to server")) { // Init of connection
                    if (printOutput) {
                        System.out.println("Connected to server");
                        System.out.println("Client is ready");
                    }

                    MakeRequestThread request = new MakeRequestThread(out, regex, starts, boundWait, minWait);
                    ReadDataThread read = new ReadDataThread(in, finishs, printOutput);
                    request.start();
                    read.start();

                    request.join();
                    read.join();
                    for (int i = 0; i < times.length; i++) {
                        times[i] = finishs[i] - starts[i];
                    }
                } else {
                    System.out.println("Problem with the connection to server");
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

class ReadDataThread extends Thread {

    BufferedReader in;
    int regex_ind;
    long[] finishs;
    boolean printOutput;

    ReadDataThread(BufferedReader in, long[] finishs, boolean printOutput) {
        super("ReadDataThread");
        this.in = in;
        this.regex_ind = 0;
        this.finishs = finishs;
        this.printOutput = printOutput;
    }

    public void run(){
        String fromServer;
        try {
            while ((fromServer = in.readLine()) != null) {
                if (printOutput) {
                    System.out.println(fromServer.replace("@@@", "\n")); // Print answer
                }
                finishs[regex_ind] = System.currentTimeMillis();
                regex_ind ++;
                if (regex_ind >= finishs.length){
                    in.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}

class MakeRequestThread extends Thread {

    PrintWriter out;
    String[] regex;
    int regex_ind;
    volatile long[] starts;
    int boundWait;
    int minWait;

    MakeRequestThread(PrintWriter out, String[] regex, long[] starts, int boundWait, int minWait) {
        super("MakeRequestThread");
        this.out = out;
        this.regex = regex;
        this.regex_ind = 0;
        this.starts = starts;
        this.boundWait = boundWait;
        this.minWait = minWait;
    }

    public void run(){

        Random ran = new Random();
        int random_wait;

        while (regex_ind < regex.length) {
            random_wait = ran.nextInt(boundWait) + minWait;
            try {
                Thread.sleep(random_wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            starts[regex_ind] = System.currentTimeMillis();
            out.println(regex[regex_ind]);
            regex_ind++;
        }

    }

}
