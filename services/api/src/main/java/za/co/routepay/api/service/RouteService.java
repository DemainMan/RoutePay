package za.co.routepay.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.routepay.api.dto.RouteResponse;
import za.co.routepay.api.entity.Route;
import za.co.routepay.api.repository.RouteRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public RouteResponse getRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found: " + id));
        return toResponse(route);
    }

    private RouteResponse toResponse(Route route) {
        return RouteResponse.builder()
                .id(route.getId())
                .name(route.getName())
                .originName(route.getOriginName())
                .destName(route.getDestName())
                .fare(route.getFare())
                .currency(route.getCurrency())
                .active(route.isActive())
                .createdAt(route.getCreatedAt())
                .build();
    }
}
