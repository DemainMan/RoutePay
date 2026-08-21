package za.co.routepay.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "ZAR";

    @Column(name = "momo_reference", length = 100)
    private String momoReference;

    @Column(name = "momo_status", length = 30)
    private String momoStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(length = 255)
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    public enum PaymentType {
        FARE, PASS_PURCHASE, PASS_TOPUP, PAYOUT
    }

    public enum PaymentStatus {
        PENDING, SUCCESSFUL, FAILED, REFUNDED
    }
}
