package calendardigest;

import com.google.api.client.auth.oauth2.Credential;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        ApplicationContext context = new AnnotationConfigApplicationContext(Beans.class);

        OAuthManager oAuthManager = context.getBean(OAuthManager.class);
        Credential credential = oAuthManager.getCalendarCredential();
        System.out.println(credential.getExpiresInSeconds());
        credential = oAuthManager.getGmailCredential();
        System.out.println(credential.getExpiresInSeconds());
    }

}
