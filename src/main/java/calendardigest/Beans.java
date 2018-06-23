package calendardigest;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

    private static final Logger LOGGER = LoggerFactory.getLogger(Beans.class);

    @Bean
    public OAuthManager oAuthManager() {
        return new OAuthManager();
    }

    @Bean
    public Scheduler emailScheduler() {
        try {
            return StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            LOGGER.error("Failed to create task scheduler.", e);
            System.exit(-1);
            return null;
        }
    }

}
