package za.co.routepay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {

    private Long id;
    private String name;
    private String originName;
    private String destName;
    private BigDecimal fare;
    private String currency;
    private boolean active;
    private Instant createdAt;
}
