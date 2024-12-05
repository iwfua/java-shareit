package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        booker = new User();
        booker.setName("Тестовый пользователь");
        booker.setEmail("test@example.com");
        entityManager.persist(booker);

        item = new Item();
        item.setName("Тестовый предмет");
        item.setDescription("Описание предмета");
        item.setOwner(owner);
        item.setIsAvailable(true);
        entityManager.persist(item);

        booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setStartDate(LocalDateTime.now().minusDays(1));
        booking.setEndDate(LocalDateTime.now().plusDays(1));
        entityManager.persist(booking);

        entityManager.flush();
    }

    @Test
    void findByBookerIdOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDateDesc(booker.getId());

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findCurrentBookings() {
        List<Booking> currentBookings = bookingRepository.findCurrentBookings(
                booker.getId(),
                LocalDateTime.now()
        );

        assertFalse(currentBookings.isEmpty());
        assertEquals(1, currentBookings.size());
        assertEquals(booking.getId(), currentBookings.get(0).getId());
    }

    @Test
    void findByBookerIdAndEndDateBeforeOrderByStartDateDesc() {
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        entityManager.persist(booking);
        entityManager.flush();

        List<Booking> pastBookings = bookingRepository.findByBookerIdAndEndDateBeforeOrderByStartDateDesc(
                booker.getId(),
                LocalDateTime.now()
        );

        assertFalse(pastBookings.isEmpty());
        assertEquals(1, pastBookings.size());
    }

    @Test
    void findByBookerIdAndStartDateAfterOrderByStartDateDesc() {
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        entityManager.persist(booking);
        entityManager.flush();

        List<Booking> futureBookings = bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(
                booker.getId(),
                LocalDateTime.now()
        );

        assertFalse(futureBookings.isEmpty());
        assertEquals(1, futureBookings.size());
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDateDesc() {
        List<Booking> bookingsByStatus = bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(
                booker.getId(),
                Status.APPROVED
        );

        assertFalse(bookingsByStatus.isEmpty());
        assertEquals(1, bookingsByStatus.size());
        assertEquals(Status.APPROVED, bookingsByStatus.get(0).getStatus());
    }

    @Test
    void existsByBookerIdAndItemIdAndEndDateBeforeAndStatus() {
        boolean exists = bookingRepository.existsByBookerIdAndItemIdAndEndDateBeforeAndStatus(
                booker.getId(),
                item.getId(),
                LocalDateTime.now().plusDays(2),
                Status.APPROVED
        );

        assertTrue(exists);
    }
}