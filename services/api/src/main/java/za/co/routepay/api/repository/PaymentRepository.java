package za.co.routepay.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.routepay.api.entity.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
}
