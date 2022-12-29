package packetfilter.drivers;

import java.util.concurrent.ExecutionException;

public interface FirewallDriver {
    /**
     * Synchronously performs access-control on N packets, blocking the caller until
     * all of N packets have been processed.
     * 
     * @param nPackets the number of packets to process.
     * 
     * @throws InterruptedException if there is unexpected interruption while
     *                              processing packets.
     */
    public void process(int nPackets) throws InterruptedException, ExecutionException;

    /**
     * Processes enough config packets so the permission table reaches a stable
     * state.
     */
    public FirewallWarmupWorker warmup();

    /**
     * Reinitializes internal state to prune all side effects of previous run(s).
     */
    public void restart();

    /**
     * Get the name of this driver implementation.
     * 
     * @return the class name of this driver.
     */
    public String getName();
}
