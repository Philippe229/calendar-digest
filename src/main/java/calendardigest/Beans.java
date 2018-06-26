package calendardigest;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.gmail.Gmail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class Beans {

    private static final Logger LOGGER = LoggerFactory.getLogger(Beans.class);

    @Bean
    public NetHttpTransport netHttpTransport() {
        try {
            return GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            System.out.println("Failed to create HTTP transport.");
            System.exit(-1);
            return null;
        }
    }

    @Bean
    public OAuthManager oAuthManager() {
        final OAuthManager oAuthManager = new OAuthManager();
        oAuthManager.requestAuthorization(netHttpTransport());
        return oAuthManager;
    }

    @Bean
    public Calendar calendar() {
        return new Calendar
                .Builder(netHttpTransport(), JacksonFactory.getDefaultInstance(), oAuthManager().getCalendarCredential())
                .setApplicationName("Calendar Digest Calendar API")
                .build();
    }

    @Bean
    public Gmail gmail() {
        return new Gmail
                .Builder(netHttpTransport(), JacksonFactory.getDefaultInstance(), oAuthManager().getGmailCredential())
                .setApplicationName("Calendar Digest Gmail API")
                .build();
    }

    @Bean
    public CalendarDigest calendarDigest() {
        return new CalendarDigest(calendar(), gmail());
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
