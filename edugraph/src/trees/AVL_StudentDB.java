package trees;

import core.Student;

// Self-balancing AVL Tree keyed on rollNumber
public class AVL_StudentDB {

    private static class Node {
        Student student;
        int height;
        Node left, right;
        Node(Student s) { student = s; height = 1; }
    }

    private Node root;

    private int height(Node n)          { return (n == null) ? 0 : n.height; }
    private int balanceFactor(Node n)   { return (n == null) ? 0 : height(n.left) - height(n.right); }
    private void updateHeight(Node n)   { n.height = 1 + Math.max(height(n.left), height(n.right)); }

    private Node rotateRight(Node y) {
        Node x = y.left;
        y.left = x.right;
        x.right = y;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        y.left = x;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    private Node balance(Node n) {
        updateHeight(n);
        int bf = balanceFactor(n);

        if (bf > 1 && balanceFactor(n.left) >= 0)  return rotateRight(n);           // LL
        if (bf > 1 && balanceFactor(n.left) < 0)   { n.left = rotateLeft(n.left);   return rotateRight(n); } // LR
        if (bf < -1 && balanceFactor(n.right) <= 0) return rotateLeft(n);            // RR
        if (bf < -1 && balanceFactor(n.right) > 0) { n.right = rotateRight(n.right); return rotateLeft(n); } // RL

        return n;
    }

    public void insert(Student student) {
        root = insertRec(root, student);
    }

    private Node insertRec(Node node, Student student) {
        if (node == null) return new Node(student);
        int roll = student.getRollNumber();
        if (roll < node.student.getRollNumber())       node.left  = insertRec(node.left, student);
        else if (roll > node.student.getRollNumber())  node.right = insertRec(node.right, student);
        else return node; // duplicate
        return balance(node);
    }

    public Student search(int rollNumber) {
        Node n = root;
        while (n != null) {
            if (rollNumber < n.student.getRollNumber())      n = n.left;
            else if (rollNumber > n.student.getRollNumber()) n = n.right;
            else return n.student;
        }
        return null;
    }

    public void inorder() {
        System.out.println("  [AVL In-order Student List by Roll Number]");
        inorderRec(root);
        System.out.println();
    }

    private void inorderRec(Node node) {
        if (node == null) return;
        inorderRec(node.left);
        System.out.print("    " + node.student + "  ");
        inorderRec(node.right);
    }
}
