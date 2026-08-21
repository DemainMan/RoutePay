package za.co.routepay.momo.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoMoPropertiesTest {

    @Test
    @DisplayName("builder creates properties with MOCK as default environment")
    void builderUsesMockDefault() {
        MoMoProperties props = MoMoProperties.builder().build();

        assertThat(props.getEnvironment()).isEqualTo(MoMoEnvironment.MOCK);
        assertThat(props.getSubscriptionKey()).isNull();
        assertThat(props.getApiUser()).isNull();
        assertThat(props.getApiKey()).isNull();
    }

    @Test
    @DisplayName("all-args constructor sets all fields")
    void allArgsConstructorSetsAllFields() {
        MoMoProperties props = new MoMoProperties(
                MoMoEnvironment.SANDBOX,
                "sub-key",
                "api-user",
                "api-key",
                "primary-key",
                "https://example.com/cb",
                "https://custom.url"
        );

        assertThat(props.getEnvironment()).isEqualTo(MoMoEnvironment.SANDBOX);
        assertThat(props.getSubscriptionKey()).isEqualTo("sub-key");
        assertThat(props.getApiUser()).isEqualTo("api-user");
        assertThat(props.getApiKey()).isEqualTo("api-key");
        assertThat(props.getPrimaryKey()).isEqualTo("primary-key");
        assertThat(props.getCallbackUrl()).isEqualTo("https://example.com/cb");
        assertThat(props.getBaseUrl()).isEqualTo("https://custom.url");
    }

    @Test
    @DisplayName("setters work correctly")
    void settersWorkCorrectly() {
        MoMoProperties props = new MoMoProperties();
        props.setEnvironment(MoMoEnvironment.PRODUCTION);
        props.setSubscriptionKey("key");
        props.setBaseUrl("http://localhost:8080");

        assertThat(props.getEnvironment()).isEqualTo(MoMoEnvironment.PRODUCTION);
        assertThat(props.getSubscriptionKey()).isEqualTo("key");
        assertThat(props.getBaseUrl()).isEqualTo("http://localhost:8080");
    }
}
