package za.co.routepay.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.routepay.api.entity.TravelPass;

import java.time.LocalDate;
import java.util.List;

public interface TravelPassRepository extends JpaRepository<TravelPass, Long> {
    List<TravelPass> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, TravelPass.PassStatus status);
    List<TravelPass> findByUserIdAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
            Long userId, LocalDate from, LocalDate until);
}
