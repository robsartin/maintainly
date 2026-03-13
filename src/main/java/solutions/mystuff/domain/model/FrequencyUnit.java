package solutions.mystuff.domain.model;

import java.time.LocalDate;

/**
 * Time unit for recurring service schedule frequency.
 *
 * <p>Used by {@link ServiceSchedule} to express how often a service
 * recurs (e.g. every 3 {@code months}). Each constant knows how to
 * advance a date by a given interval.
 *
 * @see ServiceSchedule
 */
public enum FrequencyUnit {
    days {
        @Override
        public LocalDate advance(
                LocalDate from, int interval) {
            return from.plusDays(interval);
        }
    },
    weeks {
        @Override
        public LocalDate advance(
                LocalDate from, int interval) {
            return from.plusWeeks(interval);
        }
    },
    months {
        @Override
        public LocalDate advance(
                LocalDate from, int interval) {
            return from.plusMonths(interval);
        }
    },
    years {
        @Override
        public LocalDate advance(
                LocalDate from, int interval) {
            return from.plusYears(interval);
        }
    };

    /** Advance the given date by the specified interval. */
    public abstract LocalDate advance(
            LocalDate from, int interval);
}
