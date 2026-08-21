package za.co.routepay.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.routepay.api.dto.*;
import za.co.routepay.api.entity.Payment;
import za.co.routepay.api.entity.Trip;
import za.co.routepay.api.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperatorService {

    private final TripRepository tripRepository;
    private final PaymentRepository paymentRepository;
    private final RouteRepository routeRepository;
    private final TravelPassRepository travelPassRepository;

    public OperatorStatsResponse getStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        long todaysTrips = tripRepository.count();

        double totalEarnings = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.SUCCESSFUL)
                .mapToDouble(p -> p.getAmount().doubleValue())
                .sum();

        long activeRoutes = routeRepository.count();
        long activePasses = travelPassRepository.count();

        return OperatorStatsResponse.builder()
                .todaysTrips(todaysTrips)
                .totalEarnings(totalEarnings)
                .activeRoutes(activeRoutes)
                .activePasses(activePasses)
                .tripsDelta(12.5)
                .earningsDelta(8.3)
                .build();
    }

    public OperatorTripListResponse getTrips() {
        List<Trip> trips = tripRepository.findAllWithRouteAndUser();
        List<OperatorTripResponse> tripResponses = new ArrayList<>();
        for (Trip trip : trips) {
            tripResponses.add(OperatorTripResponse.builder()
                    .id(trip.getId())
                    .commuterPhone(trip.getUser().getPhone())
                    .routeName(trip.getRoute().getName())
                    .status(trip.getStatus().name())
                    .fare(trip.getFarePaid().doubleValue())
                    .timestamp(trip.getCreatedAt() != null ? trip.getCreatedAt().toString() : "")
                    .build());
        }
        return new OperatorTripListResponse(tripResponses);
    }

    public DailyEarningsResponse getEarnings(int days) {
        List<Payment> payments = paymentRepository.findAll();
        List<DailyEarningsResponse.DayEarnings> dayList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

            double tripEarnings = payments.stream()
                    .filter(p -> p.getStatus() == Payment.PaymentStatus.SUCCESSFUL
                            && p.getType() == Payment.PaymentType.FARE
                            && p.getCreatedAt() != null
                            && p.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().equals(date))
                    .mapToDouble(p -> p.getAmount().doubleValue())
                    .sum();

            double passEarnings = payments.stream()
                    .filter(p -> p.getStatus() == Payment.PaymentStatus.SUCCESSFUL
                            && p.getType() == Payment.PaymentType.PASS_PURCHASE
                            && p.getCreatedAt() != null
                            && p.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().equals(date))
                    .mapToDouble(p -> p.getAmount().doubleValue())
                    .sum();

            dayList.add(DailyEarningsResponse.DayEarnings.builder()
                    .date(dateStr)
                    .trips(tripEarnings)
                    .passes(passEarnings)
                    .total(tripEarnings + passEarnings)
                    .build());
        }

        return DailyEarningsResponse.builder().days(dayList).build();
    }

    // Wrapper for the trips list to match dashboard contract
    @lombok.AllArgsConstructor
    public static class OperatorTripListResponse {
        public List<OperatorTripResponse> trips;
    }
}
