package org.simulation.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Event bus — holds the list of observers and dispatches events to all of them.
 * Exception in one observer does not block the others.
 */
public class SimulationEventBus {

    private final List<ISimulationObserver> observers = new ArrayList<>();

    public void subscribe(ISimulationObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(ISimulationObserver observer) {
        observers.remove(observer);
    }

    public void publish(SimulationEvent event) {
        for (ISimulationObserver observer : observers) {
            try {
                observer.onEvent(event);
            } catch (Exception e) {
                System.err.println("[EventBus] Observer " + observer.getName()
                    + " threw exception on " + event.getType() + ": " + e.getMessage());
            }
        }
    }
}