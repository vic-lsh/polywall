package packetfilter.drivers;

import java.util.concurrent.ExecutionException;

import packetfilter.firewall.Firewall;
import packetfilter.packet.generators.PacketGeneratorConfig;
import packetfilter.packet.workers.pools.PacketPhasedPipelinePool;
import packetfilter.packet.workers.pools.workflows.PhasedPipelineWorkflow;

public abstract class AbstractPhasedPipelineDriver<F extends Firewall>
        extends AbstractPoolDriver<F, PacketPhasedPipelinePool> {

    protected PhasedPipelineWorkflow workflow;

    public AbstractPhasedPipelineDriver(PhasedPipelineWorkflow workflow,
            PacketGeneratorConfig packetGenConfig) {
        super(workflow.numTotalWorkers(), packetGenConfig);
        this.workflow = workflow;
    }

    @Override
    public void process(int nPackets) throws InterruptedException, ExecutionException {
        this.processWithPool(nPackets);
    }

    @Override
    protected PacketPhasedPipelinePool createPool() {
        return new PacketPhasedPipelinePool(workflow);
    }

    protected void processWithPool(int nPackets) throws InterruptedException, ExecutionException {
        var pool = createPool();
        pool.configure(firewallService, nPackets, pktGenConfig).start().join();
    }

}
