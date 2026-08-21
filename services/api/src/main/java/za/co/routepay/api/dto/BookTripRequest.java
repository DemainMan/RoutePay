package za.co.routepay.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookTripRequest {

    @NotNull(message = "Route ID is required")
    private Long routeId;

    private Long boardingStopId;
    private Long alightingStopId;
}
