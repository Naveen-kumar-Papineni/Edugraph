package sorting;

import core.Course;
import core.Student;
import java.util.*;

// MergeSort: sort students by GPA (descending) - stable, O(n log n)
public class MergeSort_StudentRank {

    public List<Student> sort(List<Student> students) {
        if (students.size() <= 1) return new ArrayList<>(students);
        List<Student> arr = new ArrayList<>(students);
        mergeSort(arr, 0, arr.size() - 1);
        return arr;
    }

    private void mergeSort(List<Student> arr, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private void merge(List<Student> arr, int left, int mid, int right) {
        List<Student> L = new ArrayList<>(arr.subList(left, mid + 1));
        List<Student> R = new ArrayList<>(arr.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;
        while (i < L.size() && j < R.size()) {
            if (L.get(i).getGpa() >= R.get(j).getGpa()) arr.set(k++, L.get(i++));
            else arr.set(k++, R.get(j++));
        }
        while (i < L.size())  arr.set(k++, L.get(i++));
        while (j < R.size())  arr.set(k++, R.get(j++));
    }
}
