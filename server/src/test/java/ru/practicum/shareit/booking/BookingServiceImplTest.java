package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private BookingRequestDto bookingRequestDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
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
                .owner(owner)
                .isAvailable(true)
                .build();

        bookingRequestDto = new BookingRequestDto(
                now.plusDays(1),
                now.plusDays(2),
                item.getId()
        );

        lenient().when(userService.findUserById(booker.getId())).thenReturn(CreateUserDto.builder()
                .id(booker.getId())
                .name(booker.getName())
                .email(booker.getEmail())
                .build());

        lenient().when(itemService.findItemById(item.getId())).thenReturn(ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(true)
                .owner(owner)
                .build());
    }

    @Test
    void createBooking_Success() {
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });

        BookingResponseDto result = bookingService.createBooking(bookingRequestDto, booker.getId());

        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_OwnerCannotBookOwnItem() {
        when(itemService.findItemById(item.getId())).thenReturn(ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(true)
                .owner(booker)
                .build());

        assertThrows(ConflictException.class, () ->
                bookingService.createBooking(bookingRequestDto, booker.getId()));
    }

    @Test
    void createBooking_ItemUnavailable() {
        when(itemService.findItemById(item.getId())).thenReturn(ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(false)
                .owner(owner)
                .build());

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(bookingRequestDto, booker.getId()));
    }

    @Test
    void updateBooking_ApproveSuccess() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.updateBooking(owner.getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void updateBooking_RejectSuccess() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.updateBooking(owner.getId(), booking.getId(), false);

        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void updateBooking_OwnerCannotBeBooker() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(owner)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.updateBooking(owner.getId(), booking.getId(), true));
    }

    @Test
    void findBookingById_Success() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.findBookingById(booking.getId(), booker.getId());

        assertNotNull(result);
    }

    @Test
    void findBookingById_AccessDenied() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ConflictException.class, () ->
                bookingService.findBookingById(booking.getId(), 999L));
    }

    @Test
    void findBookingByUserId_AllStates() {
        when(bookingRepository.findByBookerIdOrderByStartDateDesc(anyLong())).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.findBookingByUserId(booker.getId(), "ALL");

        assertNotNull(result);
        verify(bookingRepository).findByBookerIdOrderByStartDateDesc(anyLong());
    }

    @Test
    void findBookingByUserId_InvalidState() {
        assertThrows(ValidationException.class, () ->
                bookingService.findBookingByUserId(booker.getId(), "INVALID_STATE"));
    }

    @Test
    void validationBooking_StartAfterEnd() {
        BookingRequestDto invalidDto = new BookingRequestDto(
                now.plusDays(2),
                now.plusDays(1),
                item.getId()
                );

        assertThrows(ValidationException.class, () ->
                bookingService.validationBooking(invalidDto, item, booker));
    }

    @Test
    void findBookingByUserId_CURRENT() {
        when(bookingRepository.findCurrentBookings(anyLong(), any())).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.findBookingByUserId(booker.getId(), "CURRENT");

        assertNotNull(result);
        verify(bookingRepository).findCurrentBookings(anyLong(), any());
    }

    @Test
    void findBookingByUserId_PAST() {
        when(bookingRepository.findByBookerIdAndEndDateBeforeOrderByStartDateDesc(anyLong(), any())).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.findBookingByUserId(booker.getId(), "PAST");

        assertNotNull(result);
        verify(bookingRepository).findByBookerIdAndEndDateBeforeOrderByStartDateDesc(anyLong(), any());
    }

    @Test
    void findBookingByUserId_FUTURE() {
        when(bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(anyLong(), any())).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.findBookingByUserId(booker.getId(), "FUTURE");

        assertNotNull(result);
        verify(bookingRepository).findByBookerIdAndStartDateAfterOrderByStartDateDesc(anyLong(), any());
    }

    @Test
    void findBookingByUserId_WAITING() {
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(anyLong(), eq(Status.WAITING))).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.findBookingByUserId(booker.getId(), "WAITING");

        assertNotNull(result);
        verify(bookingRepository).findByBookerIdAndStatusOrderByStartDateDesc(anyLong(), eq(Status.WAITING));
    }

    @Test
    void findBookingByUserId_REJECTED() {
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(anyLong(), eq(Status.REJECTED))).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.findBookingByUserId(booker.getId(), "REJECTED");

        assertNotNull(result);
        verify(bookingRepository).findByBookerIdAndStatusOrderByStartDateDesc(anyLong(), eq(Status.REJECTED));
    }

    @Test
    void validationBooking_EndDateBeforeStartDate() {
        BookingRequestDto invalidDto = new BookingRequestDto(
                now.plusDays(2),
                now.plusDays(1),
                item.getId()
        );

        assertThrows(ValidationException.class, () ->
                bookingService.validationBooking(invalidDto, item, booker));
    }

    @Test
    void validationBooking_OwnerIsBooker() {
        Item ownedItem = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .owner(booker)
                .isAvailable(true)
                .build();

        assertThrows(ConflictException.class, () ->
                bookingService.validationBooking(bookingRequestDto, ownedItem, booker));
    }

    @Test
    void validationBooking_ItemUnavailable() {
        Item unavailableItem = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .owner(owner)
                .isAvailable(false)
                .build();

        assertThrows(ValidationException.class, () ->
                bookingService.validationBooking(bookingRequestDto, unavailableItem, booker));
    }
}