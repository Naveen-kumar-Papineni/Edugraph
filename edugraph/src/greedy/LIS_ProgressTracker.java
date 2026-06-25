package greedy;

import core.Result;
import java.util.*;

// Longest Increasing Subsequence to track student score improvement
public class LIS_ProgressTracker {

    // O(n^2) DP: finds actual LIS with path reconstruction
    public List<Integer> lisWithPath(List<Integer> scores) {
        int n = scores.size();
        int[] dp = new int[n];
        int[] prev = new int[n];

        Arrays.fill(dp, 1);
        Arrays.fill(prev, -1);

        int maxLen = 1, maxIdx = 0;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (scores.get(j) < scores.get(i) && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    prev[i] = j;
                }
            }
            if (dp[i] > maxLen) { maxLen = dp[i]; maxIdx = i; }
        }

        // Reconstruct path
        List<Integer> lis = new ArrayList<>();
        for (int i = maxIdx; i >= 0; i = prev[i]) {
            lis.add(0, scores.get(i));
            if (prev[i] == -1) break;
        }
        return lis;
    }

    // O(n log n) LIS using patience sorting (binary search on tails array)
    public int lisLengthFast(List<Integer> scores) {
        List<Integer> tails = new ArrayList<>();

        for (int score : scores) {
            int lo = 0, hi = tails.size();
            while (lo < hi) {
                int mid = (lo + hi) / 2;
                if (tails.get(mid) < score) lo = mid + 1;
                else hi = mid;
            }
            if (lo == tails.size()) tails.add(score);
            else tails.set(lo, score);
        }
        return tails.size();
    }

    public void analyseStudent(String name, List<Result> results) {
        List<Integer> scores = new ArrayList<>();
        for (Result r : results) scores.add((int) r.getPercentage());

        List<Integer> lis = lisWithPath(scores);
        int fast = lisLengthFast(scores);

        System.out.println("  [LIS] " + name + " scores: " + scores);
        System.out.println("  [LIS] Longest improving streak: " + lis + " (length=" + lis.size() + ")");
        System.out.println("  [LIS] Fast LIS check: " + fast);

        if (lis.size() >= scores.size() * 0.6)
            System.out.println("  [LIS] Student shows CONSISTENT IMPROVEMENT");
        else
            System.out.println("  [LIS] Inconsistent performance - may need extra support.");
    }
}
