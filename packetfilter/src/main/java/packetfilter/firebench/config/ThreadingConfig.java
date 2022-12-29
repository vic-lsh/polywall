package packetfilter.firebench.config;

import packetfilter.packet.workers.pools.workflows.PartialPipelineWorkflow;
import packetfilter.packet.workers.pools.workflows.PhasedPipelineWorkflow;
import packetfilter.utils.ArgumentValidator;

public class ThreadingConfig {

    public final int defaultNumThreads;
    public final PartialPipelineWorkflow partialPipelineWorkflow;
    public final PhasedPipelineWorkflow phasedPipelineWorkflow;

    public ThreadingConfig(int defaultNumThreads, PartialPipelineWorkflow partialPipelineWorkflow,
            PhasedPipelineWorkflow phasedPipelineWorkflow) {
        ArgumentValidator.validateGreaterThanZero(defaultNumThreads);

        this.defaultNumThreads = defaultNumThreads;
        this.partialPipelineWorkflow = partialPipelineWorkflow;
        this.phasedPipelineWorkflow = phasedPipelineWorkflow;
    }
}
