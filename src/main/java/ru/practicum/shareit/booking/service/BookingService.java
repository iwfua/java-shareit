package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId);

    BookingResponseDto updateBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingResponseDto findBookingById(Long bookingId, Long ownerId);

    List<BookingResponseDto> findBookingByUserId(Long ownerId, String state);

}
