/**
 * Peform a special Quicksort, adapted  to our 2 columns array. Useful to improve our search algorithm
 * Heavly inspired of the quicksort from "Algorithm, fourth edition" by Robert Sedgewick and Kevin Wayne
 *
 * @author  Baptiste and Thibault
 * @version 1.0
 * @since   2020-12
 */
public class QuickSort {

    static int n=20;

    public static void quickSort(){
        sort(0, AdvancedServer.data[0].length-1);

    }
    public static void sort(int lo, int hi) {
        if (hi <= lo) return;
        int j = partition(lo, hi);

        if (n > 0) {
            new MultiSort(lo, j - 1).start();
            n -= 1;
        } else {
            sort(lo, j - 1);
        }
        sort(j + 1, hi);
    }

        public static int partition ( int lo, int hi){
            int i = lo, j = hi + 1;
            int v = Integer.parseInt(AdvancedServer.data[0][lo]);
            while (true) {
                while (Integer.parseInt(AdvancedServer.data[0][++i]) < v) if (i == hi) break;
                while (v < Integer.parseInt(AdvancedServer.data[0][--j])) if (j == lo) break;
                if (i >= j) break;
                exch(i, j);
            }
            exch(lo, j);
            return j;
        }

        public static void exch ( int i, int j){
            String temp[] = {AdvancedServer.data[0][j], AdvancedServer.data[1][j]};
            AdvancedServer.data[0][j] = AdvancedServer.data[0][i];
            AdvancedServer.data[1][j] = AdvancedServer.data[1][i];
            AdvancedServer.data[0][i] = temp[0];
            AdvancedServer.data[1][i] = temp[1];

        }

}
