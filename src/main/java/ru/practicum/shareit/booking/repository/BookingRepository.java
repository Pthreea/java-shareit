package ru.practicum.shareit.booking.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId,
                                        @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByBookerId(@Param("bookerId") Long bookerId,
                                     @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByBookerId(@Param("bookerId") Long bookerId,
                                       @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId,
                                       @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId,
                                      @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                         @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.start <= :now " +
            "AND b.status = :status " +
            "ORDER BY b.end DESC " +
            "LIMIT 1")
    Optional<Booking> findLastBookingByItemId(
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now,
            @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.start > :now " +
            "AND b.status = :status " +
            "ORDER BY b.start ASC " +
            "LIMIT 1")
    Optional<Booking> findNextBookingByItemId(
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now,
            @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.status = :status")
    List<Booking> findByItemIdInAndStatus(
            @Param("itemIds") List<Long> itemIds,
            @Param("status") BookingStatus status);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :bookerId " +
            "AND b.end < :now " +
            "AND b.status = :status")
    boolean existsPastBooking(
            @Param("itemId") Long itemId,
            @Param("bookerId") Long bookerId,
            @Param("now") LocalDateTime now,
            @Param("status") BookingStatus status);

}
