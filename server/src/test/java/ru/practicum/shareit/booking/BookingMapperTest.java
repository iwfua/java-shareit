package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Item Description")
                .isAvailable(true)
                .build();

        booking = Booking.builder()
                .id(1L)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void shouldMapBookingToBookingResponseDto() {
        BookingResponseDto responseDto = BookingMapper.toBookingResponseDto(booking);

        assertNotNull(responseDto);
        assertEquals(booking.getId(), responseDto.getId());
        assertEquals(booking.getStartDate(), responseDto.getStart());
        assertEquals(booking.getEndDate(), responseDto.getEnd());
        assertEquals(booking.getStatus(), responseDto.getStatus());

        // Verify item mapping
        assertNotNull(responseDto.getItem());
        assertEquals(item.getId(), responseDto.getItem().getId());
        assertEquals(item.getName(), responseDto.getItem().getName());

        // Verify booker mapping
        assertNotNull(responseDto.getBooker());
        assertEquals(booker.getId(), responseDto.getBooker().getId());
        assertEquals(booker.getName(), responseDto.getBooker().getName());
    }

    @Test
    void shouldReturnNullWhenBookingIsNullInResponseDtoMapping() {
        BookingResponseDto responseDto = BookingMapper.toBookingResponseDto(null);

        assertNull(responseDto);
    }

    @Test
    void shouldMapListOfBookingsToListOfBookingResponseDto() {
        Booking secondBooking = Booking.builder()
                .id(2L)
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        List<Booking> bookings = Arrays.asList(booking, secondBooking);
        List<BookingResponseDto> responseDtos = BookingMapper.toListBookingResponseDto(bookings);

        assertNotNull(responseDtos);
        assertEquals(2, responseDtos.size());

        // Verify first booking mapping
        assertEquals(booking.getId(), responseDtos.get(0).getId());
        assertEquals(booking.getStartDate(), responseDtos.get(0).getStart());
        assertEquals(booking.getStatus(), responseDtos.get(0).getStatus());

        // Verify second booking mapping
        assertEquals(secondBooking.getId(), responseDtos.get(1).getId());
        assertEquals(secondBooking.getStartDate(), responseDtos.get(1).getStart());
        assertEquals(secondBooking.getStatus(), responseDtos.get(1).getStatus());
    }

    @Test
    void shouldHandleEmptyListInListBookingResponseDto() {
        List<Booking> emptyBookings = Arrays.asList();
        List<BookingResponseDto> responseDtos = BookingMapper.toListBookingResponseDto(emptyBookings);

        assertNotNull(responseDtos);
        assertTrue(responseDtos.isEmpty());
    }
}