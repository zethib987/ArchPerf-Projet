import java.util.concurrent.Semaphore;

public class MultiSearchSmart extends Thread{


    private int start,end;
    private String regex;
    static String[] request_types= {"0", "1", "2", "3", "4", "5"};

    static int NUMBERSEM= 1;
    static Semaphore semaphore = new Semaphore(NUMBERSEM);

    public MultiSearchSmart(){
        super("MultiSearch");
    }
    public MultiSearchSmart( String regex, int start, int end){
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
        for (int i = start; i <= end; i++) {
                if (SmartServer.data[1][i].matches(regex)) {
                    sb.append("|" + i + ": " + SmartServer.data[1][i] + "|");
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
