package com.robsartin.maintainly.infrastructure.correlation;

public final class CorrelationIdContext {

    private static final ThreadLocal<String> HOLDER =
            new ThreadLocal<>();

    private CorrelationIdContext() {
    }

    public static String getId() {
        return HOLDER.get();
    }

    public static void setId(String id) {
        HOLDER.set(id);
    }

    public static void clear() {
        HOLDER.remove();
    }
}
