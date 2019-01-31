package assignment7;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private static final double BORDER_ENERGY = 1000.0;
    private static final int R = 16;
    private static final int G = 8;
    private static final int B = 0;
    private final int[] pic;
    private int width, height;
    private double[] energy;
    private boolean isTransposed = false;         // flag of the picture is now transposed or not
    private boolean isHorizontalCall = false;     // flag of the picture is called horizontally now or not

    /**
     * Create a seam carver object based on the given picture
     *
     * @param picture the given picture
     * @throws IllegalArgumentException if argument is null
     */
    public SeamCarver(Picture picture) {
        validate(picture);
        width = picture.width();
        height = picture.height();
        pic = new int[width * height];
        energy = new double[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pic[xyTo1D(x, y)] = picture.getRGB(x, y);
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // calcEnergy() needs full pic[]
                // use two iterations full copy and then calculate energy
                energy[xyTo1D(x, y)] = calcEnergy(x, y);
            }
        }
    }

    /**
     * Return current picture
     *
     * @return the current picture
     */
    public Picture picture() {
        checkTransposed();
        Picture picture = new Picture(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                picture.setRGB(x, y, pic[xyTo1D(x, y)]);
            }
        }
        return picture;
    }

    /**
     * Return the width of current picture
     *
     * @return the width of current picture
     */
    public int width() {
        checkTransposed();
        return width;
    }

    /**
     * Return the height of current picture
     *
     * @return the height of current picture
     */
    public int height() {
        checkTransposed();
        return height;
    }

    /**
     * Return the dual-gradient energy of pixel at column x and row y
     * <p>
     * The energy of pixel (x, y) is $sqrt(Δ_{x}^{2}(x, y) + Δ_{y}^{2}(x, y)$,
     * where the square of the x-gradient
     * $Δ_{x}^{2}(x, y) = R_{x}^{2}(x, y) + G_{x}^{2}(x, y) + B_{x}^{2}(x, y)$,
     * and where the central differences $R_x(x, y)$, $G_x(x, y)$, and $B_x(x, y)$
     * are the differences in the red, green, and blue components between
     * pixel (x + 1, y) and pixel (x − 1, y), respectively. The square of
     * the y-gradient $Δ_{y}^{2}(x, y)$ is defined in an analogous manner.
     * <p>
     * We define the energy of a pixel at the border of the image to be 1000,
     * so that it is strictly larger than the energy of any interior pixel.
     *
     * @param x the column of the pixel
     * @param y the row of the pixel
     * @return the dual-gradient energy of the pixel
     * @throws IllegalArgumentException if argument is out of width/height range
     */
    public double energy(int x, int y) {
        checkTransposed();
        validate(x, width);
        validate(y, height);
        return energy[xyTo1D(x, y)];
    }

    /**
     * Calculate the dual-gradient energy at (x, y)
     *
     * @param x the column of the pixel
     * @param y the row of the pixel
     * @return the dual-gradient energy at (x, y)
     */
    private double calcEnergy(int x, int y) {
        if (x == 0 || y == 0 || x == width - 1 || y == height - 1) return BORDER_ENERGY;
        return Math.sqrt(
                Math.pow(getRGBColor(x + 1, y, R) - getRGBColor(x - 1, y, R), 2)
                        + Math.pow(getRGBColor(x + 1, y, G) - getRGBColor(x - 1, y, G), 2)
                        + Math.pow(getRGBColor(x + 1, y, B) - getRGBColor(x - 1, y, B), 2)
                        + Math.pow(getRGBColor(x, y + 1, R) - getRGBColor(x, y - 1, R), 2)
                        + Math.pow(getRGBColor(x, y + 1, G) - getRGBColor(x, y - 1, G), 2)
                        + Math.pow(getRGBColor(x, y + 1, B) - getRGBColor(x, y - 1, B), 2)
        );
    }

    /**
     * Decoded the RGB colors encoded as a 32-bit int into seperate channel
     * <p>
     * Given a 32-bit int encoding the color, the following code extracts the RGB components:
     * <p>
     * int r = (rgb >> 16) & 0xFF;
     * int g = (rgb >>  8) & 0xFF;
     * int b = (rgb >>  0) & 0xFF;
     * <p>
     * Given the RGB components (8-bits each) of a color, the following statement packs it into a 32-bit int:
     * int rgb = (r << 16) + (g << 8) + (b << 0);
     * <p>
     * <a href="https://algs4.cs.princeton.edu/code/javadoc/edu/princeton/cs/algs4/Picture.html">
     * encoded as a 32-bit int</a>
     *
     * @param x       the column of the pixel
     * @param y       the row of the pixel
     * @param channel int value of R,G,B channels
     * @return the color of {@code channel} at pixel (x, y)
     */
    private int getRGBColor(int x, int y, int channel) {
        return pic[xyTo1D(x, y)] >> channel & 0xFF;
    }

    /**
     * Return the sequence of indices for horizontal seam
     *
     * @return the sequence of indices for horizontal seam
     * @throws IllegalArgumentException if argument is null
     */
    public int[] findHorizontalSeam() {
        isHorizontalCall = true;
        checkTransposed();
        int[] seam = findVerticalSeam();
        isHorizontalCall = false;
        return seam;
    }

    /**
     * Return the sequence of indices for vertical seam
     *
     * @return the sequence of indices for vertical seam
     * @throws IllegalArgumentException if argument is null
     */
    public int[] findVerticalSeam() {
        checkTransposed();
        int[] seam = new int[height];
        if (height <= 2) return seam;

        int[] edgeTo = new int[width * height];
        double[] distTo = new double[width * height];

        // Initiate distTo to Infinity
        for (int x = 0; x < width; x++) {
            for (int y = 1; y < height; y++) {
                distTo[xyTo1D(x, y)] = Double.POSITIVE_INFINITY;
            }
        }
        // You should not need recursion. Note that the underlying DAG has such special structure
        // that you don’t need to compute its topological order explicitly
        // Run BFS (topological sort) for the implicit DAG to search energy and save at distTo[]
        // The row-major or column-major traverse order make a difference here
        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x > 0 && distTo[xyTo1D(x - 1, y)] > energy[xyTo1D(x - 1, y)] + distTo[xyTo1D(x, y - 1)]) {
                    distTo[xyTo1D(x - 1, y)] = energy[xyTo1D(x - 1, y)] + distTo[xyTo1D(x, y - 1)];
                    edgeTo[xyTo1D(x - 1, y)] = x;
                }
                if (distTo[xyTo1D(x, y)] > energy[xyTo1D(x, y)] + distTo[xyTo1D(x, y - 1)]) {
                    distTo[xyTo1D(x, y)] = energy[xyTo1D(x, y)] + distTo[xyTo1D(x, y - 1)];
                    edgeTo[xyTo1D(x, y)] = x;
                }
                if (x < width - 1 && distTo[xyTo1D(x + 1, y)] > energy[xyTo1D(x + 1, y)] + distTo[xyTo1D(x, y - 1)]) {
                    distTo[xyTo1D(x + 1, y)] = energy[xyTo1D(x + 1, y)] + distTo[xyTo1D(x, y - 1)];
                    edgeTo[xyTo1D(x + 1, y)] = x;
                }
            }
        }
        // Find the seam endpoint of minimum energy at the last line
        for (int x = 1; x < width - 1; x++) {
            if (distTo[xyTo1D(seam[height - 2], height - 2)] > distTo[xyTo1D(x, height - 2)]) {
                seam[height - 2] = x;
            }
        }
        // Use edgeTo to backtrack the path of minimum energy
        for (int y = height - 2; y > 0; y--) {
            seam[y - 1] = edgeTo[xyTo1D(seam[y], y)];
        }
        // Follow set at border of test examples
        // helpful to reset energy when shifting array elements
        // cutoff to be 0
        seam[height - 1] = Math.max(seam[height - 2] - 1, 0);
        return seam;
    }

    /**
     * Remove horizontal seam from current picture
     *
     * @param seam the sequence of indices for horizontal seam
     * @throws IllegalArgumentException if argument is null
     *                                  or argument array is of wrong length
     *                                  or if the array is not a valid seam (i.e., either an entry is outside its
     *                                  prescribed range, or two adjacent entries differ by more than 1)
     *                                  or is called when the height of the picture is less than or equal to 1
     */
    public void removeHorizontalSeam(int[] seam) {
        isHorizontalCall = true;
        checkTransposed();
        validate(seam);
        removeVerticalSeam(seam);
        isHorizontalCall = false;
    }

    /**
     * Remove vertical seam from current picture
     *
     * @param seam the sequence of indices for vertical seam
     * @throws IllegalArgumentException if argument is null
     *                                  or argument array is of wrong length
     *                                  or if the array is not a valid seam (i.e., either an entry is outside its
     *                                  prescribed range, or two adjacent entries differ by more than 1)
     *                                  or is called when the width of the picture is less than or equal to 1
     */
    public void removeVerticalSeam(int[] seam) {
        checkTransposed();
        validate(seam);
        // Fail to make this method transpose-agnostic as the use of 1D representation does not
        // allow System.arraycopy() in calls from removeHorizontalSeam().
        // Compared to 2D representation, 1D reduces the transpose overhead of swapping [x][y],
        // yet introduces function call overhead to xyTo1D() and complex like xyTo1DAfterwards().
        // In all, 1D array representation is not recommended to implement.
        if (!isTransposed) {
            for (int y = 0; y < height; y++) {
                System.arraycopy(pic, xyTo1D(seam[y], y) + 1, pic, xyTo1DAfterwards(seam[y], y),
                        width - seam[y] - 1 + (y < height - 1 ? seam[y + 1] : 0));
                System.arraycopy(energy, xyTo1D(seam[y], y) + 1, energy, xyTo1DAfterwards(seam[y], y),
                        width - seam[y] - 1 + (y < height - 1 ? seam[y + 1] : 0));
                // Corner case when (0, y) is at seam
                // seam[y] must not reach (width - 1), as we shift seam at border smaller by 1
                if (seam[y] == 0) energy[xyTo1DAfterwards(0, y)] = BORDER_ENERGY;
            }
        } else {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (x > seam[y]) {
                        pic[xyTo1D(x - 1, y)] = pic[xyTo1D(x, y)];
                        energy[xyTo1D(x - 1, y)] = energy[xyTo1D(x, y)];
                    }
                }
                if (seam[y] == 0) energy[xyTo1D(0, y)] = BORDER_ENERGY;
            }
        }
        width--;
        // Recalculate the energies only for the pixels along the seam that was just removed
        for (int y = 1; y < height - 1; y++) {
            if (seam[y] > 1) energy[xyTo1D(seam[y] - 1, y)] = calcEnergy(seam[y] - 1, y);
            if (seam[y] < width - 1) energy[xyTo1D(seam[y], y)] = calcEnergy(seam[y], y);
        }
    }

    /**
     * Transpose by invert the flag and swap dimension, see {@code xyTo1D} for calculation after transposition
     */
    private void checkTransposed() {
        // Use bitwise XOR operator for (!isHorizontalCall && isTransposed || isHorizontalCall && !isTransposed)
        if (isHorizontalCall ^ isTransposed) {
            // That is, isTransposed should follow isHorizontalCall
            isTransposed = !isTransposed;
            int temp = height;
            height = width;
            width = temp;
        }
    }

    private int xyTo1D(int x, int y) {
        validate(x, width);
        validate(y, height);
        return !isTransposed ? x + width * y : y + x * height;
    }

    /**
     * Return the index of (x, y) after remove seam, as a helper method
     * to indicate dest position for removeVerticalSeam()
     *
     * @param x the column of the pixel
     * @param y the row of the pixel
     * @return the index of (x, y) after remove seam
     */
    private int xyTo1DAfterwards(int x, int y) {
        validate(x, width);
        validate(y, height);
        return !isTransposed ? x + (width - 1) * y : y + x * (height - 1);
    }

    private static void validate(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Picture argument can not be null");
    }

    private static void validate(int idx, int bound) {
        if (idx < 0 || idx >= bound)
            throw new IllegalArgumentException("Index " + idx + " is not between 0 and " + (bound - 1));
    }

    /**
     * The method is transpose-agnostic as we swap width and height during transposition
     *
     * @param seam the sequence of indices in a row for vertical seam (or in a column for horizontal seam)
     */
    private void validate(int[] seam) {
        if (width <= 1) throw new IllegalArgumentException("the width or height of the picture is <= 1");
        if (seam == null) throw new IllegalArgumentException("argument can not be null");
        if (seam.length != height) throw new IllegalArgumentException("argument array is of length" + width);
        for (int i = 0; i < seam.length; i++) {
            validate(seam[i], width);
            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException("Two adjacent entries differ by more than 1");
            }
        }
    }
}
