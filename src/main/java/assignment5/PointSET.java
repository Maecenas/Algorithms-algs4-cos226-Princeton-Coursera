package assignment5;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;

import java.util.TreeSet;

public class PointSET {

    private final TreeSet<Point2D> pointSet;

    /**
     *  Construct an empty set of points
     */
    public PointSET() {
        pointSet = new TreeSet<>();
    }

    /**
     * Return true if this set is empty
     *
     * @return True if this set is empty
     */
    public boolean isEmpty() {
        return pointSet.isEmpty();
    }

    /**
     * Return the number of points in the set
     *
     * @return Number of points in the set
     */
    public int size() {
        return pointSet.size();
    }

    /**
     * Add the point to the set (if it is not already in the set)
     *
     * @param p Point2D to be added to the PointSET
     * @throws IllegalArgumentException if the specified argument is null
     */
    public void insert(Point2D p) {
        validate(p);
        pointSet.add(p);
    }

    /**
     * Return the set contain point p or not
     *
     * @param p the point to find contain or not in the set
     * @return the set contain point p or not
     * @throws IllegalArgumentException if the specified argument is null
     */
    public boolean contains(Point2D p) {
        validate(p);
        return pointSet.contains(p);
    }

    /**
     * Draw all the points to standard draw
     */
    public void draw() {
        for (Point2D p : pointSet) {
            p.draw();
        }
    }

    /**
     * Return all the points that are inside the rectangle (or on the boundary)
     *
     * @param rect The horizontal-vertical aligned rectangle indicating search range
     * @return All the points that are inside the rectangle (or on the boundary)
     * @throws IllegalArgumentException if the specified argument is null
     */
    public Iterable<Point2D> range(RectHV rect) {
        validate(rect);
        TreeSet<Point2D> rectRange = new TreeSet<>();
        for (Point2D p : pointSet) {
            if (rect.contains(p)) rectRange.add(p);
        }
        return rectRange;
    }

    /**
     * Return a nearest neighbor in the set to point p; null if the set is empty
     *
     * @param p Point nearest neighbor is to found
     * @return a nearest neighbor in the set to point p; null if the set is empty
     * @throws IllegalArgumentException if the specified argument is null
     */
    public Point2D nearest(Point2D p) {
        validate(p);
        double minDistance = Double.POSITIVE_INFINITY;
        Point2D nearestPoint = null;
        for (Point2D q : pointSet) {
            double tmpDistance = q.distanceSquaredTo(p);
            if (tmpDistance < minDistance) {
                nearestPoint = q;
                minDistance = tmpDistance;
            }
        }
        return nearestPoint;
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
     * Unit tests the PointSET data type.
     *
     * @param args the command-line arguments
     */

    public static void main(String[] args) {
        PointSET ps = new PointSET();
        StdOut.println(ps.nearest(new Point2D(0, 0)) == null);
    }
}

