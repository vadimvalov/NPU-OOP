package org.simulation.core;

public abstract class SimulationComponent {

    private final String componentId;
    private boolean initialized = false;

    protected SimulationComponent(String componentId) {
        if (componentId == null || componentId.isBlank()) {
            throw new IllegalArgumentException("componentId must not be blank");
        }
        this.componentId = componentId;
    }

    public abstract String getName();

    public abstract String describe();

    protected void onInitialize() {
        System.out.printf("[%s] Initializing component: %s%n", getClass().getSimpleName(), getName());
        System.out.printf("[%s] %s%n", getClass().getSimpleName(), describe());
        initialized = true;
    }

    protected void onShutdown() {
        System.out.printf("[%s] Shutting down: %s%n", getClass().getSimpleName(), getName());
        initialized = false;
    }

    public String getComponentId() { return componentId; }

    public boolean isInitialized() { return initialized; }

    protected void requireInitialized() {
        if (!initialized) {
            throw new IllegalStateException(
                getName() + " has not been initialized — call initialize() first");
        }
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, name='%s', init=%s]",
            getClass().getSimpleName(), componentId, getName(), initialized);
    }
}