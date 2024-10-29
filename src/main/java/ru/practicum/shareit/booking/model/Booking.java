package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Transient
    public boolean isLastOrCurrent(LocalDateTime now) {
        return status == status.APPROVED
                && ((endDate.isEqual(now) || endDate.isBefore(now))
                || (startDate.isEqual(now) || startDate.isBefore(now)));
    }

    @Transient
    public boolean isFuture(LocalDateTime now) {
        return status == status.APPROVED && startDate.isAfter(now);
    }

    @Transient
    public boolean isFinished(LocalDateTime now) {
        return getStatus() == status.APPROVED && getEndDate().isBefore(now);
    }
}
