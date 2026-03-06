package org.simulation.observer;

/**
 * Observer Pattern (B1) — receives simulation lifecycle events.
 */
public interface ISimulationObserver {
    void onEvent(SimulationEvent event);
    String getName();
}