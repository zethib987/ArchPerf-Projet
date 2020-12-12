import java.io.*;

public class LaunchTest {

    public static int nClients = 100;
    public static String FILEPATH = "/media/sf_ArchPerf-Projet/regexSmall.txt";
    public static int minWait = 300;
    public static int boundWait = 1;
    public static boolean printDetails = true;
    public static boolean printOutput = false;

    public static ThreadClient[] clients;
    public static String[] regex;
    public static long[][] times;
    public static long[] means;
    public static long[] totals;

    public static void main(String[] args) throws InterruptedException {

        try{ // Load regex
            loadRegex(FILEPATH);
        } catch (Exception e){ // Handle exception
            System.err.println(e);
        }

        clients = new ThreadClient[nClients];
        times = new long[nClients][regex.length];
        means = new long[nClients];
        totals = new long[nClients];

        for (int i = 0; i < nClients; i++) {
            clients[i] = new ThreadClient(regex, times[i], minWait, boundWait, printOutput);
        }

        System.out.println("Launching " + nClients + " clients");
        for (int i = 0; i < nClients; i++) {
            clients[i].start();
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < clients.length; i++) {
            System.out.println("Waiting client " + i);
            clients[i].join();
            //times[i] = clients[i].get_times();
        }

        long finish = System.currentTimeMillis();

        for (int i = 0; i < clients.length; i++) {
            long total = 0;
            for (int j = 0; j < times[i].length; j++) {
                if (times[i][j] < 0) {
                    System.out.println("client = " + i + " request = " + j);
                }
                total += times[i][j];
            }
            totals[i] = total;
            means[i] = total/regex.length;
            if (printDetails) {
                System.out.println("Mean time by request " + means[i] + " ms for client " + i);
                //System.out.println("Total time for all the requests " + total + " ms for client " + i);
            }
        }

        long total = 0, mean = 0;
        for (int i = 0; i < clients.length; i++) {
            total += totals[i];
            mean += means[i];
        }
        mean /= clients.length;
        System.out.println("Mean time by request " + mean + " ms for all clients ");
        System.out.println("Total time elapsed: " + (finish - start));
        //System.out.println("Total time for all clients " + total + " ms for all clients ");

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
