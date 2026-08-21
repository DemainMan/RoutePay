package za.co.routepay.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import za.co.routepay.api.dto.BookTripRequest;
import za.co.routepay.api.dto.TripResponse;
import za.co.routepay.api.entity.*;
import za.co.routepay.api.exception.NotFoundException;
import za.co.routepay.api.repository.*;
import za.co.routepay.momo.MoMoClient;
import za.co.routepay.momo.collections.dto.PaymentRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final MoMoClient moMoClient;
    private final SimpMessagingTemplate messagingTemplate;

    public TripResponse bookTrip(String phone, BookTripRequest request) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new NotFoundException("Route not found: " + request.getRouteId()));

        // Collect fare via MoMo
        String reference = "trip-" + UUID.randomUUID().toString().substring(0, 8);
        var paymentReq = PaymentRequest.builder()
                .amount(route.getFare())
                .currency(route.getCurrency())
                .phone(phone)
                .reference(reference)
                .payerMessage("Fare for " + route.getName())
                .build();

        var paymentResp = moMoClient.getCollections().requestToPay(paymentReq);

        // Map MoMo status to internal status
        Payment.PaymentStatus paymentStatus;
        if ("SUCCESSFUL".equals(paymentResp.getStatus())) {
            paymentStatus = Payment.PaymentStatus.SUCCESSFUL;
        } else if ("PENDING".equals(paymentResp.getStatus())) {
            paymentStatus = Payment.PaymentStatus.PENDING;
        } else {
            paymentStatus = Payment.PaymentStatus.FAILED;
        }

        // Save payment
        Payment payment = Payment.builder()
                .user(user)
                .type(Payment.PaymentType.FARE)
                .amount(route.getFare())
                .currency(route.getCurrency())
                .momoReference(paymentResp.getReferenceId())
                .momoStatus(paymentResp.getStatus())
                .status(paymentStatus)
                .description("Fare: " + route.getName())
                .build();
        paymentRepository.save(payment);

        // Only create trip if payment is successful
        if (paymentStatus != Payment.PaymentStatus.SUCCESSFUL) {
            log.warn("Payment not successful for trip booking: status={}", paymentStatus);
            return TripResponse.builder()
                    .id(null)
                    .routeId(route.getId())
                    .routeName(route.getName())
                    .status("PENDING_PAYMENT")
                    .farePaid(route.getFare())
                    .momoReference(paymentResp.getReferenceId())
                    .build();
        }

        // Save trip
        Stop boarding = request.getBoardingStopId() != null
                ? stopRepository.findById(request.getBoardingStopId()).orElse(null) : null;
        Stop alighting = request.getAlightingStopId() != null
                ? stopRepository.findById(request.getAlightingStopId()).orElse(null) : null;

        Trip trip = Trip.builder()
                .user(user)
                .route(route)
                .boardingStop(boarding)
                .alightingStop(alighting)
                .farePaid(route.getFare())
                .momoReference(paymentResp.getReferenceId())
                .status(Trip.TripStatus.BOOKED)
                .build();
        trip = tripRepository.save(trip);

        // Notify WebSocket subscribers
        TripResponse response = toResponse(trip);
        messagingTemplate.convertAndSend("/topic/trips", response);

        log.info("Trip booked: id={}, route={}, fare={}", trip.getId(), route.getName(), route.getFare());
        return response;
    }

    public List<TripResponse> getUserTrips(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return tripRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private TripResponse toResponse(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .routeId(trip.getRoute().getId())
                .routeName(trip.getRoute().getName())
                .status(trip.getStatus().name())
                .farePaid(trip.getFarePaid())
                .momoReference(trip.getMomoReference())
                .createdAt(trip.getCreatedAt())
                .build();
    }
}
