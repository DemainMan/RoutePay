package za.co.routepay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyEarningsResponse {
    private java.util.List<DayEarnings> days;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayEarnings {
        private String date;
        private double trips;
        private double passes;
        private double total;
    }
}
