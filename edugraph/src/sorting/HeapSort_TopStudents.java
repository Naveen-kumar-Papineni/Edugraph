package sorting;

import core.Student;
import java.util.*;

// HeapSort + Min-Heap Top-K selection
public class HeapSort_TopStudents {

    // In-place heap sort on array (ascending GPA)
    public void heapSort(Student[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, n, i);
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);
            heapify(arr, i, 0);
        }
    }

    private void heapify(Student[] arr, int n, int root) {
        int largest = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < n && arr[left].getGpa() > arr[largest].getGpa())  largest = left;
        if (right < n && arr[right].getGpa() > arr[largest].getGpa()) largest = right;

        if (largest != root) {
            swap(arr, root, largest);
            heapify(arr, n, largest);
        }
    }

    private void swap(Student[] arr, int i, int j) {
        Student tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }

    // Find top K students using a min-heap of size K: O(n log K)
    public List<Student> topK(List<Student> students, int k) {
        PriorityQueue<Student> minHeap =
            new PriorityQueue<>(k, Comparator.comparingDouble(Student::getGpa));

        for (Student s : students) {
            if (minHeap.size() < k) {
                minHeap.offer(s);
            } else if (s.getGpa() > minHeap.peek().getGpa()) {
                minHeap.poll();
                minHeap.offer(s);
            }
        }

        List<Student> result = new ArrayList<>(minHeap);
        result.sort((a, b) -> Double.compare(b.getGpa(), a.getGpa()));
        return result;
    }
}
