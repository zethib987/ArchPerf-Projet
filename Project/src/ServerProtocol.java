import java.net.*;
import java.io.*;

public class ServerProtocol {

    public String[] data;
    public int foo;

    public ServerProtocol() {

        try {
            File file = new File("C:\\Users\\bapti\\OneDrive\\Documents\\Education\\EPL\\Master\\Q9\\LINGI2241 - Architecture and performance of computer systems\\Project\\dbdata.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            data = new String[2442236];
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                data[i] = line;
                i++;
            }
            this.foo = 0;
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public String processInput(String theInput) {
        if (theInput != null) {
            String[] input = theInput.split(";",2);
            if (input.length == 2) {
                for(int i = 0; i < data.length; i++) {
                    String[] line = data[i].split("@@@", 2);
                    if (input[0].equals(line[0]) && line[1].matches(input[1])) {
                        return line[1];
                    }
                }
                return "No match found for: " + theInput;
            } else {
                return "Error. Please use: <types>;<regex>";
            }
        } else {
            return null;
        }
    }

}