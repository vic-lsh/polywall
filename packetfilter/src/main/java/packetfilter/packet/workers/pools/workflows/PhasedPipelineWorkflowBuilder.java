package packetfilter.packet.workers.pools.workflows;

import packetfilter.utils.ArgumentValidator;

public class PhasedPipelineWorkflowBuilder {

    private Integer numCtrlPhaseWorkers;
    private Integer numCollectionPhaseWorkers;

    public PhasedPipelineWorkflowBuilder controlPhase(int workerCount) {
        ArgumentValidator.validateGreaterThanZero(workerCount);

        this.numCtrlPhaseWorkers = workerCount;
        return this;
    }

    public PhasedPipelineWorkflowBuilder collectionPhase(int workerCount) {
        ArgumentValidator.validateGreaterThanZero(workerCount);

        this.numCollectionPhaseWorkers = workerCount;
        return this;
    }

    public PhasedPipelineWorkflow build() throws IncompleteWorkflowBuildException {
        try {
            ArgumentValidator.notNull(this.numCtrlPhaseWorkers);
            ArgumentValidator.notNull(this.numCollectionPhaseWorkers);
        } catch (IllegalArgumentException e) {
            throw new IncompleteWorkflowBuildException();
        }

        return new PhasedPipelineWorkflow(numCtrlPhaseWorkers, numCollectionPhaseWorkers);
    }

}