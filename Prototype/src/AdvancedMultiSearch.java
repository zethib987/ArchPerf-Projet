

/**
 * AdvancedMultiSearch is used to perform a sequential search on a database's section.
 * It stores its result in an output array whose content will be return to the client.
 *
 * @author  Baptiste and Thibault
 * @version 1.0
 * @since   2020-12
 */
public class AdvancedMultiSearch extends Thread{


    private int start,end;
    private String regex;
    int indice=0;
    String []output;


    public AdvancedMultiSearch(String regex, int start, int end,String [] output, int indice){
        // initialize the instance's variables
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
        StringBuffer sb = new StringBuffer(); // creates a string buffer
        try{

        for (int i = start; i <= end; i++){
            if (AdvancedServer.data[1][i].matches(regex)) {
                sb.append(AdvancedServer.data[1][i] + "@@@"); // concatenate each match to the string buffer
            }
        }}
        catch (Exception e){e.printStackTrace();}

        output[indice]=sb.toString(); // write the final result to the right output's entry

    }


}
