package assignment5;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {

    private Node root;
    private int n;

    private static class Node {

        private final Point2D p;          // the point
        private final RectHV rect;        // the axis-aligned rectangle corresponding to this node
        private final boolean VERTICAL;   // at VERTICAL level
        private Node lb, rt;              // the lest/bottom, right/top subtree

        Node(Point2D p, RectHV rect, boolean VERTICAL) {
            this.p = p;
            this.rect = rect;
            this.VERTICAL = VERTICAL;
        }

        int compareTo(Point2D that) {
            validate(that);
            if (this.p.equals(that)) return 0;
            else if (VERTICAL) return this.p.x() > that.x() ? 1 : -1;
            else               return this.p.y() > that.y() ? 1 : -1;
        }
    }

    /**
     * Construct an empty tree of points
     */
    public KdTree() {
    }

    /**
     * Return true if this tree is empty
     *
     * @return True if this tree is empty
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Return the number of points in the tree
     *
     * @return Number of points in the tree
     */
    public int size() {
        return n;
    }

    /**
     * Add the point to the tree (if it is not already in the tree)
     *
     * @param p Point2D to be added to the PointSET
     * @throws IllegalArgumentException if the specified argument is null
     */
    public void insert(Point2D p) {
        validate(p);
        root = insert(p, root, null, 0);
    }

    private Node insert(Point2D p, Node node, Node parent, int direction) {
        // add new node
        if (node == null) {
            if (n++ == 0) return new Node(p, new RectHV(0, 0, 1, 1), true);
            RectHV rect;
            if (parent.VERTICAL) {
                if (direction > 0) {
                    rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.p.x(), parent.rect.ymax());
                } else  {
                    rect = new RectHV(parent.p.x(), parent.rect.ymin(), parent.rect.xmax(), parent.rect.ymax());
                }
            } else {
                if (direction > 0) {
                    rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.rect.xmax(), parent.p.y());
                } else {
                    rect = new RectHV(parent.rect.xmin(), parent.p.y(), parent.rect.xmax(), parent.rect.ymax());
                }
            }
            return new Node(p, rect, !parent.VERTICAL);
        } else {
            if (node.p.equals(p)) return node;
            int cmp = node.compareTo(p);
            if (cmp > 0) node.lb = insert(p, node.lb, node, cmp);
            else         node.rt = insert(p, node.rt, node, cmp);
            return node;
        }
    }

    /**
     * Return the tree contain point p or not
     *
     * @param p the point to find contain or not in the tree
     * @return the tree contain point p or not
     * @throws IllegalArgumentException if the specified argument is null
     */
    public boolean contains(Point2D p) {
        validate(p);
        return contains(p, root);
    }

    private static boolean contains(Point2D p, Node node) {
        if (node == null) return false;
        if (node.p.equals(p)) return true;
        int cmp = node.compareTo(p);
        if (cmp > 0) return contains(p, node.lb);
        else return contains(p, node.rt);
    }

    /**
     * Draw all the points to standard draw
     */
    public void draw() {
        draw(root);
    }

    private static void draw(Node node) {
        if (node == null) return;
        draw(node.lb);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        StdDraw.setPenRadius();
        if (node.VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
        }
        draw(node.rt);
    }

    /**
     * Return all the points that are inside the rectangle (or on the boundary)
     *
     * @param rect The horizontal-vertical aligned rectangle indicating search range
     * @return All the points that are inside the rectangle (or on the boundary)
     */
    public Iterable<Point2D> range(RectHV rect) {
        validate(rect);
        Queue<Point2D> pointQueue = new Queue<>();
        range(rect, root, pointQueue);
        return pointQueue;
    }

    private static void range(RectHV rect, Node x, Queue<Point2D> pointQueue) {
        if (x == null) return;
        if (rect.contains(x.p)) pointQueue.enqueue(x.p);
        if (x.lb != null && rect.intersects(x.lb.rect)) range(rect, x.lb, pointQueue);
        if (x.rt != null && rect.intersects(x.rt.rect)) range(rect, x.rt, pointQueue);
    }

    /**
     * Return a nearest neighbor in the tree to point p; null if the tree is empty
     *
     * @param p Point nearest neighbor is to found
     * @return a nearest neighbor in the tree to point p; null if the tree is empty
     */
    public Point2D nearest(Point2D p) {
        validate(p);
        if (isEmpty()) return null;
        return nearest(root, root.p, p);
    }

    private Point2D nearest(Node node, Point2D nearest, Point2D p) {
        if (node != null) {
            if (p.distanceSquaredTo(node.p) < p.distanceSquaredTo(nearest)) nearest = node.p;
            int cmp = node.compareTo(p);
            if (cmp > 0) {
                nearest = nearest(node.lb, nearest, p);
                if (node.rt != null && nearest.distanceSquaredTo(p) > node.rt.rect.distanceSquaredTo(p)) {
                    nearest = nearest(node.rt, nearest, p);
                }
            } else if (cmp < 0) {
                nearest = nearest(node.rt, nearest, p);
                if (node.lb != null && nearest.distanceSquaredTo(p) > node.lb.rect.distanceSquaredTo(p)) {
                    nearest = nearest(node.lb, nearest, p);
                }
            }
        }
        return nearest;
    }

    /**
     * Check whether argument object is legal or not
     *
     * @param obj an object
     * @throws IllegalArgumentException if the specified argument is null
     */
    private static void validate(Object obj) {
        if (obj == null) throw new IllegalArgumentException();
    }

    /**
     * Unit tests the KdTree data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        PointSET ps = new PointSET();
        StdOut.println(ps.nearest(new Point2D(0, 0)) == null);
    }
}
