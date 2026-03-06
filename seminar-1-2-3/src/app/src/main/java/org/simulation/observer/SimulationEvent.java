package org.simulation.observer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable event published by SimulationController to all observers.
 */
public class SimulationEvent {

    public enum Type {
        ON_START,
        ON_BEFORE_STEP,
        ON_AFTER_STEP,
        ON_CONVERGED,
        ON_CHECKPOINT,
        ON_PAUSED,
        ON_SOLVER_SWITCHED,
        ON_ERROR,
        ON_STOP
    }

    private final Type                type;
    private final double              time;
    private final int                 step;
    private final Map<String, Object> metadata;

    private SimulationEvent(Type type, double time, int step, Map<String, Object> metadata) {
        this.type     = type;
        this.time     = time;
        this.step     = step;
        this.metadata = Collections.unmodifiableMap(metadata);
    }

    // ── Factory methods ───────────────────────────────────────────────────────

    public static SimulationEvent start(double time, int step) {
        return new SimulationEvent(Type.ON_START, time, step, new HashMap<>());
    }

    public static SimulationEvent beforeStep(double time, int step) {
        return new SimulationEvent(Type.ON_BEFORE_STEP, time, step, new HashMap<>());
    }

    public static SimulationEvent afterStep(double time, int step, double residual) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("residual", residual);
        return new SimulationEvent(Type.ON_AFTER_STEP, time, step, meta);
    }

    public static SimulationEvent converged(double time, int step) {
        return new SimulationEvent(Type.ON_CONVERGED, time, step, new HashMap<>());
    }

    public static SimulationEvent checkpoint(double time, int step, String filePath) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("checkpointFile", filePath);
        return new SimulationEvent(Type.ON_CHECKPOINT, time, step, meta);
    }

    public static SimulationEvent paused(double time, int step) {
        return new SimulationEvent(Type.ON_PAUSED, time, step, new HashMap<>());
    }

    public static SimulationEvent solverSwitched(double time, int step,
                                                  String fromSolver, String toSolver) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("from", fromSolver);
        meta.put("to",   toSolver);
        return new SimulationEvent(Type.ON_SOLVER_SWITCHED, time, step, meta);
    }

    public static SimulationEvent error(double time, int step, String message) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("errorMessage", message);
        return new SimulationEvent(Type.ON_ERROR, time, step, meta);
    }

    public static SimulationEvent stop(double time, int step) {
        return new SimulationEvent(Type.ON_STOP, time, step, new HashMap<>());
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public Type   getType()     { return type; }
    public double getTime()     { return time; }
    public int    getStep()     { return step; }
    public Object getMeta(String key) { return metadata.get(key); }

    @Override
    public String toString() {
        return String.format("SimulationEvent[%s  step=%d  t=%.6f]", type, step, time);
    }
}