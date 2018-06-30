package calendardigest;

import com.google.api.client.util.Base64;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public final class CalendarDigest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarDigest.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' h:mm a");
    private final Calendar calendar;
    private final Gmail gmail;
    private Events eventsThisWeek;
    private Events eventsNextWeek;
    private Events eventsInTwoWeeks;

    public CalendarDigest(final Calendar calendar, final Gmail gmail) {
        this.calendar = calendar;
        this.gmail = gmail;
    }

    void send() {
        LOGGER.info("Creating calendar digest...");
        loadEvents();
        try {
            final URI uri = ClassLoader.getSystemResource("email_credentials.txt").toURI();
            final List<String> lines = Files.readAllLines(Paths.get(uri));
            final String to = lines.get(0);
            final String from = lines.get(1);
            final String subject = "Next " + eventsThisWeek.getItems().size() + " Events";
            final String bodyText = formatBody();

            MimeMessage message = createEmail(to, from, subject, bodyText);
            if (message != null) {
                sendMessage(message);
                LOGGER.info("Email sent!");
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEvents() {
        LOGGER.info("Fetching events...");
        final long nowMillis = System.currentTimeMillis();
        final long oneDayMillis = TimeUnit.DAYS.toMillis(1);
        final DateTime now = new DateTime(nowMillis);
        final DateTime nextMonday = new DateTime(nowMillis + 8 * oneDayMillis);
        final DateTime mondayInTwoWeeks = new DateTime(nowMillis + 15 * oneDayMillis);
        final DateTime mondayInThreeWeeks = new DateTime(nowMillis + 22 * oneDayMillis);
        try {
            eventsThisWeek = getEventsForInterval(now, nextMonday);
            eventsNextWeek = getEventsForInterval(nextMonday, mondayInTwoWeeks);
            eventsInTwoWeeks = getEventsForInterval(mondayInTwoWeeks, mondayInThreeWeeks);
        } catch (IOException e) {
            LOGGER.error("Failed to load events.", e);
        }
    }

    private Events getEventsForInterval(DateTime lowerBound, DateTime upperBound) throws IOException {
        return calendar.events().list("primary")
                .setTimeMin(lowerBound)
                .setTimeMax(upperBound)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
    }

    private MimeMessage createEmail(final String to,
                                    final String from,
                                    final String subject,
                                    final String bodyText) {
        LOGGER.info("Creating email...");
        try {
            final Session session = Session.getDefaultInstance(new Properties(), null);
            final MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(from));
            email.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(to));
            email.setSubject(subject);
            email.setText(bodyText);
            return email;
        } catch (MessagingException e) {
            LOGGER.error("Failed to create email.", e);
            return null;
        }
    }

    private String formatBody() {
        if (eventsThisWeek.getItems().isEmpty() && eventsNextWeek.getItems().isEmpty() &&
                eventsInTwoWeeks.getItems().isEmpty()) {
            return "No upcoming events found.";
        } else {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Upcoming events").append(System.lineSeparator());
            stringBuilder.append("This week:").append(System.lineSeparator());
            appendEvents(eventsThisWeek.getItems(), stringBuilder);
            stringBuilder.append("Next week:").append(System.lineSeparator());
            appendEvents(eventsNextWeek.getItems(), stringBuilder);
            stringBuilder.append("In two weeks:").append(System.lineSeparator());
            appendEvents(eventsInTwoWeeks.getItems(), stringBuilder);
            return stringBuilder.toString();
        }
    }

    private void appendEvents(List<Event> items, StringBuilder stringBuilder) {
        if (items.isEmpty()) {
            stringBuilder.append("No events").append(System.lineSeparator());
        } else {
            for (final Event event : items) {
                if (event.getStart().getDateTime() != null) {
                    final long millisStart = event.getStart().getDateTime().getValue();
                    final String formattedDate = DATE_FORMAT.format(new Date(millisStart));
                    stringBuilder.append(String.format("%s (%s)\n", event.getSummary(), formattedDate));
                } else {
                    stringBuilder.append(String.format("%s (%s)\n", event.getSummary(), event.getStart().getDate()));
                }
            }
        }
    }

    private void sendMessage(final MimeMessage emailContent) {
        final Message message = createMessageWithEmail(emailContent);
        if (message != null) {
            try {
                gmail.users().messages().send("me", message).execute();
            } catch (IOException e) {
                LOGGER.error("Failed to send message.", e);
            }
        }
    }

    private Message createMessageWithEmail(final MimeMessage emailContent) {
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            emailContent.writeTo(buffer);
            final String encodedEmail = Base64.encodeBase64URLSafeString(buffer.toByteArray());
            final Message message = new Message();
            message.setRaw(encodedEmail);
            return message;
        } catch (MessagingException | IOException e) {
            LOGGER.error("Failed to encode message.", e);
            return null;
        }
    }

}
