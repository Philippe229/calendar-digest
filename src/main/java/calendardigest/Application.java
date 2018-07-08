package calendardigest;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        ApplicationContext context = AppContextProvider.getApplicationContext();

        Scheduler scheduler = context.getBean(Scheduler.class);
        try {
            scheduler.start();

            JobDetail job = newJob(SendEmailJob.class)
                    .withIdentity("job1", "group1")
                    .build();

            final ZoneId zoneId = ZoneId.of("America/Montreal");
            final LocalDate localDate = LocalDate.now();
            final LocalDate sunday = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            final Date dateMontreal = Date.from(sunday.atStartOfDay(zoneId).toInstant());
            final Date dateUTC = null;
            final Date date = new Date(dateMontreal.getTime() + TimeUnit.MINUTES.toMillis(15));

            final long oneWeekInHours = TimeUnit.DAYS.toHours(7);

            LOGGER.info("ZoneId: " + zoneId.getId());
            LOGGER.info("Digest will first be sent on " + sunday.toString());
            LOGGER.info("Montreal time: " + dateMontreal.toString());
            LOGGER.info("Digest will be sent at time: " + date.toString());

            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startAt(dateMontreal)
                    .withSchedule(SimpleScheduleBuilder
                            .repeatHourlyForever((int) oneWeekInHours))
                    .build();

            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
