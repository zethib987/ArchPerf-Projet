public class QuickSort {

    static int n=20;

    public static void quickSort(){
        sort(0,SmartServer.data[0].length-1);


    }
    public static void sort(int lo, int hi) {
        if (hi <= lo) return;
        int j = partition(lo, hi);
        //System.out.println(j);

        if (n > 0) {
            //System.out.println("N: "+n);
            new MultiSort(lo, j - 1).start();
            n -= 1;
        } else {
            sort(lo, j - 1);
        }
        sort(j + 1, hi);
    }

        public static int partition ( int lo, int hi){
            int i = lo, j = hi + 1;
            int v = Integer.parseInt(SmartServer.data[0][lo]);
            while (true) {
                while (Integer.parseInt(SmartServer.data[0][++i]) < v) if (i == hi) break;
                while (v < Integer.parseInt(SmartServer.data[0][--j])) if (j == lo) break;
                if (i >= j) break;
                exch(i, j);
            }
            exch(lo, j);
            return j;
        }

        public static void exch ( int i, int j){
            String temp[] = {SmartServer.data[0][j], SmartServer.data[1][j]};
            SmartServer.data[0][j] = SmartServer.data[0][i];
            SmartServer.data[1][j] = SmartServer.data[1][i];
            SmartServer.data[0][i] = temp[0];
            SmartServer.data[1][i] = temp[1];

        }

}
