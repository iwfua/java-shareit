package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(OWNER_ID) Long bookerId,
                                            @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Пришел POST-запрос на создание бронирования с bookerId={} и телом bookingRequestDto={}", bookerId, bookingRequestDto);
        BookingResponseDto response = bookingService.createBooking(bookingRequestDto, bookerId);
        log.info("Отправлен POST-ответ с телом bookingResponseDto={}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@RequestHeader(OWNER_ID) Long ownerId,
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        log.info("Пришел PATCH-запрос на обновление бронирования с ownerId={}, bookingId={}, approved={}", ownerId, bookingId, approved);
        BookingResponseDto response = bookingService.updateBooking(ownerId, bookingId, approved);
        log.info("Отправлен PATCH-ответ с телом bookingResponseDto={}", response);
        return response;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBookingById(@PathVariable(name = "bookingId") Long bookingId,
                                              @RequestHeader(OWNER_ID) Long userId) {
        log.info("Пришел GET-запрос на получение бронирования с bookingId={} и userId={}", bookingId, userId);
        BookingResponseDto response = bookingService.findBookingById(bookingId, userId);
        log.info("Отправлен GET-ответ с телом bookingResponseDto={}", response);
        return response;
    }

    @GetMapping
    public List<BookingResponseDto> findBookingsByUser(@RequestHeader(OWNER_ID) Long ownerId,
                                                       @RequestParam(name = "state", defaultValue = "ALL", required = false) String state) {
        log.info("Пришел GET-запрос на получение бронирований пользователя с ownerId={} и состоянием state={}", ownerId, state);
        List<BookingResponseDto> response = bookingService.findBookingByUserId(ownerId, state);
        log.info("Отправлен GET-ответ с телом bookingResponseDtos={}", response);
        return response;
    }
}