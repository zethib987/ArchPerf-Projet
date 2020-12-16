import java.util.concurrent.Semaphore;

public class AdvancedMultiSearch extends Thread{


    private int start,end;
    private String regex;
    static String[] request_types= {"0", "1", "2", "3", "4", "5"};

    static int NUMBERSEM= 1; // nombre de semData, ici 1 car il est utilisé comme un mutex
    static Semaphore semaphore = new Semaphore(NUMBERSEM);

    public AdvancedMultiSearch(){
        super("MultiSearch");
    }
    public AdvancedMultiSearch(String regex, int start, int end){
        super("MultiSearch");
        this.start=start;
        this.end=end;
        this.regex=regex;
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
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AdvancedServer.output+=sb.toString();
        semaphore.release();
    }


}
