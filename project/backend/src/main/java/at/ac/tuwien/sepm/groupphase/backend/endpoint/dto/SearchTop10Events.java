package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.EventType;

import java.time.YearMonth;

public record SearchTop10Events(
    EventType category,
    YearMonth yearMonth
) {

}
