package za.co.routepay.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.routepay.api.entity.Operator;

import java.util.Optional;

public interface OperatorRepository extends JpaRepository<Operator, Long> {
    Optional<Operator> findByUserId(Long userId);
}
