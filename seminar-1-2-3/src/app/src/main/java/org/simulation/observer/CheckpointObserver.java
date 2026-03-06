package org.simulation.observer;

import org.simulation.controller.SimulationController;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Writes checkpoint files every N steps and on error.
 * Publishes ON_CHECKPOINT after each write.
 */
public class CheckpointObserver implements ISimulationObserver {

    private final SimulationController controller;
    private final int                  checkpointEvery;
    private final String               checkpointDir;

    public CheckpointObserver(SimulationController controller,
                               int checkpointEvery,
                               String checkpointDir) {
        this.controller      = controller;
        this.checkpointEvery = checkpointEvery;
        this.checkpointDir   = checkpointDir;
    }

    @Override
    public void onEvent(SimulationEvent event) {
        switch (event.getType()) {
            case ON_AFTER_STEP:
                if (event.getStep() % checkpointEvery == 0) {
                    writeCheckpoint(event);
                }
                break;
            case ON_ERROR:
                writeCheckpoint(event); // emergency checkpoint
                break;
            default:
                break;
        }
    }

    private void writeCheckpoint(SimulationEvent event) {
        String fileName = String.format("%s/checkpoint_step_%06d.txt",
            checkpointDir, event.getStep());

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.printf("step=%d%n", event.getStep());
            writer.printf("time=%.8f%n", event.getTime());

            double[] values = controller.getModel().getFieldValues();
            writer.printf("fieldSize=%d%n", values.length);
            for (double v : values) {
                writer.printf("%.8f%n", v);
            }
            System.out.println("[Checkpoint] Written: " + fileName);
        } catch (IOException e) {
            System.err.println("[Checkpoint] Failed to write: " + e.getMessage());
            return;
        }

        controller.getEventBus().publish(
            SimulationEvent.checkpoint(event.getTime(), event.getStep(), fileName)
        );
    }

    @Override
    public String getName() { return "CheckpointObserver"; }
}