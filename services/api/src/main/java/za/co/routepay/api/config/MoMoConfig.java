package za.co.routepay.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import za.co.routepay.momo.MoMoClient;
import za.co.routepay.momo.config.MoMoEnvironment;

@Configuration
public class MoMoConfig {

    @Value("${momo.environment:MOCK}")
    private String environment;

    @Value("${momo.subscription-key:mock-subscription-key}")
    private String subscriptionKey;

    @Value("${momo.api-user:}")
    private String apiUser;

    @Value("${momo.api-key:}")
    private String apiKey;

    @Value("${momo.primary-key:}")
    private String primaryKey;

    @Value("${momo.callback-url:}")
    private String callbackUrl;

    @Bean
    public MoMoClient moMoClient() {
        return MoMoClient.builder()
                .environment(MoMoEnvironment.valueOf(environment))
                .subscriptionKey(subscriptionKey)
                .apiUser(apiUser.isEmpty() ? null : apiUser)
                .apiKey(apiKey.isEmpty() ? null : apiKey)
                .primaryKey(primaryKey.isEmpty() ? null : primaryKey)
                .callbackUrl(callbackUrl.isEmpty() ? null : callbackUrl)
                .build();
    }
}
