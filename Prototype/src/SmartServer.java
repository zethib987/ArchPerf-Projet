import java.io.*;
import java.util.*;

public class SmartServer {

    static String FILEPATH = "C:\\Users\\Thib\\Documents\\unif\\2020-2021\\Q1\\Architecture and Perf\\projet\\Prototype\\src\\dbdata.txt";
    static int portNumber = 4444;


    static String data [][];
    static int NLINES=0;
    static String output="";
    static int NTPT=3; // number of threads per type
    static int NTHREADS=6; // number of threads for the smart3 search

    static Integer indexes[];
    static Thread threads[];

    static String request1 = "1,3;the(.*)";
    static String request2 = "0,4;civ(.*)";
    static String request3 = ";FER(.*)";
    static String request4 = ";why(.*)";



    public static void main(String []args) throws InterruptedException {

        System.out.println("Launching server");
        System.out.println("Loading database");


        try{ // Load database
            smart_dbLoad(FILEPATH);
        } catch (Exception e){ // Handle exception
            System.out.println(e);
        }
        System.out.println("Database loaded");
        System.out.println("Number of lines:"+NLINES);
        QuickSort.quickSort();
        indexes=startIndexes();
        for(int i=0;i<indexes.length;i++) System.out.println(indexes[i]);
        System.out.println("Server is ready");



        // displayDb(data);
        Test(50);

       /* boolean listening = true;
        // Launch a multi server thread for each client
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new MultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) { // Error on port listening
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
        catch (Exception e){ // Other error
            System.err.println("Error while initiating the multi server: " + e);
            System.exit(-1);
        }*/

    }

    public static void Test(int nTest){ // sur ma machine la première fonction est généralement avantagée => si on test 2 fois la même, la première sera en générale plus rapide
        long [] res = new long [2];
        long start,finish,timeElapsed;
        start=0;
        for(int i=0;i<nTest;i++) {
            try { // Load database
                start = System.currentTimeMillis();
                smart3_search(request4);
            } catch (Exception e) { // Handle exception
                System.out.println(e);
            }
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            res[0]+=timeElapsed;

        }
        res[0]/=nTest;


        try {
            Thread.sleep(5);
        }
        catch(Exception e){
            System.out.println(e);
        }

        for(int i=0;i<nTest;i++) {
            try { // Load database
                start =System.currentTimeMillis();
                smart4_search(request4);
            } catch (Exception e) { // Handle exception
                System.out.println(e);
            }
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            res[1]+=timeElapsed;

        }
        res[1]/=nTest;

        System.out.println("meanTime 1:"+ res[0]);
        System.out.println("meanTime 2:"+ res[1]);

    }

    // assuming that the indexes are consecutive numbers that start from 0 to n-1 (with n= number of threads)
    public static Integer [] startIndexes(){
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
        indexes= new Integer[liste.size()];
        return (Integer[]) liste.toArray(indexes);
    }


    public static String smart4_search(String request) throws InterruptedException {
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
                    threads[(i*NTPT)+j] = new  MultiSearchSmart(regex,(indexes[index]+j*D)+1,indexes[index]+(j+1)*D);
                    threads[(i*NTPT)+j].start();
                }
                threads[(i*NTPT)+j] = new  MultiSearchSmart(regex,(indexes[index]+j*D)+1,indexes[index+1]-1); // division pas tjrs entière du coup on prend le "reste"
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

    }

    public static void smart_dbLoad(String fileName) throws IOException {
        BufferedReader br;
        int nLines = howManyLines(fileName);
        NLINES = nLines;
        data = new String[2][nLines];

        try{
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
        } catch(Exception e) { // Error while reading database
            System.err.println("Error while reading database: " + e);
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

    public static void displayDb(String [][] data) {
        for(int i=0;i<data[0].length && i<1000;i++){
            System.out.println("type:"+data[0][i]+" val:"+data[1][i]);
        }
    }
    

    public static String smart3_search(String request) throws InterruptedException {
        String[] split_request = request.split(";",2);
        if (split_request.length == 2) {
            String [] request_types;
            if (split_request[0].length() != 0) {
                request_types = split_request[0].split(",");
            } else { // If no <types> specified then all
                request_types = new String[] {"0", "1", "2", "3", "4", "5"};
            }
            String regex = split_request[1];

            threads = new Thread[NTHREADS];
            for(int i=0;i<threads.length;i++){
                threads[i]=new MultiSearch(request_types,regex,(NLINES/NTHREADS)*i, (NLINES/NTHREADS)*(i+1));
                threads[i].start();
            }
            /*Thread t1 = new MultiSearch(request_types,regex,0, NLINES/3);
            Thread t2 = new MultiSearch(request_types,regex,(NLINES/3) + 1, (NLINES/3)*2);
            Thread t3 = new MultiSearch(request_types,regex,(NLINES/3)*2 + 1, NLINES-1);
            t1.start();
            t2.start();
            t3.start();
            t1.join();
            t2.join();
            t3.join();*/

            for(int i=0;i<threads.length;i++){
                threads[i].join();
            }



            if (output.length() == 0)
                return "No match found";
            else
                return output;
        } else {
            return "Usage: <types>;<regex>";
        }

    }


}

