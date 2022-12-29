package packetfilter.drivers;

import java.util.concurrent.ExecutionException;

import packetfilter.firewall.Firewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.PacketPartialPipielinePool;
import packetfilter.packet.workers.pools.workflows.PartialPipelineWorkflow;

public abstract class AbstractPartialPipelineDriver<F extends Firewall>
        extends AbstractPoolDriver<F, PacketPartialPipielinePool> {

    protected PartialPipelineWorkflow workflow;

    public AbstractPartialPipelineDriver(PartialPipelineWorkflow workflow, PacketGeneratorConfig packetGenConfig) {
        super(workflow.numTotalWorkers(), packetGenConfig);
        this.workflow = workflow;
    }

    @Override
    public void process(int nPackets) throws InterruptedException, ExecutionException {
        this.processWithPool(nPackets);
    }

    @Override
    protected PacketPartialPipielinePool createPool() {
        return new PacketPartialPipielinePool(workflow);
    }

    protected void processWithPool(int nPackets) throws InterruptedException, ExecutionException {
        var pool = createPool();
        pool.configure(firewallService, nPackets, pktGenConfig).start().join();
    }

}
