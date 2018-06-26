package assignment3;

import assignment3.utils.LineSegment;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {

    private final ArrayList<LineSegment> lineSegments = new ArrayList<>();

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        validate(points);
        final int numOfPoints = points.length;

        // iterate through every four elements
        for (int i = 0; i < numOfPoints - 3; i++) {
            for (int j = i + 1; j < numOfPoints - 2; j++) {
                for (int k = j + 1; k < numOfPoints - 1; k++) {
                    for (int l = k + 1; l < numOfPoints; l++) {
                        // the fourth points are collinear
                        if (points[i].slopeTo(points[j]) == points[i].slopeTo(points[k])
                                && points[i].slopeTo(points[k]) == points[i].slopeTo(points[l])) {
                            Point[] segment = {points[i], points[j], points[k], points[l]};
                            Arrays.sort(segment);
                            lineSegments.add(new LineSegment(segment[0], segment[3]));
                        }
                    }
                }
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineSegments.size();
    }

    // the line segments
    public LineSegment[] segments() {
        return lineSegments.toArray(new LineSegment[numberOfSegments()]);
    }

    private static void validate(Point[] points) {
        if (points == null) { throw new IllegalArgumentException(); }
        final int numOfPoints = points.length;

        for (int i = 0; i < numOfPoints; i++) {
            if (points[i] == null) { throw new IllegalArgumentException(); }
        }

        ArrayList<Point> ps = new ArrayList<>(Arrays.asList(points));
        for (int i = 0; i < numOfPoints; i++) {
            if (ps.indexOf(ps.get(i)) != i) { throw new IllegalArgumentException(); }
        }
    }

    public static void main(String[] args) {
        // read the numOfSegments points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
