import javax.management.StringValueExp;
import java.lang.reflect.Array;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartServer {

    static String FILEPATH = "C:\\Users\\Thib\\Documents\\unif\\2020-2021\\Q1\\Architecture and Perf\\projet\\Prototype\\src\\dbdata.txt";
    static int portNumber = 4444;

    static String [] types;
    static String [] sentences;
    static String data [][];


    static String request1 = "1,3;the(.*)";
    static String request2 = "0,4;civ(.*)";
    static String request3 = ";FER(.*)";
    static String request4 = ";why(.*)";
    //String [] requests = {request1,request2,request3,request4};


    public static void main(String []args){

        System.out.println("Launching server");
        System.out.println("Loading database");

        try{ // Load database
            raw_dbLoad(FILEPATH);
            smart_dbLoad(FILEPATH);
        } catch (Exception e){ // Handle exception
            System.out.println(e);
        }
        System.out.println("Database loaded");
        System.out.println("Server is ready");
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
                smart_search(request3);
            } catch (Exception e) { // Handle exception
                System.out.println(e);
            }
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            res[0]+=timeElapsed;
            System.out.println("timer 1 :"+timeElapsed);
        }
        res[0]/=nTest;
        System.out.println("");
        System.out.println("-----------------------------------");
        System.out.println("");

        try {
            Thread.sleep(5);
        }
        catch(Exception e){
            System.out.println(e);
        }


        for(int i=0;i<nTest;i++) {
            try { // Load database
                start =System.currentTimeMillis();
                raw_search(request3);
            } catch (Exception e) { // Handle exception
                System.out.println(e);
            }
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            res[1]+=timeElapsed;
            System.out.println("timer 2 :"+timeElapsed);
        }
        res[1]/=nTest;
        System.out.println("");
        System.out.println("-----------------------------------");
        System.out.println("");

        System.out.println("meanTime 1:"+ res[0]);
        System.out.println("meanTime 2:"+ res[1]);



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

    public static void displayDb(int type[],String [] val) {
        for(int i=0;i<type.length;i++){
            System.out.println("type:"+type[i]+" val:"+val[i]);
        }
    }

    public static void smart_dbLoad(String fileName) throws IOException {
        BufferedReader br;
        int nLines = howManyLines(fileName);
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
    

    public static String smart_search(String request) {

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
                for (String request_type: request_types) {
                    if (data[0][i]==request_type && data[1][i].matches(regex)) {
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

    /******************  RAW  ******************/


    public static void raw_dbLoad(String fileName) throws IOException {
        BufferedReader br;
        int nLines = howManyLines(fileName);
        types = new String[nLines];
        sentences = new String[nLines];

        try{
            br = new BufferedReader(new FileReader(fileName));
            String line;
            String[] line_split;
            for (int i = 0; i < nLines; i++) {
                line = br.readLine();
                line_split = line.split("@@@", 2);
                types[i] = line_split[0];
                sentences[i] = line_split[1];
            }
            br.close();
        } catch(Exception e) { // Error while reading database
            System.err.println("Error while reading database: " + e);
            System.exit(-1);
        }

    }

    public static String raw_search(String request) {

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
            for (int i = 0; i < types.length; i++) {
                for (String request_type: request_types) {
                    if (types[i].equals(request_type) && sentences[i].matches(regex)) {
                        sb.append("|" + i + ": " + sentences[i] + "|");
                    }
                }
            }

            String output = sb.toString();
            if (output.length() == 0)
                return "No match found";
            else
                return output;
        }
        else {
            return "Usage: <types>;<regex>";
        }
    }



}


