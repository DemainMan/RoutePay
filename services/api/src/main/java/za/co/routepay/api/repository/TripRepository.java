package za.co.routepay.api.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.routepay.api.entity.Trip;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    @EntityGraph(attributePaths = {"route"})
    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT t FROM Trip t JOIN FETCH t.route JOIN FETCH t.user")
    List<Trip> findAllWithRouteAndUser();

    List<Trip> findByStatus(Trip.TripStatus status);
}
