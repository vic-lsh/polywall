package packetfilter.utils.concurrent;

/**
 * Exception raised if attempting to poll from a TerminableQueue that's already
 * terminated.
 */
public class QueueTerminatedException extends Exception {
}
