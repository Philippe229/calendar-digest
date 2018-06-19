package calendardigest;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class OAuthManager {

    /**
     * Directory to store user credentials
     */
    private static final String CREDENTIALS_FOLDER = "credentials";

    /**
     * File where client ID and client secret are stored
     */
    private static final String CLIENT_SECRET_DIR = "client_secret.json";

    /**
     * Global instance of the scopes required by this app.
     * If modifying these scopes, delete previously saved credentials/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

    private Logger logger;
    private JsonFactory jsonFactory;
    private GoogleClientSecrets clientSecrets;

    public OAuthManager() {
        logger = LoggerFactory.getLogger(OAuthManager.class);
        jsonFactory = JacksonFactory.getDefaultInstance();
        loadClientSecrets();
    }

    private void loadClientSecrets() {
        InputStream inputStream = Application.class.getClassLoader().getResourceAsStream("client_secret.json");
        try {
            clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(inputStream));
            logger.info("Client secrets loaded.");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void test() {

    }
}
