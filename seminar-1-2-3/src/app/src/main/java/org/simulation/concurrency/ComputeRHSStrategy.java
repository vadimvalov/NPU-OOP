package org.simulation.concurrency;

public interface ComputeRHSStrategy {
    void computeSubdomainRHS(Subdomain subdomain, double[] rhsOut);
}
