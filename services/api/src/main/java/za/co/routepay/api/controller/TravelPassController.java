package za.co.routepay.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import za.co.routepay.api.dto.PurchasePassRequest;
import za.co.routepay.api.dto.TravelPassResponse;
import za.co.routepay.api.service.TravelPassService;

import java.util.List;

@RestController
@RequestMapping("/api/passes")
@RequiredArgsConstructor
@Tag(name = "Travel Passes", description = "Purchase daily/weekly/monthly travel passes")
public class TravelPassController {

    private final TravelPassService passService;

    @PostMapping
    @Operation(summary = "Purchase a travel pass (charged via MoMo Payments)")
    public ResponseEntity<TravelPassResponse> purchasePass(
            Authentication auth,
            @Valid @RequestBody PurchasePassRequest request) {
        String phone = (String) auth.getPrincipal();
        return ResponseEntity.ok(passService.purchasePass(phone, request));
    }

    @GetMapping
    @Operation(summary = "Get user's active travel passes")
    public ResponseEntity<List<TravelPassResponse>> getMyPasses(Authentication auth) {
        String phone = (String) auth.getPrincipal();
        return ResponseEntity.ok(passService.getUserPasses(phone));
    }
}
