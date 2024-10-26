//package com.example.practice.config;
//import com.paypal.base.rest.APIContext;
//import com.paypal.base.rest.OAuthTokenCredential;
//import com.paypal.base.rest.PayPalRESTException;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class PayPalConfig {

//	public String clientId="AdWGAw4eqoJUeEaD6AIrODP1mJqqGO0V3YnHibYFYXdc2NzawpztOn4LZcZ18l6fy2kuR2NOI2RqKH6E";

//    @Value("${paypal.client.secret}")
//	public String clientSecret="EBBV9W6hrMzaojNS-Vp1ROnUUKp2oEMQ9GVRipAsNETTgNuvpoW4sL5slrD82UTQ5rgiCucV-sZnAwMD";
    
//    @Value("${paypal.mode}")
//	public String mode="sandbox";

//    @Bean
//    public Map<String, String> paypalSdkConfig() {
//        Map<String, String> configMap = new HashMap<>();
//        configMap.put("mode", "sandbox"); // or "live" for production
//        return configMap;
//    }
//
//    @Bean
//    public OAuthTokenCredential oAuthTokenCredential() {
//        return new OAuthTokenCredential(clientId, clientSecret);
//    }

//    @Bean
//    public APIContext apiContext() {
//        return new APIContext(clientId, clientSecret, "sandbox");
//    }
//}

package com.example.practice.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

//    @Value("${paypal.client.id}")
    private String clientId="AdWGAw4eqoJUeEaD6AIrODP1mJqqGO0V3YnHibYFYXdc2NzawpztOn4LZcZ18l6fy2kuR2NOI2RqKH6E";

//    @Value("${paypal.client.secret}")
    private String clientSecret="EBBV9W6hrMzaojNS-Vp1ROnUUKp2oEMQ9GVRipAsNETTgNuvpoW4sL5slrD82UTQ5rgiCucV-sZnAwMD";

    @Bean
    public PayPalHttpClient payPalClient() {
        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        return new PayPalHttpClient(environment);
    }
}


