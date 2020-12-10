import java.util.concurrent.Semaphore;

public class MultiSearch extends Thread{


    private int start,end;
    private String regex;
    private String [] request_types;

    static int NUMBERSEM= 1;
    static Semaphore semaphore = new Semaphore(NUMBERSEM);

    public MultiSearch(){
        super("MultiSearch");
    }
    public MultiSearch(String [] request_types, String regex, int start, int end){
        super("MultiSearch");
        this.start=start;
        this.end=end;
        this.regex=regex;
        this.request_types=request_types;
    }
    public void run(){
        //long start = System.currentTimeMillis();
        searchThread();
        //long stop = System.currentTimeMillis();
        //System.out.println("timer3:"+(stop-start));
    }

    public void searchThread() {
        StringBuffer sb = new StringBuffer();
        for (int i = start; i <= end; i++) {
            for (String request_type : request_types) {
                if (AdvancedServer.data[0][i].equals(request_type) && AdvancedServer.data[1][i].matches(regex)) {
                    sb.append(AdvancedServer.data[1][i] + "@@@");
                }
            }
        }
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AdvancedServer.output+=sb.toString();
        semaphore.release();
    }
}
