package org.simulation.concurrency;

/**
 * Task 2: Subdomain limits for domain decomposition parallelism.
 */
public class Subdomain {
    private final int startX, endX;
    private final int startY, endY;
    
    public Subdomain(int startX, int endX, int startY, int endY) {
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }
    
    public int getStartX() { return startX; }
    public int getEndX() { return endX; }
    public int getStartY() { return startY; }
    public int getEndY() { return endY; }
}
