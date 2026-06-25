package greedy;

import core.Course;
import java.util.*;

// 0/1 Knapsack (DP) and Fractional Knapsack (Greedy) for course selection
public class Knapsack_CourseSelector {

    // 0/1 Knapsack: select best courses within credit limit, each course taken once
    // dp[i][w] = max value using first i courses with <= w credits
    public List<String> zeroOneKnapsack(List<Course> courses, int creditLimit) {
        int n = courses.size();
        int[][] dp = new int[n + 1][creditLimit + 1];

        for (int i = 1; i <= n; i++) {
            Course c = courses.get(i - 1);
            int w = c.getCredits();
            int value = c.getDifficulty() * 5 + 50;

            for (int cap = 0; cap <= creditLimit; cap++) {
                dp[i][cap] = dp[i - 1][cap]; // don't include course i
                if (w <= cap) {
                    int withItem = value + dp[i - 1][cap - w];
                    if (withItem > dp[i][cap])
                        dp[i][cap] = withItem; // include course i
                }
            }
        }

        // Backtrack to find selected courses
        List<String> selected = new ArrayList<>();
        int cap = creditLimit;
        for (int i = n; i >= 1; i--) {
            if (dp[i][cap] != dp[i - 1][cap]) {
                Course c = courses.get(i - 1);
                selected.add(c.getCourseId());
                cap -= c.getCredits();
            }
        }

        System.out.println("  [0/1 Knapsack] Max value: " + dp[n][creditLimit]);
        return selected;
    }

    // Fractional Knapsack: fractions allowed - sort by value/credit ratio
    public double fractionalKnapsack(List<Course> courses, int creditLimit) {
        List<double[]> items = new ArrayList<>();
        for (Course c : courses) {
            double value = c.getDifficulty() * 5 + 50;
            items.add(new double[]{value, c.getCredits(), value / c.getCredits()});
        }

        items.sort((a, b) -> Double.compare(b[2], a[2])); // sort by ratio descending

        double totalValue = 0;
        int remaining = creditLimit;

        for (double[] item : items) {
            if (remaining <= 0) break;
            int weight = (int) item[1];
            if (weight <= remaining) {
                totalValue += item[0];
                remaining -= weight;
            } else {
                totalValue += item[0] * ((double) remaining / weight);
                remaining = 0;
            }
        }

        System.out.printf("  [Fractional Knapsack] Max value: %.2f%n", totalValue);
        return totalValue;
    }
}
