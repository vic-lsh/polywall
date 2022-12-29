package packetfilter.firebench;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import packetfilter.drivers.FirewallDriver;
import packetfilter.firebench.logger.BenchLogger;
import packetfilter.utils.DelimitedNumberFormatter;

class FirewallBench {
    private FirewallDriver driver;

    public FirewallBench(FirewallDriver driver) {
        this.driver = driver;
    }

    /**
     * Measures how long it takes for a firewall implementation to process N
     * packets.
     * 
     * @param nPackets the number of packets to process.
     * @param nTrials  number of repeated runs.
     * @return the average time elapsed of a trial, in nanoseconds.
     * @throws InterruptedException if there is unexpected interruption while
     *                              processing N packets.
     * @throws ExecutionException
     */
    public long bench(int nPackets, int nTrials) throws InterruptedException, ExecutionException {
        long sum = 0;
        for (int i = 0; i < nTrials; i++) {
            sum += benchOnce(nPackets);
        }
        return sum / nTrials;
    }

    private long benchOnce(int nPackets) throws InterruptedException, ExecutionException {
        driver.restart();
        warmupDriver();

        long startTime = System.nanoTime();
        driver.process(nPackets);
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    private void warmupDriver() throws InterruptedException {
        var worker = driver.warmup();

        worker.start();

        var checkinTask = new Timer();

        checkinTask.schedule(new TimerTask() {
            @Override
            public void run() {
                var percentageFormatter = new DecimalFormat("##.##%");
                var numFormatter = new DelimitedNumberFormatter();
                BenchLogger.log(
                        String.format("\twarmup progress: %s (processed: %s, total: %s)",
                                percentageFormatter.format(worker.getProgress()),
                                numFormatter.format(worker.getProcessedCount()),
                                numFormatter.format(worker.getTotal())));
            }
        }, 0, 1000);

        worker.join();
        checkinTask.cancel();
        System.out.println("warmup completed");
    }
}
