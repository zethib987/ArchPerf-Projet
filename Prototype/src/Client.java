import java.net.*;
import java.io.*;

public class Client {
    private static Socket serverSocket;

    public static void main(String []args){
       // String hostName = args[0];
        String hostName = "localhost";
        int portNumber = Integer.parseInt(args[0]);
       //int  portNumber = 4444;
        System.out.println("Lancement client..");
        String simpleRequest = "1,4;second";
        String noMatchRequest1 = "4,3;story";
        String noMatchRequest2 = "1,2,3;yolo"; // not tested
        String normalRequest = "0,2;rami...";


        try {
            serverSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            //System.out.println("Client connected addr: "+hostName+ "  on port:"+portNumber);

            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                out.println(normalRequest);
                fromServer = in.readLine();
                System.out.println("Server: "+fromServer);
                break;

            }
            Thread.sleep(2000);
            serverSocket.close();

        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
