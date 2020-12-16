import java.io.*;

public class LaunchTest {

    public static int nClients = 50;
    public static String FILEPATH = "C:\\Users\\Thib\\Documents\\unif\\2020-2021\\Q1\\Architecture and Perf\\projet\\regexEasy.txt";
    //public static String FILEPATH = "C:\\Users\\bapti\\OneDrive\\Documents\\Education\\EPL\\Master\\Q9\\LINGI2241 - Architecture and performance of computer systems\\ArchPerf-Projet\\regexEasy.txt";
    public static double lambda = 0.1; // Rate (mean number of request by s)
    public static boolean printDetails = false;
    public static boolean printOutput = false;

    public static ThreadClient[] clients;
    public static String[] regex;
    public static long[][] times;
    public static long[] means;
    public static long[] totals;
    public static long[] mins;
    public static long[] maxs;
    public static long[] vars;

    public static void main(String[] args) throws InterruptedException {

        try{
            loadRegex(FILEPATH);
        } catch (Exception e){
            System.err.println(e);
        }

        clients = new ThreadClient[nClients];
        times = new long[nClients][regex.length];
        means = new long[nClients];
        totals = new long[nClients];
        mins = new long[nClients];
        maxs = new long[nClients];
        vars = new long[nClients];

        for (int i = 0; i < nClients; i++) {
            clients[i] = new ThreadClient(regex, times[i], lambda, printOutput);
        }

        System.out.println("Launching " + nClients + " clients");
        for (int i = 0; i < nClients; i++) {
            clients[i].start();
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < clients.length; i++) {
            System.out.println("Waiting client " + i);
            clients[i].join();
        }

        long finish = System.currentTimeMillis();

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
            if (printDetails) {
                System.out.println("Details of client " + i + ": ");
                System.out.println("Mean time " + means[i] + " ms");
                System.out.println("Min time " + mins[i] + " ms");
                System.out.println("Max time " + maxs[i] + " ms");
                System.out.println("Variance " + vars[i] + " ms");
                System.out.println("Std " + Math.sqrt(vars[i]) + " ms");
            }
        }

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
        System.out.println("===========================");
        System.out.println("Means for all the clients :");
        //System.out.println("Mean time " + mean + " ms");
        //System.out.println("Min time " + min + " ms");
        //System.out.println("Max time " + max + " ms");
        //System.out.println("Variance " + var + " ms");
        //System.out.println("Std " + Math.sqrt(var) + " ms");
	System.out.println(mean);
        System.out.println(min);
        System.out.println(max);
        System.out.println(var);
        System.out.println("===========================");
        System.out.println("Total running time " + (finish - start) + " ms");

    }

    public static int howManyLines(String fileName){
        BufferedReader br;
        int nLines=0;
        try{
            br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            while (line != null) {
                nLines++;
                line = br.readLine();
            }
            br.close();
        }catch(Exception e){
            System.err.println("Error in howManyLines(): " + e);
            System.exit(-1);
        }
        return nLines;
    }

    public static void loadRegex(String fileName) throws IOException {
        BufferedReader br;
        int nLines = howManyLines(fileName);
        regex = new String[nLines];
        try{
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
