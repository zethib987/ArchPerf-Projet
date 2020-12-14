import java.net.*;
import java.io.*;
import java.util.concurrent.Semaphore;

public class AdvancedMultiServerThread extends Thread{
    private Socket socket = null;
    static int NUMBERSEM = 1;
    static Semaphore semaphore = new Semaphore(NUMBERSEM);
    static Semaphore semNCurrentCLients= new Semaphore(1);

    public AdvancedMultiServerThread(Socket socket) {
        super("AdvancedMultiServerThread");
        this.socket = socket;
    }

    public void run() {



        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            semNCurrentCLients.acquire();
            AdvancedServer.nCurrentClients++;
           // System.out.println("co :"+AdvancedServer.nCurrentClients);
            semNCurrentCLients.release();
            String inputLine, outputLine;
            outputLine = "Connected to server";
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                semaphore.acquire(); // wait for the sem to be available
               // System.out.println("Request: " + inputLine);
                out.println(AdvancedServer.smart4_search(inputLine));
                semaphore.release(); // free the sem
            }
            socket.close();
            semNCurrentCLients.acquire();
            AdvancedServer.nCurrentClients--;
            // System.out.println("deco :"+AdvancedServer.nCurrentClients);
            semNCurrentCLients.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
