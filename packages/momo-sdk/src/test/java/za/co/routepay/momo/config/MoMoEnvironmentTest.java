package za.co.routepay.momo.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoMoEnvironmentTest {

    @Test
    @DisplayName("MOCK has localhost base URL")
    void mockHasLocalhostUrl() {
        assertThat(MoMoEnvironment.MOCK.getBaseUrl()).isEqualTo("http://localhost:8099");
    }

    @Test
    @DisplayName("SANDBOX has sandbox base URL")
    void sandboxHasSandboxUrl() {
        assertThat(MoMoEnvironment.SANDBOX.getBaseUrl()).isEqualTo("https://sandbox.momodeveloper.mtn.com");
    }

    @Test
    @DisplayName("PRODUCTION has production base URL")
    void productionHasProductionUrl() {
        assertThat(MoMoEnvironment.PRODUCTION.getBaseUrl()).isEqualTo("https://proxy.momoapi.mtn.com");
    }

    @Test
    @DisplayName("all values are defined")
    void allValuesDefined() {
        assertThat(MoMoEnvironment.values()).hasSize(3);
    }
}
