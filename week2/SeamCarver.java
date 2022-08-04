import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    // Instance variables
    private Picture officialCopy;
    private Picture staticCopy;
    private double[][] energy;
    private boolean isTransposed;

    // Constructor
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Can't modify a null image!");
        officialCopy = new Picture(picture);
        staticCopy = new Picture(picture);
        energy = new double[officialCopy.width()][officialCopy.height()];
        isTransposed = false;
    }

    // Return current picture
    public Picture picture() {
        return staticCopy;
    }

    // Width of current picture
    public int width() {
        return staticCopy.width();
    }

    // Height of current picture
    public int height() {
        return staticCopy.height();
    }

    // Helper function to check if input coordinates are valid
    private boolean isIllegal(int cols, int rows) {
        if (cols >= 0 && cols < width()) {
            if (rows >= 0 && rows < height()) return false;
        }
        return true;
    }

    // Energy of a particular pixel at coordinate x, y
    public double energy(int x, int y) {
        if (isIllegal(x, y)) throw new IllegalArgumentException("Arguments are out of range!");

        // Return energy if it's already known and stored
        if (energy[x][y] != 0) return energy[x][y];

        // If edge or corner pixel, energy is 1000 by default;
        if (x == width() - 1 || x == 0) return 1000;
        else if (y == height() - 1 || y == 0) return 1000;

        // Calculate the energy if new value
        double sumX = 0, sumY = 0;
        // G, B, R
        for (int i = 0; i < 3; i++) {
            double tempx1 = (staticCopy.getRGB(x - 1, y) >> i * 8) & 0xFF;
            double tempx2 = (staticCopy.getRGB(x + 1, y) >> i * 8) & 0xFF;
            double diffx = tempx1 - tempx2;
            sumX += diffx * diffx;
            double temp3 = (staticCopy.getRGB(x, y - 1) >> i * 8) & 0xFF;
            double temp4 = (staticCopy.getRGB(x, y + 1) >> i * 8) & 0xFF;
            double diffy = temp3 - temp4;
            sumY += diffy * diffy;
        }
        // Store the energy and return it
        energy[x][y] = Math.sqrt(sumX + sumY);
        return energy[x][y];
    }

    // Helper function for energy
    private double energyHelper(int cols, int rows) {
        if (isTransposed) {
            return energy(rows, cols);
        }
        else return energy(cols, rows);
    }

    private int[] runTopologicalSearch() {
        // Declare helper arrays
        double[][] distTo = new double[officialCopy.width()][officialCopy.height()];
        int[][] cameFrom = new int[officialCopy.width()][officialCopy.height()];

        // Set up the array[][] distances to be infinity
        for (int rows = 0; rows < officialCopy.height(); rows++) {
            for (int cols = 0; cols < officialCopy.width(); cols++) {
                if (rows == 0) {
                    distTo[cols][rows] = 1000.0;
                    cameFrom[cols][rows] = cols;
                }
                else distTo[cols][rows] = Double.POSITIVE_INFINITY;
            }
        }


        // Explore one pixel at a time, except the last row
        for (int rows = 0; rows < officialCopy.height() - 1; rows++) {
            for (int cols = 0; cols < officialCopy.width(); cols++) {
                // Bottom left
                if (cols > 0) {
                    if (distTo[cols - 1][rows + 1] > distTo[cols][rows] + energyHelper(cols - 1,
                                                                                       rows + 1)) {
                        distTo[cols - 1][rows + 1] = distTo[cols][rows] + energyHelper(cols - 1,
                                                                                       rows + 1);
                        cameFrom[cols - 1][rows + 1] = cols;
                    }
                }
                // Bottom right
                if (cols < officialCopy.width() - 1) {
                    if (distTo[cols + 1][rows + 1] > distTo[cols][rows] + energyHelper(cols + 1,
                                                                                       rows + 1)) {
                        distTo[cols + 1][rows + 1] = distTo[cols][rows] + energyHelper(cols + 1,
                                                                                       rows + 1);
                        cameFrom[cols + 1][rows + 1] = cols;
                    }
                }
                // Bottom
                if (distTo[cols][rows + 1] > distTo[cols][rows] + energyHelper(cols, rows + 1)) {
                    distTo[cols][rows + 1] = distTo[cols][rows] + energyHelper(cols, rows + 1);
                    cameFrom[cols][rows + 1] = cols;
                }
            }
        }

        // Scan for shortest path
        double min = Double.POSITIVE_INFINITY;
        int voila = 0;
        for (int i = 0; i < officialCopy.width(); i++) {
            if (distTo[i][officialCopy.height() - 1] < min) {
                min = distTo[i][officialCopy.height() - 1];
                voila = i;
            }
        }

        int[] seam = new int[officialCopy.height()];
        seam[officialCopy.height() - 1] = voila;
        for (int i = 0; i < officialCopy.height() - 1; i++) {
            voila = cameFrom[voila][officialCopy.height() - 1 - i];
            seam[officialCopy.height() - 2 - i] = voila;
        }

        return seam;
    }

    private void transpose() {
        Picture modifiedCopyTranspose = new Picture(officialCopy.height(), officialCopy.width());
        for (int rows = 0; rows < officialCopy.height(); rows++) {
            for (int cols = 0; cols < officialCopy.width(); cols++) {
                modifiedCopyTranspose.setRGB(rows, cols, officialCopy.getRGB(cols, rows));
            }
        }
        officialCopy = modifiedCopyTranspose;
        isTransposed = !isTransposed;
    }

    // Find a horizontal seam
    public int[] findHorizontalSeam() {
        if (!isTransposed) transpose();
        return runTopologicalSearch();
    }

    // Find a vertical seam
    public int[] findVerticalSeam() {
        if (isTransposed) transpose();
        return runTopologicalSearch();
    }

    // Private function to check seam's validity
    private void checkSeam(int[] seam, int dim) {
        if (seam == null) throw new IllegalArgumentException("Null argument!");
        if (dim == 0) {
            if (seam.length != width())
                throw new IllegalArgumentException("Invalid seam!");
        }
        if (dim == 1) {
            if (seam.length != height())
                throw new IllegalArgumentException("Invalid seam!");
        }
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0) throw new IllegalArgumentException("Invalid element in seam!");
            if (dim == 0) {
                if (seam[i] > height() - 1)
                    throw new IllegalArgumentException("Invalid element in seam!");
            }
            if (dim == 1) {
                if (seam[i] > width() - 1)
                    throw new IllegalArgumentException("Invalid element in seam!");
            }
            if (i != seam.length - 1) {
                int temp = seam[i] - seam[i + 1];
                if (!(temp * temp == 1 || temp * temp == 0))
                    throw new IllegalArgumentException("Invald elements in seam!");
            }
        }
    }

    public void removeHorizontalSeam(int[] seam) {
        checkSeam(seam, 0);
        if (height() <= 1) throw new IllegalArgumentException("Image don't got more rows lol ok");
        // Transpose the picture if it is
        if (isTransposed) transpose();
        // Create new picture
        Picture tempPicture = new Picture(width(), height() - 1);
        // Create new energy grid
        double[][] tempEnergy = new double[width()][height() - 1];
        for (int cols = 0; cols < width(); cols++) {
            for (int rows = 0; rows < height() - 1; rows++) {
                // Before the row to be removed
                if (rows < seam[cols] - 1) {
                    tempPicture.setRGB(cols, rows, picture().getRGB(cols, rows));
                    tempEnergy[cols][rows] = energy(cols, rows);
                }
                // One row before
                else if (rows == seam[cols] - 1) {
                    tempPicture.setRGB(cols, rows, picture().getRGB(cols, rows));
                    tempEnergy[cols][rows] = 0;
                }
                // Removed row
                else if (rows == seam[cols]) {
                    tempPicture.setRGB(cols, rows, picture().getRGB(cols, rows + 1));
                    tempEnergy[cols][rows] = 0;
                }
                // After removed row
                else {
                    tempPicture.setRGB(cols, rows, picture().getRGB(cols, rows + 1));
                    tempEnergy[cols][rows] = energy(cols, rows + 1);
                }
            }
        }
        energy = tempEnergy;
        officialCopy = tempPicture;
        staticCopy = new Picture(officialCopy);
        isTransposed = false;
    }

    public void removeVerticalSeam(int[] seam) {
        checkSeam(seam, 1);
        if (width() <= 1)
            throw new IllegalArgumentException("Image don't got more collumns lol ok");
        // Transpose the picture if it is transposed
        if (isTransposed) transpose();
        // Create new picture
        Picture tempPicture = new Picture(width() - 1, height());
        // New energy grid
        double[][] tempEnergy = new double[width() - 1][height()];
        for (int rows = 0; rows < height(); rows++) {
            for (int cols = 0; cols < width() - 1; cols++) {
                // Before the column to be removed
                if (cols < seam[rows] - 1) {
                    tempPicture.setRGB(cols, rows, picture().getRGB(cols, rows));
                    tempEnergy[cols][rows] = energy(cols, rows);
                }
                // One col before
                else if (cols == seam[rows] - 1) {
                    tempPicture.setRGB(cols, rows, picture().getRGB(cols, rows));
                    tempEnergy[cols][rows] = 0;
                }
                // Removed col
                else if (cols == seam[rows]) {
                    tempPicture.setRGB(cols, rows, picture().getRGB(cols + 1, rows));
                    tempEnergy[cols][rows] = 0;
                }
                // After removed col
                else {
                    tempPicture.setRGB(cols, rows, picture().getRGB(cols + 1, rows));
                    tempEnergy[cols][rows] = energy(cols + 1, rows);
                }
            }
        }
        energy = tempEnergy;
        officialCopy = tempPicture;
        staticCopy = new Picture(officialCopy);
        isTransposed = false;
    }

    public static void main(String[] args) {
        /* Intended to be empty */
    }

}
