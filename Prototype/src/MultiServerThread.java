import java.net.*;
import java.io.*;

public class MultiServerThread extends Thread{
    private Socket socket = null;

    public MultiServerThread(Socket socket) {
        super("MultiServerThread");
        this.socket = socket;
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            outputLine = "Hello there";
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Client:"+inputLine);
                out.println(Server.search(inputLine));
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
