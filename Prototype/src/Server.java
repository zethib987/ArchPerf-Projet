import javax.management.StringValueExp;
import java.lang.reflect.Array;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    static String FILEPATH = "C:\\Users\\bapti\\OneDrive\\Documents\\Education\\EPL\\Master\\Q9\\LINGI2241 - Architecture and performance of computer systems\\ArchPerf-Projet\\dbdata.txt";
    static int portNumber = 4444;

    static String [] types;
    static String [] sentences;

    public static void main(String []args){

        System.out.println("Launching server");
        System.out.println("Loading database");
        try{ // Load database
            raw_dbLoad(FILEPATH);
        } catch (Exception e){ // Handle exception
            System.out.println(e);
        }
        System.out.println("Database loaded");
        System.out.println("Server is ready");

        boolean listening = true;
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
            String[] request_types;
            if (split_request[0].length() != 0){
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
        } else {
            return "Usage: <types>;<regex>";
        }

    }
/*
    public static String advanced_search(String request) {

        boolean loadingTypes=true;
        ArrayList<Integer> types= new ArrayList<>();
        String s="";
        String res="";
        for(int i=0;i<request.length();i++){
            char c=request.charAt(i);
            if(c==';'){
                types.add(Integer.parseInt(s));
                loadingTypes=false;
                s="";
            }
            else if(loadingTypes){ // chargement des types
                if(c!=','){
                    s+=c;
                }
                else{
                    types.add(Integer.parseInt(s));
                    s="";
                }
            }
            else{ // chargement de la regex
                s+=c;
            }
        }
        Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE); // on stock la regex dans pattern
        Matcher matcher;
        for(int i=0;i<type.length;i++){
            if(types.contains(type[i])){ // si bon type on check la regex
                if( pattern.matcher(val[i]).find()){ // regex match
                    System.out.println("Match");
                    return val[i];
                }
            }
        }
        return "no match";
    }

    public static void advanced_dbLoad(String fileName) throws IOException {
        BufferedReader br;
        int nLines=howManyLines(fileName);
        type = new int[nLines];
        val = new String[nLines];

        try{
            br = new BufferedReader(new FileReader(fileName));
            String line;
            for(int i=0;i<nLines;i++) {
                line = br.readLine();
                type[i]=Integer.parseInt(String.valueOf(line.charAt(0))); // parsing dégeulasse
                String s="";
                for(int j=4;j<line.length();j++){
                    s+=line.charAt(j);
                }
                val[i]=s;
            }
            br.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void displayDb(int type[],String [] val) {
        for(int i=0;i<type.length;i++){
            System.out.println("type:"+type[i]+" val:"+val[i]);
        }
    }
 */
}


