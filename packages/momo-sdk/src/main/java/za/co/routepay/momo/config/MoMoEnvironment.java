package za.co.routepay.momo.config;

/**
 * MTN MoMo API environments.
 */
public enum MoMoEnvironment {

    /** Local mock — no external calls, realistic fake responses. */
    MOCK("http://localhost:8099"),

    /** MTN sandbox — test credentials, test phone numbers. */
    SANDBOX("https://sandbox.momodeveloper.mtn.com"),

    /** Production — real money, real phone numbers. */
    PRODUCTION("https://proxy.momoapi.mtn.com");

    private final String baseUrl;

    MoMoEnvironment(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
