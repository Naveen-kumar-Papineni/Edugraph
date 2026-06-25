package trees;

import core.Course;
import core.Student;

// BST indexed by courseId (String)
public class BST_CourseIndex {

    private static class Node {
        Course course;
        Node left, right;
        Node(Course c) { this.course = c; }
    }

    private Node root;

    public void insert(Course course) {
        root = insertRec(root, course);
    }

    private Node insertRec(Node node, Course course) {
        if (node == null) return new Node(course);
        int cmp = course.getCourseId().compareTo(node.course.getCourseId());
        if (cmp < 0) node.left  = insertRec(node.left, course);
        else if (cmp > 0) node.right = insertRec(node.right, course);
        return node;
    }

    public Course search(String courseId) {
        Node n = root;
        while (n != null) {
            int cmp = courseId.compareTo(n.course.getCourseId());
            if (cmp == 0) return n.course;
            n = (cmp < 0) ? n.left : n.right;
        }
        return null;
    }

    public void delete(String courseId) {
        root = deleteRec(root, courseId);
    }

    private Node deleteRec(Node node, String courseId) {
        if (node == null) return null;
        int cmp = courseId.compareTo(node.course.getCourseId());
        if (cmp < 0) node.left = deleteRec(node.left, courseId);
        else if (cmp > 0) node.right = deleteRec(node.right, courseId);
        else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            // Two children: replace with in-order successor
            Node successor = node.right;
            while (successor.left != null) successor = successor.left;
            node.course = successor.course;
            node.right = deleteRec(node.right, successor.course.getCourseId());
        }
        return node;
    }

    public void inorder() {
        System.out.println("  [BST In-order Course List]");
        inorderRec(root);
        System.out.println();
    }

    private void inorderRec(Node node) {
        if (node == null) return;
        inorderRec(node.left);
        System.out.print("    " + node.course.getCourseId() + "(" + node.course.getName() + ")  ");
        inorderRec(node.right);
    }
}
