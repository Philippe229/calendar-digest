package calendardigest;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
public final class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private final OAuthManager oAuthManager;

    @Autowired
    public MainController(final OAuthManager oAuthManager) {
        this.oAuthManager = oAuthManager;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String submit(@RequestBody String body) {
        System.out.println(body);

        // Build a new authorized API client service.
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            oAuthManager.requestAuthorization(HTTP_TRANSPORT);
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.error("Failed to create HTTP transport.", e);
            System.exit(-1);
        }

        final Credential credential = oAuthManager.getCredential();
        System.out.println(credential.getExpiresInSeconds());

        return "{ \"message\": \"hello from server\" }";
    }

}
