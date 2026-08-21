package za.co.routepay.momo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import za.co.routepay.momo.config.MoMoEnvironment;
import za.co.routepay.momo.config.MoMoProperties;
import za.co.routepay.momo.mock.MockMoMoBackend;

import static org.assertj.core.api.Assertions.assertThat;

class MoMoClientTest {

    @Nested
    @DisplayName("Builder pattern")
    class Builder {

        @Test
        @DisplayName("creates client with default MOCK environment")
        void createsClientWithMockDefault() {
            MoMoClient client = MoMoClient.builder().build();

            assertThat(client.getProperties().getEnvironment()).isEqualTo(MoMoEnvironment.MOCK);
            assertThat(client.getProperties().getBaseUrl()).isEqualTo("http://localhost:8099");
        }

        @Test
        @DisplayName("creates client with SANDBOX environment")
        void createsClientWithSandbox() {
            MoMoClient client = MoMoClient.builder()
                    .environment(MoMoEnvironment.SANDBOX)
                    .subscriptionKey("test-key")
                    .build();

            assertThat(client.getProperties().getEnvironment()).isEqualTo(MoMoEnvironment.SANDBOX);
            assertThat(client.getProperties().getBaseUrl()).isEqualTo("https://sandbox.momodeveloper.mtn.com");
            assertThat(client.getProperties().getSubscriptionKey()).isEqualTo("test-key");
        }

        @Test
        @DisplayName("creates client with PRODUCTION environment")
        void createsClientWithProduction() {
            MoMoClient client = MoMoClient.builder()
                    .environment(MoMoEnvironment.PRODUCTION)
                    .subscriptionKey("prod-key")
                    .apiUser("user-uuid")
                    .apiKey("api-key-value")
                    .primaryKey("primary-key")
                    .build();

            MoMoProperties props = client.getProperties();
            assertThat(props.getEnvironment()).isEqualTo(MoMoEnvironment.PRODUCTION);
            assertThat(props.getBaseUrl()).isEqualTo("https://proxy.momoapi.mtn.com");
            assertThat(props.getApiUser()).isEqualTo("user-uuid");
            assertThat(props.getApiKey()).isEqualTo("api-key-value");
            assertThat(props.getPrimaryKey()).isEqualTo("primary-key");
        }

        @Test
        @DisplayName("allows custom base URL override")
        void allowsCustomBaseUrl() {
            MoMoClient client = MoMoClient.builder()
                    .baseUrl("http://localhost:9999")
                    .build();

            assertThat(client.getProperties().getBaseUrl()).isEqualTo("http://localhost:9999");
        }

        @Test
        @DisplayName("exposes all sub-clients")
        void exposesAllSubClients() {
            MoMoClient client = MoMoClient.builder().build();

            assertThat(client.getCollections()).isNotNull();
            assertThat(client.getDisbursements()).isNotNull();
            assertThat(client.getRemittances()).isNotNull();
            assertThat(client.getPayments()).isNotNull();
            assertThat(client.getAuth()).isNotNull();
            assertThat(client.getMockBackend()).isNotNull();
        }

        @Test
        @DisplayName("builder methods are fluent")
        void builderMethodsAreFluent() {
            MoMoClient.Builder builder = MoMoClient.builder();

            MoMoClient.Builder result = builder
                    .environment(MoMoEnvironment.SANDBOX)
                    .subscriptionKey("key")
                    .apiUser("user")
                    .apiKey("key")
                    .primaryKey("pk")
                    .callbackUrl("https://example.com/cb")
                    .baseUrl("https://custom.url");

            assertThat(result).isSameAs(builder);
        }
    }
}
