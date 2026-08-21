package za.co.routepay.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.routepay.api.entity.Trip;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Trip> findByStatus(Trip.TripStatus status);
}
