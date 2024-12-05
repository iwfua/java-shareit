package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingRequestDtoTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Long itemId = 1L;

        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);

        JsonContent<BookingRequestDto> result = json.write(bookingRequestDto);

        assertThat(result).hasJsonPath("$.start")
                .hasJsonPath("$.end")
                .hasJsonPath("$.itemId");

        assertThat(result).extractingJsonPathStringValue("$.start")
                .satisfies(startValue -> assertThat(startValue).isNotNull());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .satisfies(endValue -> assertThat(endValue).isNotNull());
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(itemId));
    }
}