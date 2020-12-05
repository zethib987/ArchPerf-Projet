public class MultiSort extends Thread{

    int lo,hi;

    public MultiSort(int lo,int hi){
        super("MultiSort");
        this.lo=lo;
        this.hi=hi;
    }

    public void run(){
        QuickSort.sort(lo,hi);
    }
}
