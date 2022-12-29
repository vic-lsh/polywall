package packetfilter.firebench.logger;

public class BenchLogger {

    private static boolean debug = false;

    public static void log(String... messages) {
        if (debug) {
            printLogs(messages);
        }
    }

    private static void printLogs(String... messages) {
        for (String msg : messages) {
            System.out.print(msg + " ");
        }
        System.out.println();
    }

    public static void enable() {
        debug = true;
    }

    public static void disable() {
        debug = false;
    }
}
