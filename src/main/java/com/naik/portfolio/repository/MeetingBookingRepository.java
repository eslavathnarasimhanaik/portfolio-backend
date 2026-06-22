package com.naik.portfolio.repository;

import com.naik.portfolio.model.MeetingBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingBookingRepository extends JpaRepository<MeetingBooking, Long> {
    List<MeetingBooking> findByEmail(String email);
    List<MeetingBooking> findByStatus(String status);
}
