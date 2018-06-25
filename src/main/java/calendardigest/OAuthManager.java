package calendardigest;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.gmail.GmailScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public final class OAuthManager {

    /**
     * Directory to store Gmail user credentials
     */
    private static final String GMAIL_CREDENTIALS_FOLDER = "credentials/gmail";

    /**
     * Directory to store calendar user credentials
     */
    private static final String CALENDAR_CREDENTIALS_FOLDER = "credentials/calendar";

    /**
     * File where client ID and client secret for Gmail API are stored
     */
    private static final String GMAIL_CLIENT_SECRET = "gmail_client_secret.json";

    /**
     * File where client ID and client secret for calendar API are stored
     */
    private static final String CALENDAR_CLIENT_SECRET = "calendar_client_secret.json";

    /**
     * Global instance of the scopes required by this app.
     * If modifying these scopes, delete previously saved credentials/ folder.
     */
    private static final List<String> GMAIL_SCOPES = Collections.singletonList(GmailScopes.GMAIL_COMPOSE);
    private static final List<String> CALENDAR_SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthManager.class);
    private final JsonFactory jsonFactory;

    private GoogleClientSecrets gmailClientSecrets;
    private GoogleClientSecrets calendarClientSecrets;
    private Credential gmailCredential;
    private Credential calendarCredential;

    public OAuthManager() {
        jsonFactory = JacksonFactory.getDefaultInstance();
    }

    @PostConstruct
    public void init() {
        loadClientSecrets();
        requestAuthorization();
    }

    private void loadClientSecrets() {
        final InputStream calendarInputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(CALENDAR_CLIENT_SECRET);

        final InputStream gmailInputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(GMAIL_CLIENT_SECRET);
        try {
            calendarClientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(calendarInputStream));
            gmailClientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(gmailInputStream));
            LOGGER.info("Client secrets loaded.");
        } catch (IOException e) {
            LOGGER.error("Failed to load client secrets.", e);
            System.exit(-1);
        }
    }

    private void requestAuthorization() {
        final String userId = "testUser";
        NetHttpTransport httpTransport = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.error("Failed to create HTTP transport.", e);
            System.exit(-1);
        }

        try {
            // Build flow and trigger user authorization request.
            LOGGER.info("Requesting authorization for calendar.");
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                    .Builder(httpTransport, jsonFactory, calendarClientSecrets, CALENDAR_SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(CALENDAR_CREDENTIALS_FOLDER)))
                    .setAccessType("offline")
                    .build();

            AuthorizationCodeInstalledApp authorizationCode = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver());
            calendarCredential = authorizationCode.authorize(userId);

            LOGGER.info("Requesting authorization for Gmail.");
            flow = new GoogleAuthorizationCodeFlow
                    .Builder(httpTransport, jsonFactory, gmailClientSecrets, GMAIL_SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(GMAIL_CREDENTIALS_FOLDER)))
                    .setAccessType("offline")
                    .build();

            authorizationCode = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver());
            gmailCredential = authorizationCode.authorize(userId);

            LOGGER.info(userId + " authorized.");
        } catch (IOException e) {
            LOGGER.error("Failed to request user authorization.", e);
        }
    }

    Credential getCalendarCredential() {
        return calendarCredential;
    }

    Credential getGmailCredential() {
        return gmailCredential;
    }

}
