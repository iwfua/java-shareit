package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    List<Booking> findByBookerIdOrderByStartDateDesc(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.startDate < :now " +
            "AND b.endDate > :now " +
            "ORDER BY b.startDate DESC")
    List<Booking> findCurrentBookings(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );

    // Поиск завершенных бронирований
    List<Booking> findByBookerIdAndEndDateBeforeOrderByStartDateDesc(
            Long bookerId,
            LocalDateTime end
    );

    // Поиск будущих бронирований
    List<Booking> findByBookerIdAndStartDateAfterOrderByStartDateDesc(
            Long bookerId,
            LocalDateTime start
    );

    // Поиск бронирований по статусу
    List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(
            Long bookerId,
            Status status
    );

    boolean existsByBookerIdAndItemIdAndEndDateBeforeAndStatus(
            Long userId,
            Long itemId,
            LocalDateTime end,
            Status status
        );
}
