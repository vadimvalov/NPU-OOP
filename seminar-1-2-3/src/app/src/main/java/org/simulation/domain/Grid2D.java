package org.simulation.domain;

import org.simulation.core.SimulationDomain;   

public class Grid2D implements SimulationDomain {
    private final int nx;
    private final int ny;
    private final double lx;
    private final double ly;
    private final double dx;
    private final double dy;
    
    private BoundaryCondition boundaryCondition;
    
    public Grid2D(int nx, int ny, double lx, double ly) {
        if (nx < 3 || ny < 3) {
            throw new IllegalArgumentException("Grid must have at least 3x3 nodes");
        }
        if (lx <= 0 || ly <= 0) {
            throw new IllegalArgumentException("Domain dimensions must be positive");
        }
        
        this.nx = nx;
        this.ny = ny;
        this.lx = lx;
        this.ly = ly;
        this.dx = lx / (nx - 1);
        this.dy = ly / (ny - 1);
        
        this.boundaryCondition = new DirichletBoundaryCondition(0.0);
    }
    
    public Grid2D(int nx, int ny) {
        this(nx, ny, 1.0, 1.0);
    }
    
    @Override
    public int getSize() {
        return nx * ny;
    }
    
    @Override
    public int getNx() {
        return nx;
    }
    
    @Override
    public int getNy() {
        return ny;
    }
    
    @Override
    public double getDx() {
        return dx;
    }
    
    @Override
    public double getDy() {
        return dy;
    }
    
    public double getLx() {
        return lx;
    }
    
    public double getLy() {
        return ly;
    }
    
    @Override
    public double getX(int i) {
        if (i < 0 || i >= nx) {
            throw new IndexOutOfBoundsException("X index out of bounds: " + i);
        }
        return i * dx;
    }
    
    @Override
    public double getY(int j) {
        if (j < 0 || j >= ny) {
            throw new IndexOutOfBoundsException("Y index out of bounds: " + j);
        }
        return j * dy;
    }
    
    @Override
    public boolean isBoundary(int i, int j) {
        return i == 0 || i == nx - 1 || j == 0 || j == ny - 1;
    }
    
    @Override
    public int getIndex(int i, int j) {
        if (i < 0 || i >= nx || j < 0 || j >= ny) {
            throw new IndexOutOfBoundsException(
                "Invalid indices (" + i + ", " + j + ") for grid " + nx + "x" + ny
            );
        }
        return j * nx + i;
    }
    
    public int[] getIndices2D(int index) {
        if (index < 0 || index >= nx * ny) {
            throw new IndexOutOfBoundsException("Invalid linear index: " + index);
        }
        
        int i = index % nx;
        int j = index / nx;
        return new int[]{i, j};
    }
    
    @Override
    public void applyBoundaryConditions(double[] field) {
        if (field.length != nx * ny) {
            throw new IllegalArgumentException(
                "Field size " + field.length + " does not match grid size " + (nx * ny)
            );
        }
        
        if (boundaryCondition != null) {
            boundaryCondition.apply(this, field);
        }
    }
    
    public void setBoundaryCondition(BoundaryCondition bc) {
        this.boundaryCondition = bc;
    }
    
    public BoundaryCondition getBoundaryCondition() {
        return boundaryCondition;
    }
    
    public boolean isInterior(int i, int j) {
        return i > 0 && i < nx - 1 && j > 0 && j < ny - 1;
    }
    
    public double computeLaplacian(double[] field, int i, int j) {
        if (!isInterior(i, j)) {
            return 0.0;
        }
        
        double u_ij = field[getIndex(i, j)];
        double u_ip = field[getIndex(i + 1, j)];
        double u_im = field[getIndex(i - 1, j)];
        double u_jp = field[getIndex(i, j + 1)];
        double u_jm = field[getIndex(i, j - 1)];
        
        double d2u_dx2 = (u_ip - 2.0 * u_ij + u_im) / (dx * dx);
        double d2u_dy2 = (u_jp - 2.0 * u_ij + u_jm) / (dy * dy);
        
        return d2u_dx2 + d2u_dy2;
    }
    
    public double[] computeGradient(double[] field, int i, int j) {
        double dudx = 0.0;
        double dudy = 0.0;
        
        if (i > 0 && i < nx - 1) {
            double u_ip = field[getIndex(i + 1, j)];
            double u_im = field[getIndex(i - 1, j)];
            dudx = (u_ip - u_im) / (2.0 * dx);
        }
        
        if (j > 0 && j < ny - 1) {
            double u_jp = field[getIndex(i, j + 1)];
            double u_jm = field[getIndex(i, j - 1)];
            dudy = (u_jp - u_jm) / (2.0 * dy);
        }
        
        return new double[]{dudx, dudy};
    }
    
    @Override
    public String toString() {
        return String.format(
            "Grid2D: %dx%d nodes, domain [%.2f x %.2f] m, spacing [dx=%.4f, dy=%.4f] m",
            nx, ny, lx, ly, dx, dy
        );
    }
    
    public double getCellVolume() {
        return dx * dy;
    }
}