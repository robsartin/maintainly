package solutions.mystuff.infrastructure.correlation;

/**
 * Thread-local holder for UUIDv7 correlation identifiers.
 *
 * @see CorrelationIdFilter
 */
public final class CorrelationIdContext {

    private static final ThreadLocal<String> HOLDER =
            new ThreadLocal<>();

    private CorrelationIdContext() {
    }

    /** Returns the correlation ID for the current thread. */
    public static String getId() {
        return HOLDER.get();
    }

    /** Sets the correlation ID for the current thread. */
    public static void setId(String id) {
        HOLDER.set(id);
    }

    /** Removes the correlation ID from the current thread. */
    public static void clear() {
        HOLDER.remove();
    }
}
