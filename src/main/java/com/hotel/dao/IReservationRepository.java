package com.hotel.dao;

import com.hotel.model.Reservation;
import java.time.LocalDate;
import java.util.List;



public interface IReservationRepository {
    void save(Reservation res);
    void update(Reservation res);
    void delete(String id);
    Reservation findById(String id);
    List<Reservation> findAll();
    List<Reservation> findByGuestId(String guestId);
    List<Reservation> findByRoomNumber(String roomNumber);
    List<Reservation> findByStatus(Reservation.Status status);
    List<Reservation> findActive();
    boolean checkDateOverlap(String roomNumber, LocalDate checkIn, LocalDate checkOut, String excludeId);
    boolean checkDuplicateGuestRoomBooking(String guestId, String roomNumber, LocalDate checkIn, LocalDate checkOut, String excludeId);
    long countActiveByGuest(String guestId);
}




abstract class ReservationRepository implements IReservationRepository {


    protected boolean isValidDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) return false;
        return checkOut.isAfter(checkIn);
    }


    protected boolean isModifiable(Reservation res) {
        if (res == null) return false;
        return res.getStatus() != Reservation.Status.CANCELLED
                && res.getStatus() != Reservation.Status.CHECKED_OUT;
    }

    // Interface methods ඔක්කොම abstract — ReservationDAO @Override කරනවා
    @Override public abstract void save(Reservation res);
    @Override public abstract void update(Reservation res);
    @Override public abstract void delete(String id);
    @Override public abstract Reservation findById(String id);
    @Override public abstract List<Reservation> findAll();
    @Override public abstract List<Reservation> findByGuestId(String guestId);
    @Override public abstract List<Reservation> findByRoomNumber(String roomNumber);
    @Override public abstract List<Reservation> findByStatus(Reservation.Status status);
    @Override public abstract List<Reservation> findActive();
    @Override public abstract boolean checkDateOverlap(String roomNumber, LocalDate checkIn, LocalDate checkOut, String excludeId);
    @Override public abstract boolean checkDuplicateGuestRoomBooking(String guestId, String roomNumber, LocalDate checkIn, LocalDate checkOut, String excludeId);
    @Override public abstract long countActiveByGuest(String guestId);
}
