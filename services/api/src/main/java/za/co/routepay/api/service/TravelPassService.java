package za.co.routepay.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.routepay.api.dto.PurchasePassRequest;
import za.co.routepay.api.dto.TravelPassResponse;
import za.co.routepay.api.entity.TravelPass;
import za.co.routepay.api.entity.User;
import za.co.routepay.api.repository.TravelPassRepository;
import za.co.routepay.api.repository.UserRepository;
import za.co.routepay.momo.MoMoClient;
import za.co.routepay.momo.payments.dto.PaymentRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelPassService {

    private final TravelPassRepository passRepository;
    private final UserRepository userRepository;
    private final MoMoClient moMoClient;

    private static final BigDecimal DAILY_PRICE = new BigDecimal("25.00");
    private static final BigDecimal WEEKLY_PRICE = new BigDecimal("99.00");
    private static final BigDecimal MONTHLY_PRICE = new BigDecimal("350.00");

    public TravelPassResponse purchasePass(String phone, PurchasePassRequest request) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new za.co.routepay.api.exception.NotFoundException("User not found"));

        TravelPass.PassType passType = TravelPass.PassType.valueOf(request.getPassType());
        BigDecimal price = switch (passType) {
            case DAILY -> DAILY_PRICE;
            case WEEKLY -> WEEKLY_PRICE;
            case MONTHLY -> MONTHLY_PRICE;
        };

        // Pay via MoMo Payments API
        String reference = "pass-" + UUID.randomUUID().toString().substring(0, 8);
        var paymentReq = PaymentRequest.builder()
                .amount(price)
                .currency("ZAR")
                .phone(phone)
                .reference(reference)
                .productType(passType.name())
                .payerMessage("Purchase " + passType.name().toLowerCase() + " travel pass")
                .build();

        var paymentResp = moMoClient.getPayments().requestPayment(paymentReq);

        // Calculate validity
        LocalDate today = LocalDate.now();
        LocalDate validUntil = switch (passType) {
            case DAILY -> today.plusDays(1);
            case WEEKLY -> today.plusWeeks(1);
            case MONTHLY -> today.plusMonths(1);
        };

        // Save pass
        TravelPass pass = TravelPass.builder()
                .user(user)
                .passType(passType)
                .validFrom(today)
                .validUntil(validUntil)
                .pricePaid(price)
                .momoReference(paymentResp.getReferenceId())
                .status(TravelPass.PassStatus.ACTIVE)
                .build();
        pass = passRepository.save(pass);

        log.info("Pass purchased: id={}, type={}, user={}", pass.getId(), passType, phone);
        return toResponse(pass);
    }

    public List<TravelPassResponse> getUserPasses(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new za.co.routepay.api.exception.NotFoundException("User not found"));
        return passRepository.findByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), TravelPass.PassStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TravelPassResponse toResponse(TravelPass pass) {
        return TravelPassResponse.builder()
                .id(pass.getId())
                .passType(pass.getPassType().name())
                .validFrom(pass.getValidFrom())
                .validUntil(pass.getValidUntil())
                .pricePaid(pass.getPricePaid())
                .status(pass.getStatus().name())
                .momoReference(pass.getMomoReference())
                .build();
    }
}
