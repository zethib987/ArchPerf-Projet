import java.net.*;
import java.io.*;
import java.util.Timer;
import java.util.concurrent.Semaphore;

public class AdvancedMultiServerThread extends Thread{
    private Socket socket = null;



    public AdvancedMultiServerThread(Socket socket) {
        super("AdvancedMultiServerThread");
        this.socket = socket;
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            outputLine = "Connected to server";
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                String s = smart4_search(inputLine, AdvancedServer.indexes,4);
                //semaphore.acquire(); // wait for the sem to be available
                //System.out.println("Request: " + inputLine);
                out.println(s);
                //semaphore.release(); // free the sem
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public static String smart4_search(String request,int [] indexesTab, int NTPT) throws InterruptedException {
        int[] indexes = indexesTab.clone();
        Thread threads[];

        String output[];

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
            output = new String[request_types.length*NTPT];

            // creating and lauching all the threads
            for( int i=0;i<request_types.length;i++){
                int index = Integer.parseInt(request_types[i]);
                int D = indexes[index+1]-indexes[index]; // delta/distance
                D/= NTPT; // distance per threads
                indexes[index]-=1; // ugly trick to partition the limits of threads
                int j;
                for(j=0;j<NTPT-1;j++){ // on laisse le dernier pour après car cas particulier
                    threads[(i*NTPT)+j] = new AdvancedMultiSearch(regex,(indexes[index]+j*D)+1,indexes[index]+(j+1)*D,output,(i*NTPT)+j);
                    threads[(i*NTPT)+j].start();
                }
                threads[(i*NTPT)+j] = new AdvancedMultiSearch(regex,(indexes[index]+j*D)+1,indexes[index+1]-1,output,(i*NTPT)+j); // division pas tjrs entière du coup on prend le "reste"
                threads[(i*NTPT)+j].start();
                indexes[index]++; // correct the ugly trick's effect
            }

            // waiting for all the threads to finish
            for(int k=0;k< threads.length;k++){
                threads[k].join();
            }
            String res="";
            for(int i =0;i< output.length;i++){
                res+=output[i];
            }

            if (res.length() == 0)
                return "No match found";
            else
                return res;
        } else {
            return "Usage: <types>;<regex>";
        }


    }

}
