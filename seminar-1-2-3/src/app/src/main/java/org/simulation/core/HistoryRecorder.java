package org.simulation.core;

import org.simulation.observer.ISimulationObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Task 2 Component: HistoryRecorder
 * 
 * Stores the history of the simulation by keeping selective snapshots of the fields 
 * at certain timesteps.
 * 
 * Memory Management Strategy:
 * - Ownership: The SimulationController (or main execution flow) owns the HistoryRecorder.
 * - Lifetime: Bound to the entire simulation run. Destroyed when the controller is destroyed.
 * - Allocation: Allocated on the heap. Inner data is dynamically allocated.
 * - Duplication: Deep copies of the field arrays are required to preserve historical state, 
 *   but this is strictly controlled by 'snapshotFrequency' to prevent memory bloat and OutOfMemory errors.
 * - Exceeding memory limits is avoided by retaining only double[] primitives rather than 2D generic objects.
 */
public class HistoryRecorder implements ISimulationObserver {

    private final List<Snapshot> history = new ArrayList<>();
    private final int snapshotFrequency; // How often to record
    
    // Limits the size of history to prevent unbounded memory growth
    private final int maxSnapshots; 

    private final org.simulation.controller.SimulationController controller;

    public HistoryRecorder(org.simulation.controller.SimulationController controller, int snapshotFrequency, int maxSnapshots) {
        this.controller = controller;
        this.snapshotFrequency = snapshotFrequency;
        this.maxSnapshots = maxSnapshots;
    }

    @Override
    public void onEvent(org.simulation.observer.SimulationEvent event) {
        switch (event.getType()) {
            case ON_START:
                history.clear();
                System.out.println("[HistoryRecorder] Initialized and cleared previous history.");
                break;
            case ON_AFTER_STEP:
                int currentStep = event.getStep();
                if (currentStep % snapshotFrequency == 0) {
                    if (history.size() >= maxSnapshots) {
                        history.remove(0); 
                    }
                    
                    double[] fieldValues = controller.getModel().getFieldValues();
                    double[] snapshotData = new double[fieldValues.length];
                    System.arraycopy(fieldValues, 0, snapshotData, 0, fieldValues.length);
                    
                    history.add(new Snapshot(event.getTime(), currentStep, snapshotData));
                    System.out.println("[HistoryRecorder] Recorded snapshot at step " + currentStep + " (t=" + event.getTime() + ")");
                }
                break;
            case ON_STOP:
                System.out.println("[HistoryRecorder] Simulation ended. Total snapshots saved: " + history.size());
                break;
            default:
                break;
        }
    }

    @Override
    public String getName() {
        return "HistoryRecorder";
    }
    
    public List<Snapshot> getHistory() {
        return history;
    }

    public static class Snapshot {
        public final double time;
        public final int step;
        public final double[] data;

        public Snapshot(double time, int step, double[] data) {
            this.time = time;
            this.step = step;
            this.data = data;
        }
    }
}
