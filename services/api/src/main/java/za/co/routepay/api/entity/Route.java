package za.co.routepay.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "routes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "origin_name", nullable = false, length = 100)
    private String originName;

    @Column(name = "origin_lat", nullable = false)
    private BigDecimal originLat;

    @Column(name = "origin_lng", nullable = false)
    private BigDecimal originLng;

    @Column(name = "dest_name", nullable = false, length = 100)
    private String destName;

    @Column(name = "dest_lat", nullable = false)
    private BigDecimal destLat;

    @Column(name = "dest_lng", nullable = false)
    private BigDecimal destLng;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fare;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "ZAR";

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}
