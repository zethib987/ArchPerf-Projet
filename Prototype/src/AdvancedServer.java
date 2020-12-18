import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.Semaphore;

public class AdvancedServer {

    static String FILEPATH = "/media/sf_src/dbdata.txt";
    static int portNumber = 4444;

    static Semaphore sem = new Semaphore(2);
    static String data [][];
    static int NLINES = 0;
    static int NTPT = Runtime.getRuntime().availableProcessors();

    static int indexes[];

    public static void main(String []args) throws InterruptedException {

        System.out.println("Launching server");
        System.out.println("Loading database");

        try{ // Load database
            smart_dbLoad(FILEPATH);
        } catch (Exception e){ // Handle exception
            System.out.println(e);
        }

        System.out.println("Database loaded");
        QuickSort.quickSort();
        indexes = startIndexes();
        System.out.println("AdvancedServer is ready");

       boolean listening = true;
        // Launch a multi server thread for each client
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
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

    public static void smart_dbLoad(String fileName) throws IOException {
        BufferedReader br;
        int nLines = howManyLines(fileName);
        NLINES = nLines;
        data = new String[2][nLines];

        try {
            br = new BufferedReader(new FileReader(fileName));
            String line;
            String[] line_split;
            for (int i = 0; i < nLines; i++) {
                line = br.readLine();
                line_split = line.split("@@@", 2);
                data[0][i] = line_split[0];
                data[1][i] = line_split[1];
            }
            br.close();
        } catch (Exception e) { // Error while reading database
            System.err.println("Error while reading database: " + e);
            System.exit(-1);
        }

    }

    // assuming that the indexes are consecutive numbers that start from 0 to n-1 (with n= number of threads)
    public static int [] startIndexes(){
        ArrayList<Integer> liste = new ArrayList<>();
        int current = Integer.parseInt(data[0][0]);
        liste.add(0);
        for(int i=0;i<data[0].length;i++){
            if(Integer.parseInt(data[0][i])!=current){
                current = Integer.parseInt(data[0][i]);
                liste.add(i);
            }
        }
        liste.add(data[0].length-1);
        Integer [] indexes= new Integer[liste.size()];
        indexes = liste.toArray(indexes);
        int res [] = new int [indexes.length];
        for(int i =0; i<res.length;i++){
            res[i]=indexes[i];
        }
        return res;

    }

    /*public static String smart4_search(String request,int [] indexesTab) throws InterruptedException {
        int[] indexes = indexesTab.clone();

        String[] split_request = request.split(";",2);
        if (split_request.length == 2) {
            String [] request_types;
            if (split_request[0].length() != 0) {
                request_types = split_request[0].split(",");
            } else { // If no <types> specified then all
                request_types = new String[] {"0", "1", "2", "3", "4", "5"};
            }
            String regex = split_request[1];

            threads = new Thread[request_types.length*NTPT];

            // creating and lauching all the threads
            for( int i=0;i<request_types.length;i++){
                int index = Integer.parseInt(request_types[i]);
                int D = indexes[index+1]-indexes[index]; // delta/distance
                D/= NTPT; // distance per threads
                indexes[index]-=1; // ugly trick to partition the limits of threads
                int j;
                for(j=0;j<NTPT-1;j++){ // on laisse le dernier pour après car cas particulier
                    threads[(i*NTPT)+j] = new AdvancedMultiSearch(regex,(indexes[index]+j*D)+1,indexes[index]+(j+1)*D);
                    threads[(i*NTPT)+j].start();
                }
                threads[(i*NTPT)+j] = new AdvancedMultiSearch(regex,(indexes[index]+j*D)+1,indexes[index+1]-1); // division pas tjrs entière du coup on prend le "reste"
                threads[(i*NTPT)+j].start();
                indexes[index]++; // correct the ugly trick's effect
            }

            // waiting for all the threads to finish
            for(int k=0;k< threads.length;k++){
                threads[k].join();
            }


            if (output.length() == 0)
                return "No match found";
            else
                return output;
        } else {
            return "Usage: <types>;<regex>";
        }


    }*/

}

