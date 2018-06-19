package calendardigest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

    @Bean
    public OAuthManager oAuthManager() {
        return new OAuthManager();
    }

}
