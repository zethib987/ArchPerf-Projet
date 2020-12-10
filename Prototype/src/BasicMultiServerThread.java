import java.net.*;
import java.io.*;
import java.util.concurrent.Semaphore;

public class BasicMultiServerThread extends Thread{
    public final Socket socket;
    public static Semaphore semaphore;
    public static String[] types;
    public static String[] sentences;
    public static boolean print;

    public BasicMultiServerThread(Socket socket, Semaphore semaphore, String [] types, String [] sentences, boolean print) {
        super("BasicMultiServerThread");
        this.socket = socket;
        this.semaphore = semaphore;
        this.types = types;
        this.sentences = sentences;
        this.print = print;
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String inputLine, outputLine;
            outputLine = "Connected to server";
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                semaphore.acquire(1); // wait for the sem to be available
                if (print) {
                    System.out.println("Request: " + inputLine);
                }
                out.println(raw_search(inputLine)); // Basic server
                semaphore.release(1); // free the sem
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }


    public static String raw_search(String request) {

        String[] split_request = request.split(";",2);
        if (split_request.length == 2) {
            String[] request_types;
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
                        sb.append(sentences[i] + "@@@");
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
