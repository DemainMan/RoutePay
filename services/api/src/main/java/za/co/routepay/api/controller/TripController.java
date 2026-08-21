package za.co.routepay.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import za.co.routepay.api.dto.BookTripRequest;
import za.co.routepay.api.dto.TripResponse;
import za.co.routepay.api.service.TripService;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "Book and manage taxi trips")
public class TripController {

    private final TripService tripService;

    @PostMapping
    @Operation(summary = "Book a trip (collects fare via MoMo)")
    public ResponseEntity<TripResponse> bookTrip(
            Authentication auth,
            @Valid @RequestBody BookTripRequest request) {
        String phone = (String) auth.getPrincipal();
        return ResponseEntity.ok(tripService.bookTrip(phone, request));
    }

    @GetMapping
    @Operation(summary = "Get user's trip history")
    public ResponseEntity<List<TripResponse>> getMyTrips(Authentication auth) {
        String phone = (String) auth.getPrincipal();
        return ResponseEntity.ok(tripService.getUserTrips(phone));
    }
}
