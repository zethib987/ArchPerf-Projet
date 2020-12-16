import java.net.*;
import java.io.*;
import java.util.Random;

public class ThreadClient extends Thread{

    public static String hostName = "localhost";
    public static int portNumber = 4444;

    public static String[] regex;
    public static boolean printOutput;
    public long[] times;
    public long[] finishs;
    public long[] starts;
    public static double lambda;

    public ThreadClient(String[] regex, long[] times, double lambda, boolean printOutput) {
        super("ThreadClient");
        ThreadClient.regex = regex;
        this.times = times;
        ThreadClient.lambda = lambda;
        ThreadClient.printOutput = printOutput;
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

            Random ran = new Random();
            try {
                Thread.sleep(ran.nextInt(50));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String fromServer;
            finishs = new long[times.length];
            starts = new long[times.length];

            if ((fromServer = in.readLine()) != null) { // Wait for the server to answer
                if (fromServer.equals("Connected to server")) { // Init of connection
                    if (printOutput) {
                        System.out.println("Connected to server");
                        System.out.println("Client is ready");
                    }

                    MakeRequestThread request = new MakeRequestThread(out, regex, starts, lambda);
                    ReadDataThread read = new ReadDataThread(in, finishs, printOutput);
                    read.start();
		            request.start();

		            read.join();
                    request.join();
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

}

class ReadDataThread extends Thread {

    public BufferedReader in;
    public int regex_ind;
    public long[] finishs;
    public boolean printOutput;

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
                finishs[regex_ind] = System.currentTimeMillis();
                if (printOutput) {
                    System.out.println(fromServer.replace("@@@", "\n")); // Print answer
                }
                regex_ind ++;
                if (!(regex_ind < finishs.length)){
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

    private static double lambda;
    public static String[] regex;
    public PrintWriter out;
    public int regex_ind;
    public long[] starts;

    MakeRequestThread(PrintWriter out, String[] regex, long[] starts, double lambda) {
        super("MakeRequestThread");
        this.out = out;
        this.regex_ind = 0;
        this.starts = starts;
        MakeRequestThread.regex = regex;
        MakeRequestThread.lambda = lambda;
    }

    public void run(){

        int NRequest_by_s;

        while (regex_ind < regex.length) {
            NRequest_by_s = poisson_wait();
            System.out.println(NRequest_by_s);
            try {
                Thread.sleep(NRequest_by_s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            starts[regex_ind] = System.currentTimeMillis();
            out.println(regex[regex_ind]);
            regex_ind++;
        }

    }

    public int poisson_wait() {
        int r = 0;
        Random random = new Random();
        double a = random.nextDouble();
        double p = Math.exp(-lambda);

        while (a > p) {
            r++;
            a = a - p;
            p = p * lambda / r;
        }
        if (r == 0) {
            return (int) poisson_wait();
        } else {
            return 1000 / r;
        }
    }

}
