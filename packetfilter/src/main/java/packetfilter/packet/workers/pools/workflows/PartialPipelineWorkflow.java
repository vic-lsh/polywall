package packetfilter.packet.workers.pools.workflows;

public record PartialPipelineWorkflow(int numDataWorkers, int numConfigWorkers) {

    public int numTotalWorkers() {
        return numConfigWorkers + numDataWorkers;
    }
}
