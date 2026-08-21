package za.co.routepay.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.routepay.api.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
