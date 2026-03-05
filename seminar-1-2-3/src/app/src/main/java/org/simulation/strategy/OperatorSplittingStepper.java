package org.simulation.strategy;

import org.simulation.core.PhysicalModel;
import org.simulation.core.SimulationDomain;

/**
 * Operator Splitting Stepper — multi-physics coupling (A1, Interaction #3).
 *
 * Steps two models sequentially in a chosen order:
 *   HEAT_FIRST:  step(heatModel) → step(flowModel)
 *   FLOW_FIRST:  step(flowModel) → step(heatModel)
 *
 * The primaryModel passed to step() is ignored —
 * this stepper owns its two sub-models explicitly.
 */
public class OperatorSplittingStepper implements IStepperStrategy {

    public enum CouplingOrder { HEAT_FIRST, FLOW_FIRST }

    private final PhysicalModel  heatModel;
    private final PhysicalModel  flowModel;
    private final CouplingOrder  order;
    private final IStepperStrategy subStepper;

    private double lastResidual = 0.0;

    public OperatorSplittingStepper(PhysicalModel heatModel,
                                    PhysicalModel flowModel,
                                    CouplingOrder order,
                                    IStepperStrategy subStepper) {
        this.heatModel  = heatModel;
        this.flowModel  = flowModel;
        this.order      = order;
        this.subStepper = subStepper;
    }

    @Override
    public double[] step(PhysicalModel ignored, SimulationDomain domain, double dt) {
        PhysicalModel first  = (order == CouplingOrder.HEAT_FIRST) ? heatModel : flowModel;
        PhysicalModel second = (order == CouplingOrder.HEAT_FIRST) ? flowModel : heatModel;

        subStepper.step(first,  domain, dt);
        subStepper.step(second, domain, dt);

        lastResidual = subStepper.getLastResidual();
        return second.getFieldValues();
    }

    @Override
    public double getLastResidual() { return lastResidual; }

    @Override
    public String getName() {
        return "Operator Splitting (" + order + ")";
    }
}