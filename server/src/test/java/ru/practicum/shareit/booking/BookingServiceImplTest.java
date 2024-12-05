package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .id(1L)
                .name("Booker")
                .email("booker@example.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .isAvailable(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void updateBooking_Approve_Success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.updateBooking(owner.getId(), booking.getId(), true);

        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBooking_Reject_Success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.updateBooking(owner.getId(), booking.getId(), false);

        assertNotNull(result);
        assertEquals(Status.REJECTED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBooking_BookerTriesToUpdate_ShouldThrowValidationException() {

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.updateBooking(booker.getId(), booking.getId(), true)
        );
    }

    @Test
    void updateBooking_NonOwnerTriesToUpdate_ShouldThrowValidationException() {

        User anotherUser = User.builder().id(3L).build();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.updateBooking(anotherUser.getId(), booking.getId(), true)
        );
    }

    @Test
    void findBookingById_OwnerAccess_Success() {

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.findBookingById(booking.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void findBookingById_BookerAccess_Success() {

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.findBookingById(booking.getId(), booker.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void findBookingById_UnauthorizedAccess_ShouldThrowConflictException() {

        User unauthorizedUser = User.builder().id(3L).build();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ConflictException.class, () ->
                bookingService.findBookingById(booking.getId(), unauthorizedUser.getId())
        );
    }

    @Test
    void findBookingById_BookingNotFound_ShouldThrowNotFoundException() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.findBookingById(999L, booker.getId())
        );
    }

    @Test
    void findBookingByUserId_AllState_Success() {

        when(bookingRepository.findByBookerIdOrderByStartDateDesc(booker.getId()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> results = bookingService.findBookingByUserId(booker.getId(), "ALL");

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    void findBookingByUserId_InvalidState_ShouldThrowValidationException() {
        assertThrows(ValidationException.class, () ->
                bookingService.findBookingByUserId(booker.getId(), "INVALID_STATE")
        );
    }

    @Test
    void validation_EndDateBeforeStartDate_ShouldThrowValidationException() {
        BookingRequestDto invalidDto = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        User mockUser = mock(User.class);
        Item mockItem = mock(Item.class);

        assertThrows(ValidationException.class, () ->
                bookingService.validationBooking(invalidDto, mockItem, mockUser)
        );
    }
}