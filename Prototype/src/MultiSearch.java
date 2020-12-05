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
       searchThread();
    }

    public void searchThread() {
        StringBuffer sb = new StringBuffer();
        for (int i = start; i <= end; i++) {
            for (String request_type : request_types) {
                if (SmartServer.data[0][i] == request_type && SmartServer.data[1][i].matches(regex)) {
                    sb.append("|" + i + ": " + SmartServer.data[1][i] + "|");
                }
            }
        }
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SmartServer.output+=sb.toString();
        semaphore.release();
    }
}
