package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.status.Status;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder(toBuilder = true)
public class Booking {
    private final Integer id;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Integer itemId;
    private final Integer bookerId;
    private Status status;
}
