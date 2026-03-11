package solutions.mystuff.domain.model;

/**
 * Time unit for recurring service schedule frequency.
 *
 * <p>Used by {@link ServiceSchedule} to express how often a service
 * recurs (e.g. every 3 {@code months}).
 *
 * @see ServiceSchedule
 */
public enum FrequencyUnit {
    days,
    weeks,
    months,
    years
}
