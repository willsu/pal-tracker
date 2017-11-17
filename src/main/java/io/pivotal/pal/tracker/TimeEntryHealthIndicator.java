package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {
    private final TimeEntryRepository timeEntryRepository;

    public TimeEntryHealthIndicator(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    @Override
    public Health health() {
        Status status;
        List<TimeEntry> timeEntries = timeEntryRepository.list();

        if (timeEntries.size() <= 5) {
            status = Status.UP;
        } else {
            status = Status.DOWN;
        }
        return new Health.Builder(status).build();
    }
}
