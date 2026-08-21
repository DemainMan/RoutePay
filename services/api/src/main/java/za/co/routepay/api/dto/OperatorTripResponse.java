package za.co.routepay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatorTripResponse {
    private long id;
    private String commuterPhone;
    private String routeName;
    private String status;
    private double fare;
    private String timestamp;
}
