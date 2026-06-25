package sorting;

// Radix Sort (LSD) for integer roll numbers - non-comparison sort, O(d*n)
public class RadixSort_RollNumbers {

    public void sort(int[] rollNumbers) {
        if (rollNumbers.length == 0) return;
        int max = rollNumbers[0];
        for (int v : rollNumbers) if (v > max) max = v;

        // Process digit by digit from LSD to MSD
        for (int exp = 1; max / exp > 0; exp *= 10)
            countingSortByDigit(rollNumbers, exp);
    }

    // Stable counting sort on the digit at position 'exp'
    private void countingSortByDigit(int[] arr, int exp) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10]; // digits 0-9

        for (int val : arr)
            count[(val / exp) % 10]++;

        // Prefix sum to get positions
        for (int i = 1; i < 10; i++)
            count[i] += count[i - 1];

        // Build output (reverse for stability)
        for (int i = n - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % 10;
            output[--count[digit]] = arr[i];
        }

        System.arraycopy(output, 0, arr, 0, n);
    }
}
