package packetfilter.packet.workers.pools.workflows;

import packetfilter.utils.ArgumentValidator;

public class PartialPipelineWorkflowBuilder {

    private Integer numDataWorkers;
    private Integer numConfigWorkers;

    public PartialPipelineWorkflowBuilder numDataWorkers(int count) {
        ArgumentValidator.validateGreaterThanZero(count);

        this.numDataWorkers = count;
        return this;
    }

    public PartialPipelineWorkflowBuilder numConfigWorkers(int count) {
        ArgumentValidator.validateGreaterThanZero(count);

        this.numConfigWorkers = count;
        return this;
    }

    public PartialPipelineWorkflow build() throws IncompleteWorkflowBuildException {
        try {
            ArgumentValidator.notNull(this.numDataWorkers);
            ArgumentValidator.notNull(this.numConfigWorkers);
        } catch (IllegalArgumentException e) {
            throw new IncompleteWorkflowBuildException();
        }

        return new PartialPipelineWorkflow(numDataWorkers, numConfigWorkers);
    }

}