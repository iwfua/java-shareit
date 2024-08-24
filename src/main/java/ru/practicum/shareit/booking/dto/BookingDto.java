package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.status.Status;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingDto {
    private Integer id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer itemId;
    private Integer bookerId;
    private Status status;
}