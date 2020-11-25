import javax.management.StringValueExp;
import java.lang.reflect.Array;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    static int portNumber= 4444;
    static int [] type;
    static String [] val;

    public static void main(String []args){
        System.out.println("Lancement du serveur..");
        try{
            dbLoad("C:\\Users\\Thib\\Documents\\unif\\2020-2021\\Q1\\Algo Struct\\TestServer\\src\\input.txt");
            //displayDb(type,val);
        }
        catch (Exception e){
            System.out.println(e);
        }
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new MultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
    public static String search(String request){
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

    public static void dbLoad(String fileName) throws IOException {
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
            System.out.println(e);
        }
        return nLines;
    }

    public static void displayDb(int type[],String [] val){
        for(int i=0;i<type.length;i++){
            System.out.println("type:"+type[i]+" val:"+val[i]);
        }
    }
}


