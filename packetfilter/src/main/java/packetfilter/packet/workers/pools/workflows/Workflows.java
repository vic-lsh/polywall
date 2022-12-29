package packetfilter.packet.workers.pools.workflows;

public class Workflows {

    public static PartialPipelineWorkflowBuilder buildPartialPipeline() {
        return new PartialPipelineWorkflowBuilder();
    }

    public static PhasedPipelineWorkflowBuilder buildPhasedPipeline() {
        return new PhasedPipelineWorkflowBuilder();
    }
}
