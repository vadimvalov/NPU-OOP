package org.simulation.observer;

/**
 * Logs simulation events to console.
 * Replaces direct System.out calls in controller/states.
 */
public class ConsoleLoggerObserver implements ISimulationObserver {

    private final int printEveryNSteps;

    public ConsoleLoggerObserver(int printEveryNSteps) {
        this.printEveryNSteps = printEveryNSteps;
    }

    @Override
    public void onEvent(SimulationEvent event) {
        switch (event.getType()) {
            case ON_START:
                System.out.println("[LOG] Simulation started at t=" + event.getTime());
                break;
            case ON_AFTER_STEP:
                if (event.getStep() % printEveryNSteps == 0) {
                    double residual = (Double) event.getMeta("residual");
                    System.out.printf("[LOG] step=%5d  t=%.6f  residual=%.2e%n",
                        event.getStep(), event.getTime(), residual);
                }
                break;
            case ON_CONVERGED:
                System.out.println("[LOG] Converged at step=" + event.getStep());
                break;
            case ON_PAUSED:
                System.out.printf("[LOG] Paused at step=%d  t=%.6f%n",
                    event.getStep(), event.getTime());
                break;
            case ON_SOLVER_SWITCHED:
                System.out.printf("[LOG] Solver switched: %s → %s at step=%d%n",
                    event.getMeta("from"), event.getMeta("to"), event.getStep());
                break;
            case ON_ERROR:
                System.err.println("[LOG] ERROR at step=" + event.getStep()
                    + ": " + event.getMeta("errorMessage"));
                break;
            case ON_STOP:
                System.out.println("[LOG] Simulation stopped at step=" + event.getStep()
                    + "  t=" + event.getTime());
                break;
            default:
                break;
        }
    }

    @Override
    public String getName() { return "ConsoleLoggerObserver"; }
}