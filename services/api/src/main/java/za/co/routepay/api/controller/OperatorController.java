package za.co.routepay.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.routepay.api.dto.*;
import za.co.routepay.api.service.OperatorService;

@RestController
@RequestMapping("/api/operator")
@RequiredArgsConstructor
@Tag(name = "Operator", description = "Operator dashboard data")
public class OperatorController {

    private final OperatorService operatorService;

    @GetMapping("/stats")
    @Operation(summary = "Get operator dashboard stats")
    public ResponseEntity<OperatorStatsResponse> getStats() {
        return ResponseEntity.ok(operatorService.getStats());
    }

    @GetMapping("/trips")
    @Operation(summary = "Get all trips for operator dashboard")
    public ResponseEntity<?> getTrips() {
        return ResponseEntity.ok(operatorService.getTrips());
    }

    @GetMapping("/earnings")
    @Operation(summary = "Get earnings breakdown by day")
    public ResponseEntity<DailyEarningsResponse> getEarnings(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(operatorService.getEarnings(days));
    }
}
