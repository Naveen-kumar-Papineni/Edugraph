package sorting;

import core.Course;
import java.util.List;

// QuickSort: sort courses by popularity descending - O(n log n) average
public class QuickSort_CourseRank {

    public void sort(List<Course> courses) {
        quickSort(courses, 0, courses.size() - 1);
    }

    private void quickSort(List<Course> arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    // Lomuto partition scheme - pivot = last element
    private int partition(List<Course> arr, int low, int high) {
        int pivot = arr.get(high).getPopularity();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr.get(j).getPopularity() >= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(List<Course> arr, int i, int j) {
        Course tmp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, tmp);
    }
}
