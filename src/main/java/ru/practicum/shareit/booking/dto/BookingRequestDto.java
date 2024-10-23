package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingRequestDto {

    @Future
    @NotNull
    @JsonProperty("start")
    private LocalDateTime startDate;

    @Future
    @NotNull
    @JsonProperty("end")
    private LocalDateTime endDate;

    @NotNull
    private Long itemId;
}