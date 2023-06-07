
package com.ubetgpt.betgpt.paypal.config;

import com.paypal.base.rest.APIContext;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "paypal")
public class PaypalConfig {

    @NotEmpty
    private String baseUrl;

    @NotEmpty
    private String clientId;
    @NotEmpty
    private String secret;

    @Bean
    public APIContext apiContext() {
        APIContext context = new APIContext(clientId, secret, "sandbox");
        context.setConfigurationMap(null);
        return context;
    }
}
