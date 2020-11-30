import java.net.*;
import java.io.*;

public class MultiServerThread extends Thread {
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
            ServerProtocol sp = new ServerProtocol();
            System.out.println("Server launched.");

            while ((inputLine = in.readLine()) != null) {
                outputLine = sp.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Fatal error"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}