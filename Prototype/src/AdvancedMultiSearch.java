import java.util.concurrent.Semaphore;

public class AdvancedMultiSearch extends Thread{


    private int start,end;
    private String regex;
    int indice=0;
    String []output;


    public AdvancedMultiSearch(){
        super("MultiSearch");
    }
    public AdvancedMultiSearch(String regex, int start, int end,String [] output, int indice){
        super("MultiSearch");
        this.start=start;
        this.end=end;
        this.regex=regex;
        this.output=output;
        this.indice=indice;
        output[indice]="";
    }
    public void run(){
      searchThread();
    }

    public void searchThread() {
        StringBuffer sb = new StringBuffer();
        try{

        for (int i = start; i <= end; i++){
            if (AdvancedServer.data[1][i].matches(regex)) {
                sb.append(AdvancedServer.data[1][i] + "@@@");
            }
        }}
        catch (Exception e){e.printStackTrace();}

        output[indice]=sb.toString();

    }


}
