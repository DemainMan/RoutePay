package za.co.routepay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatorStatsResponse {
    private long todaysTrips;
    private double totalEarnings;
    private long activeRoutes;
    private long activePasses;
    private double tripsDelta;
    private double earningsDelta;
}
