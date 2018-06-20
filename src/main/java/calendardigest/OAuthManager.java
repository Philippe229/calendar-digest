package calendardigest;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public final class OAuthManager {

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

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthManager.class);
    private final JsonFactory jsonFactory;
    private GoogleClientSecrets clientSecrets;
    private Credential credential;

    public OAuthManager() {
        jsonFactory = JacksonFactory.getDefaultInstance();
        loadClientSecrets();
    }

    private void loadClientSecrets() {
        final InputStream inputStream = Application.class
                .getClassLoader()
                .getResourceAsStream(CLIENT_SECRET_DIR);
        try {
            clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(inputStream));
            LOGGER.info("Client secrets loaded.");
        } catch (IOException e) {
            LOGGER.error("Failed to load client secrets.", e);
            System.exit(-1);
        }
    }

    public void requestAuthorization(final NetHttpTransport httpTransport) {
        try {
            // Build flow and trigger user authorization request.
            final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                    .Builder(httpTransport, jsonFactory, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                    .setAccessType("offline")
                    .build();

            final LocalServerReceiver localServerReceiver = new LocalServerReceiver();
            final AuthorizationCodeInstalledApp authorizationCode = new AuthorizationCodeInstalledApp(flow, localServerReceiver);
            credential = authorizationCode.authorize("testUser");
            LOGGER.info("testUser" + " authorized.");
        } catch (IOException e) {
            LOGGER.error("Failed to request user authorization.", e);
        }
    }

    public Credential getCredential() {
        return credential;
    }

}
