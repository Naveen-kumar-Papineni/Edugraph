package greedy;

// Fenwick Tree (Binary Indexed Tree) for prefix sum queries on scores
// Point update and prefix sum in O(log n) using bit manipulation
public class FenwickTree_Scores {

    private final int[] tree;
    private final int n;

    public FenwickTree_Scores(int n) {
        this.n = n;
        this.tree = new int[n + 1]; // 1-indexed
    }

    // Add delta to position i (1-indexed)
    // Walk up using i += (i & -i) which isolates the lowest set bit
    public void update(int i, int delta) {
        for (; i <= n; i += (i & -i))
            tree[i] += delta;
    }

    // Sum from index 1 to i (inclusive)
    // Walk down using i -= (i & -i)
    public int prefixSum(int i) {
        int sum = 0;
        for (; i > 0; i -= (i & -i))
            sum += tree[i];
        return sum;
    }

    // Sum from l to r: prefix(r) - prefix(l-1)
    public int rangeSum(int l, int r) {
        if (l > r) return 0;
        return prefixSum(r) - (l > 1 ? prefixSum(l - 1) : 0);
    }

    public void build(int[] scores) {
        for (int i = 0; i < scores.length && i < n; i++)
            update(i + 1, scores[i]);
    }

    public void printTree() {
        System.out.print("  [Fenwick internal]: ");
        for (int i = 1; i <= n; i++) System.out.print(tree[i] + " ");
        System.out.println();
    }
}
