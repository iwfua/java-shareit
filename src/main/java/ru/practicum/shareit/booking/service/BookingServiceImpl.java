package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId) {
        log.info("Создание бронирования с bookerId={} и bookingRequestDto={}", bookerId, bookingRequestDto);

        User user = UserMapper.fromDto(userService.findUserById(bookerId));
        Item item = ItemMapper.fromDto(itemService.findItemById(bookingRequestDto.getItemId()));

        validationBooking(bookingRequestDto, item, user);

        Booking booking = Booking.builder()
                .startDate(bookingRequestDto.getStartDate())
                .endDate(bookingRequestDto.getEndDate())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        BookingResponseDto response = BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
        log.info("Бронирование создано: bookingResponseDto={}", response);
        return response;
    }

    @Override
    @Transactional
    public BookingResponseDto updateBooking(Long ownerId, Long bookingId, Boolean approved) {
        log.info("Обновление бронирования: ownerId={}, bookingId={}, approved={}", ownerId, bookingId, approved);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Бронирование с id={} не найдено", bookingId);
                    throw new NotFoundException("Booking not found");
                });

        if (booking.getBooker().getId().equals(ownerId)) {
            log.error("Ошибка валидации: владелец не может быть одновременно и пользователем, ownerId={}", ownerId);
            throw new ValidationException("Owner cannot be a booker");
        }

        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            log.error("Ошибка валидации: некорректный владелец с ownerId={}", ownerId);
            throw new ValidationException("OwnerId is incorrect");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        BookingResponseDto response = BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
        log.info("Обновление завершено: bookingResponseDto={}", response);
        return response;
    }

    @Override
    @Transactional
    public BookingResponseDto findBookingById(Long bookingId, Long userId) {
        log.info("Получение бронирования с bookingId={} для userId={}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Бронирование с id={} не найдено", bookingId);
                    throw new NotFoundException("Booking not found");
                });

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            log.error("Конфликт прав доступа: userId={} не является владельцем или автором бронирования", userId);
            throw new ConflictException("Only the owner or the booker can view this booking");
        }

        BookingResponseDto response = BookingMapper.toBookingResponseDto(booking);
        log.info("Бронирование получено: bookingResponseDto={}", response);
        return response;
    }

    public List<BookingResponseDto> findBookingByUserId(Long userId, String state) {
        log.info("Получение списка бронирований для userId={} со статусом state={}", userId, state);
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case "ALL" -> {
                List<BookingResponseDto> bookings = BookingMapper.toListBookingResponseDto(
                        bookingRepository.findByBookerIdOrderByStartDateDesc(userId));
                log.info("Найдено {} бронирований для userId={}", bookings.size(), userId);
                yield bookings;
            }

            case "CURRENT" -> {
                List<BookingResponseDto> bookings = BookingMapper.toListBookingResponseDto(
                        bookingRepository.findCurrentBookings(userId, now));
                log.info("Найдено {} текущих бронирований для userId={}", bookings.size(), userId);
                yield bookings;
            }

            case "PAST" -> {
                List<BookingResponseDto> bookings = BookingMapper.toListBookingResponseDto(
                        bookingRepository.findByBookerIdAndEndDateBeforeOrderByStartDateDesc(userId, now));
                log.info("Найдено {} завершенных бронирований для userId={}", bookings.size(), userId);
                yield bookings;
            }

            case "FUTURE" -> {
                List<BookingResponseDto> bookings = BookingMapper.toListBookingResponseDto(
                        bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(userId, now));
                log.info("Найдено {} будущих бронирований для userId={}", bookings.size(), userId);
                yield bookings;
            }

            case "WAITING" -> {
                List<BookingResponseDto> bookings = BookingMapper.toListBookingResponseDto(
                        bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(userId, Status.WAITING));
                log.info("Найдено {} ожидающих подтверждения бронирований для userId={}", bookings.size(), userId);
                yield bookings;
            }

            case "REJECTED" -> {
                List<BookingResponseDto> bookings = BookingMapper.toListBookingResponseDto(
                        bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(userId, Status.REJECTED));
                log.info("Найдено {} отклоненных бронирований для userId={}", bookings.size(), userId);
                yield bookings;
            }

            default -> {
                log.error("Некорректное состояние бронирования: state={}", state);
                throw new ValidationException("State is incorrect");
            }
        };
    }

    public void validationBooking(BookingRequestDto bookingRequestDto, Item item, User booker) {
        log.info("Валидация бронирования с bookingRequestDto={}, item={}, booker={}", bookingRequestDto, item, booker);

        if (bookingRequestDto.getStartDate().isAfter(bookingRequestDto.getEndDate())) {
            log.error("Ошибка валидации: дата начала позже даты окончания, bookingRequestDto={}", bookingRequestDto);
            throw new ValidationException("Start date cannot be after end date");
        }

        if (bookingRequestDto.getEndDate().isBefore(bookingRequestDto.getStartDate())) {
            log.error("Ошибка валидации: дата окончания раньше даты начала, bookingRequestDto={}", bookingRequestDto);
            throw new ValidationException("End date cannot be before start date");
        }

        if (item.getOwnerId().equals(booker.getId())) {
            log.error("Ошибка валидации: владелец не может бронировать свою вещь, itemId={}, bookerId={}", item.getId(), booker.getId());
            throw new ConflictException("Owner cannot be booker");
        }

        if (!item.getIsAvailable()) {
            log.error("Ошибка валидации: предмет недоступен для бронирования, itemId={}", item.getId());
            throw new ValidationException("Item is not available");
        }
    }
}