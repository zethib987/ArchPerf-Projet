import java.net.*;
import java.io.*;
import java.util.concurrent.Semaphore;

public class BasicServer {

    public static String FILEPATH = "C:\\Users\\bapti\\OneDrive\\Documents\\Education\\EPL\\Master\\Q9\\LINGI2241 - Architecture and performance of computer systems\\ArchPerf-Projet\\dbdata.txt";
    public static int portNumber = 4444;
    public static int NClientsSimultaneity = 100;
    public static boolean print = false;

    public static Semaphore semData;
    public static String [] types;
    public static String [] sentences;

    public static void main(String []args){

        System.out.println("Launching server");
        semData = new Semaphore(NClientsSimultaneity);

        System.out.println("Loading database");
        try{ // Load database
            raw_dbLoad(FILEPATH);
        } catch (Exception e){ // Handle exception
            System.out.println(e);
        }
        System.out.println("Database loaded");
        System.out.println("BasicServer is ready");

        boolean listening = true;
        // Launch a multi server thread for each client
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new BasicMultiServerThread(serverSocket.accept(), semData, types, sentences, print).start();
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

}


