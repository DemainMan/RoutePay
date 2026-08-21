package za.co.routepay.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.routepay.api.entity.Route;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByActiveTrue();
}
