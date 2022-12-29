package packetfilter.packet.workers.pools.workflows;

public record PhasedPipelineWorkflow(int numCtrlPhaseWorkers, int numCollectionPhaseWorkers) {
    public int numTotalWorkers() {
        return numCtrlPhaseWorkers + numCollectionPhaseWorkers;
    }
}
