package packetfilter.drivers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import packetfilter.drivers.coarse.CoarsePartialPipelineDriver;
import packetfilter.drivers.coarse.CoarsePhasedPipelineDriver;
import packetfilter.drivers.coarse.CoarseTaskPoolDriver;
import packetfilter.drivers.coarse.CoarseWorkerPoolDriver;
import packetfilter.drivers.finegrained.FineGrainedPartialPipelineDriver;
import packetfilter.drivers.finegrained.FineGrainedPhasedPipelineDriver;
import packetfilter.drivers.finegrained.FineGrainedTaskPoolDriver;
import packetfilter.drivers.finegrained.FineGrainedWorkerPoolDriver;
import packetfilter.drivers.nonblocking.NonBlockingPartialPipelineDriver;
import packetfilter.drivers.nonblocking.NonBlockingPhasedPipelineDriver;
import packetfilter.drivers.nonblocking.NonBlockingTaskPoolDriver;
import packetfilter.drivers.nonblocking.NonBlockingWorkerPoolDriver;
import packetfilter.drivers.serial.SerialFirewallDriver;
import packetfilter.drivers.stripedlock.pngr.StripedLockPNGRPartialPipelineDriver;
import packetfilter.drivers.stripedlock.pngr.StripedLockPNGRPhasedPipelineDriver;
import packetfilter.drivers.stripedlock.pngr.StripedLockPNGRTaskPoolDriver;
import packetfilter.drivers.stripedlock.pngr.StripedLockPNGRWorkerPoolDriver;
import packetfilter.drivers.stripedlock.standard.StripedLockPartialPipelineDriver;
import packetfilter.drivers.stripedlock.standard.StripedLockPhasedPipelineDriver;
import packetfilter.drivers.stripedlock.standard.StripedLockTaskPoolDriver;
import packetfilter.drivers.stripedlock.standard.StripedLockWorkerPoolDriver;
import packetfilter.firebench.config.ThreadingConfig;
import packetfilter.packet.generators.PacketGeneratorConfig;

public class DriversManifest implements Iterable<Supplier<FirewallDriver>> {

    private final ArrayList<Supplier<FirewallDriver>> driverFactories;
    public final SerialFirewallDriver serialDriver;

    public DriversManifest(ThreadingConfig threadingConfig, PacketGeneratorConfig pktGenConfig) {
        this.driverFactories = createDrivers(threadingConfig, pktGenConfig);
        this.serialDriver = new SerialFirewallDriver(pktGenConfig);
    }

    private static ArrayList<Supplier<FirewallDriver>> createDrivers(ThreadingConfig threadingConfig,
            PacketGeneratorConfig pktGenCfg) {
        int numThreads = threadingConfig.defaultNumThreads;
        var partialPipelineWorkflow = threadingConfig.partialPipelineWorkflow;
        var phasedPipelineWorkflow = threadingConfig.phasedPipelineWorkflow;

        ArrayList<Supplier<FirewallDriver>> drivers = new ArrayList<>(List.of(
                () -> new CoarseTaskPoolDriver(numThreads, pktGenCfg),
                () -> new CoarseWorkerPoolDriver(numThreads, pktGenCfg),
                () -> new CoarsePartialPipelineDriver(partialPipelineWorkflow, pktGenCfg),
                () -> new CoarsePhasedPipelineDriver(phasedPipelineWorkflow, pktGenCfg),

                () -> new StripedLockTaskPoolDriver(numThreads, pktGenCfg),
                () -> new StripedLockWorkerPoolDriver(numThreads, pktGenCfg),
                () -> new StripedLockPartialPipelineDriver(partialPipelineWorkflow, pktGenCfg),
                () -> new StripedLockPhasedPipelineDriver(phasedPipelineWorkflow, pktGenCfg),

                () -> new StripedLockPNGRTaskPoolDriver(numThreads, pktGenCfg),
                () -> new StripedLockPNGRWorkerPoolDriver(numThreads, pktGenCfg),
                () -> new StripedLockPNGRPartialPipelineDriver(partialPipelineWorkflow, pktGenCfg),
                () -> new StripedLockPNGRPhasedPipelineDriver(phasedPipelineWorkflow, pktGenCfg),

                () -> new FineGrainedTaskPoolDriver(numThreads, pktGenCfg),
                () -> new FineGrainedWorkerPoolDriver(numThreads, pktGenCfg),
                () -> new FineGrainedPartialPipelineDriver(partialPipelineWorkflow, pktGenCfg),
                () -> new FineGrainedPhasedPipelineDriver(phasedPipelineWorkflow, pktGenCfg),

                () -> new NonBlockingTaskPoolDriver(numThreads, pktGenCfg),
                () -> new NonBlockingWorkerPoolDriver(numThreads, pktGenCfg),
                () -> new NonBlockingPartialPipelineDriver(partialPipelineWorkflow, pktGenCfg),
                () -> new NonBlockingPhasedPipelineDriver(phasedPipelineWorkflow, pktGenCfg)));

        return drivers;
    }

    @Override
    public Iterator<Supplier<FirewallDriver>> iterator() {
        return this.driverFactories.iterator();
    }
}
