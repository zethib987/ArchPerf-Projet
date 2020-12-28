import java.net.*;
import java.io.*;

/**
 * AdvancedMultiServerThread first creates the input and output stream to allow the communication with the client
 * When it is done, send a message of connection to the client and wait for the client to send requests.
 * When a request is received, the thread begins to organize the multi-threaded search by starting several
 * "AdvancedMultiSearch" threads. Each of them will compute a part of the search and concatenate it's result
 * to a global variable that will finally be sent to the client.
 * When it's done, the server starts waiting again for a request again.
 *
 * @author  Baptiste and Thibault
 * @version 1.0
 * @since   2020-12
 */
public class AdvancedMultiServerThread extends Thread{
    private Socket socket = null;// The new socket of the client

    public AdvancedMultiServerThread(Socket socket) {
        super("AdvancedMultiServerThread");
        this.socket = socket;
    }

    public void run() {

        try (
                // Create the input and output streams based on the socket of the client
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            // Approve the connection to the server by sending a connection message to the client
            outputLine = "Connected to server";
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {// while socket is busy
                AdvancedServer.sem.acquire(1); // wait for the semaphore that keep the smart4_search
                String s = smart4_search(inputLine, AdvancedServer.indexes, AdvancedServer.NTPT); // launch a smart4_search thread by request
                AdvancedServer.sem.release(1); // free the semaphore
                out.println(s);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    /**
     * Start multiple search threads (AdvancedMultiSearch) that will compute a research only on a section
     * of the database and then store their results to a string array.
     * Each entry of this array is concatenate to a result string that is returned at the end
     * when the whole array has been browsed.
     */
    public static String smart4_search(String request,int [] indexesTab, int NTPT) throws InterruptedException {
        int[] indexes = indexesTab.clone();
        Thread threads[];

        String output[];

        // Handle the request
        String[] split_request = request.split(";",2);
        if (split_request.length == 2) {// The request has a valid format
            String [] request_types;
            if (split_request[0].length() != 0) {
                request_types = split_request[0].split(",");
            } else { // If no <types> specified then all
                request_types = new String[] {"0", "1", "2", "3", "4", "5"};
            }
            String regex = split_request[1];

            threads = new Thread[request_types.length*NTPT];
            output = new String[request_types.length*NTPT];

            // creating and lauching all the threads
            for( int i=0;i<request_types.length;i++){
                int index = Integer.parseInt(request_types[i]);
                int D = indexes[index+1]-indexes[index]; // delta/distance
                D/= NTPT; // distance per threads
                indexes[index]-=1; // ugly trick to partition the limits of threads
                // starts NTPT search threads per type's section
                int j;
                for(j=0;j<NTPT-1;j++){
                    threads[(i*NTPT)+j] = new AdvancedMultiSearch(regex,(indexes[index]+j*D)+1,indexes[index]+(j+1)*D,output,(i*NTPT)+j);
                    threads[(i*NTPT)+j].start();
                }
                // the last one is a bit different
                threads[(i*NTPT)+j] = new AdvancedMultiSearch(regex,(indexes[index]+j*D)+1,indexes[index+1]-1,output,(i*NTPT)+j); // division pas tjrs entière du coup on prend le "reste"
                threads[(i*NTPT)+j].start();
                indexes[index]++; // correct the ugly trick's effect
            }

            // waiting for all the threads to finish
            for(int k=0;k< threads.length;k++){
                threads[k].join();
            }
            String res="";
            // concatenate each entry of output to the result string
            for(int i =0;i< output.length;i++){
                res+=output[i];
            }


            if (res.length() == 0)
                return "No match found";
            else
                return res; // return the result
        } else {
            return "Usage: <types>;<regex>";
        }


    }

}
