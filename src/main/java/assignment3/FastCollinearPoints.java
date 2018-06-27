package assignment3;

import assignment3.utils.LineSegment;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {

    private final ArrayList<LineSegment> lineSegments = new ArrayList<>();

    public FastCollinearPoints(Point[] points) {
        validate(points);
        int numOfPoints = points.length;
        if (numOfPoints < 4) { return; }
        Point[] pointSet = points.clone();
        Arrays.sort(pointSet);
        // Given a point p, the following method determines
        // whether p participates in a set of 4 or more collinear points.
        for (int i = 0; i < numOfPoints; i++) {
            // Think of p as the origin.
            // Sort the points according to the slopes they makes with p.
            Point[] ps = pointSet.clone();
            Arrays.sort(ps, ps[i].slopeOrder());
            // For each other point q, determine the slope it makes with p.
            // Also note that ps[0] == pointSet[i]
            for (int idxSlow = 1, idxFast; idxSlow < numOfPoints - 2; idxSlow = idxFast) {
                idxFast = idxSlow;
                double slopeSlow = ps[0].slopeTo(ps[idxFast++]);
                double slopeFast = ps[0].slopeTo(ps[idxFast++]);

                while (slopeSlow == slopeFast) {
                    if (idxFast == numOfPoints) {
                        idxFast++;
                        break;
                    }
                    slopeFast = ps[0].slopeTo(ps[idxFast++]);
                }
                idxFast--;
                // Check if any 3 or more adjacent points in the sorted order
                // have equal slopes with respect to p.
                // If so, these points, together with p, are collinear.
                int numOfAdjacentPoint = idxFast - idxSlow;
                if (numOfAdjacentPoint >= 3) {
                    // sort the array as previous sort is unstable
                    Point[] segment = new Point[numOfAdjacentPoint + 1];
                    segment[0] = ps[0];
                    System.arraycopy(ps, idxSlow, segment, 1, numOfAdjacentPoint);
                    Arrays.sort(segment);
                    // make sure no duplicate subsegment
                    if (segment[0] == ps[0]) {
                        lineSegments.add(new LineSegment(segment[0], segment[numOfAdjacentPoint]));
                    }
                }
            }
        }
    }

    public int numberOfSegments() {
        return lineSegments.size();
    }

    public LineSegment[] segments() {
        return lineSegments.toArray(new LineSegment[numberOfSegments()]);
    }

    private void validate(Point[] points) {
        if (points == null) { throw new IllegalArgumentException(); }
        final int numOfPoints = points.length;

        for (Point point : points) {
            if (point == null) { throw new IllegalArgumentException(); }
        }

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                if ((points[j].equals(points[i]))) { throw new IllegalArgumentException(); }
            }
        }
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];

        for (int i = 0; i < n; i++)
        {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) { p.draw(); }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments())
        {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
