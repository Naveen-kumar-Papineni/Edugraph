package greedy;

// Segment Tree for range queries (sum, min, max) on score arrays
// Each node stores aggregate over a sub-array; built in O(n), queried in O(log n)
public class SegmentTree_Analytics {

    private final int[] sumTree;
    private final int[] minTree;
    private final int[] maxTree;
    private final int n;

    public SegmentTree_Analytics(int[] scores) {
        this.n = scores.length;
        sumTree = new int[4 * n];
        minTree = new int[4 * n];
        maxTree = new int[4 * n];
        build(scores, 1, 0, n - 1);
    }

    private void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            sumTree[node] = arr[start];
            minTree[node] = arr[start];
            maxTree[node] = arr[start];
        } else {
            int mid = (start + end) / 2;
            build(arr, 2 * node, start, mid);
            build(arr, 2 * node + 1, mid + 1, end);
            sumTree[node] = sumTree[2 * node] + sumTree[2 * node + 1];
            minTree[node] = Math.min(minTree[2 * node], minTree[2 * node + 1]);
            maxTree[node] = Math.max(maxTree[2 * node], maxTree[2 * node + 1]);
        }
    }

    public int rangeSum(int l, int r) { return querySum(1, 0, n - 1, l, r); }
    public int rangeMin(int l, int r) { return queryMin(1, 0, n - 1, l, r); }
    public int rangeMax(int l, int r) { return queryMax(1, 0, n - 1, l, r); }
    public double rangeAverage(int l, int r) { return (r >= l) ? (double) rangeSum(l, r) / (r - l + 1) : 0; }

    private int querySum(int node, int start, int end, int l, int r) {
        if (r < start || end < l) return 0;
        if (l <= start && end <= r) return sumTree[node];
        int mid = (start + end) / 2;
        return querySum(2 * node, start, mid, l, r) + querySum(2 * node + 1, mid + 1, end, l, r);
    }

    private int queryMin(int node, int start, int end, int l, int r) {
        if (r < start || end < l) return Integer.MAX_VALUE;
        if (l <= start && end <= r) return minTree[node];
        int mid = (start + end) / 2;
        return Math.min(queryMin(2 * node, start, mid, l, r), queryMin(2 * node + 1, mid + 1, end, l, r));
    }

    private int queryMax(int node, int start, int end, int l, int r) {
        if (r < start || end < l) return Integer.MIN_VALUE;
        if (l <= start && end <= r) return maxTree[node];
        int mid = (start + end) / 2;
        return Math.max(queryMax(2 * node, start, mid, l, r), queryMax(2 * node + 1, mid + 1, end, l, r));
    }
}
