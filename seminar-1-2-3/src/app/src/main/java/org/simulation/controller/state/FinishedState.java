package org.simulation.controller.state;

/**
 * FINISHED — simulation completed; no further transitions.
 */
public class FinishedState extends AbstractSimulationState {

    @Override
    public String getName() { return "FINISHED"; }
}
