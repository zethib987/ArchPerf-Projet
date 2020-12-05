import java.io.*;
import java.util.*;

public class SmartServer {

    static String FILEPATH = "C:\\Users\\Thib\\Documents\\unif\\2020-2021\\Q1\\Architecture and Perf\\projet\\Prototype\\src\\dbdata.txt";
    static int portNumber = 4444;

    static String [] types;
    static String [] sentences;
    static String data [][];
    static int NLINES=0;
    static String output="";
    static String test[][];
    static Integer indexes[];

    static Thread threads[]=new Thread[6];

    static String request1 = "1,3;the(.*)";
    static String request2 = "0,4;civ(.*)";
    static String request3 = ";FER(.*)";
    static String request4 = ";why(.*)";
    //String [] requests = {request1,request2,request3,request4};


    public static void main(String []args) throws InterruptedException {

        System.out.println("Launching server");
        System.out.println("Loading database");


        try{ // Load database
            smart_dbLoad(FILEPATH);
        } catch (Exception e){ // Handle exception
            System.out.println(e);
        }
        System.out.println("Database loaded");
       // System.out.println(NLINES);
        long start = System.currentTimeMillis();
        QuickSort.quickSort();
        long end = System.currentTimeMillis();
        long res = end-start;
        indexes=startIndexes();
        System.out.println("Server is ready");



        // displayDb(data);
        Test(10);

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

    public static void Test(int nTest)  {
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
           // System.out.println("timer 1 :"+timeElapsed);
        }
        res[0]/=nTest;
       /* System.out.println("");
        System.out.println("-----------------------------------");
        System.out.println("");*/

        try {
            Thread.sleep(5);
        }
        catch(Exception e){
            System.out.println(e);
        }

        String s="";
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
            //System.out.println("timer 2 :"+timeElapsed);
        }
        res[1]/=nTest;
       /* System.out.println("");
        System.out.println("-----------------------------------");
        System.out.println("");*/

        System.out.println("meanTime 1:"+ res[0]);
        System.out.println("meanTime 2:"+ res[1]);



    }

    public static Integer [] startIndexes(){
        ArrayList<Integer> liste = new ArrayList<>();
        int current = Integer.parseInt(data[0][0]);
        for(int i=0;i<data[0].length;i++){
            if(Integer.parseInt(data[0][i])!=current){
                current = Integer.parseInt(data[0][i]);
                liste.add(i);
            }
        }
        indexes= new Integer[liste.size()];
        return (Integer[]) liste.toArray(indexes);
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


            int start =0;
            int i=0;

            for( i=0;i< request_types.length-1;i++){
                threads[i] = new  MultiSearchSmart(regex,start,indexes[i]);
                threads[i].start();
                start=indexes[i]+1;
            }

            threads[i] = new  MultiSearchSmart(regex,start,data[0].length-1);
            threads[i].start();
            for(int k=0;k<threads.length;k++){
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


            Thread t1 = new   MultiSearch(request_types,regex,0, NLINES/3);
            Thread t2 = new MultiSearch(request_types,regex,(NLINES/3) + 1, (NLINES/3)*2);
            Thread t3 = new MultiSearch(request_types,regex,(NLINES/3)*2 + 1, NLINES-1);
            t1.start();
            t2.start();
            t3.start();


            t1.join();
            t2.join();
            t3.join();



            if (output.length() == 0)
                return "No match found";
            else
                return output;
        } else {
            return "Usage: <types>;<regex>";
        }

    }




    public static String smart2_search(String request) {
        String[] split_request = request.split(";",2);
        if (split_request.length == 2) {
            String [] request_types;
            if (split_request[0].length() != 0) {
                request_types = split_request[0].split(",");
            } else { // If no <types> specified then all
                request_types = new String[] {"0", "1", "2", "3", "4", "5"};
            }
            String regex = split_request[1];
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < data[0].length; i++) {
                for (String request_type : request_types) {
                    if (data[0][i] == request_type && data[1][i].matches(regex)) {
                        sb.append("|" + i + ": " + data[1][i] + "|");
                    }
                }
            }

            String output = sb.toString();
            if (output.length() == 0)
                return "No match found";
            else
                return output;
        } else {
            return "Usage: <types>;<regex>";
        }

    }



}

