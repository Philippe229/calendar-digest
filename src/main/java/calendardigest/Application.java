package calendardigest;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
//        ApplicationContext context = AppContextProvider.getApplicationContext();
//
//        Scheduler scheduler = context.getBean(Scheduler.class);
//        try {
//            scheduler.start();
//
//            JobDetail job = newJob(SendEmailJob.class)
//                    .withIdentity("job1", "group1")
//                    .build();
//
//            final LocalDate localDate = LocalDate.now();
//            final LocalDate sunday = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
//            final Date date = Date.from(sunday.atStartOfDay(ZoneId.systemDefault()).toInstant());
//            final long oneWeekInHours = TimeUnit.DAYS.toHours(7);
//
//            Trigger trigger = newTrigger()
//                    .withIdentity("trigger1", "group1")
//                    .startAt(new Date(date.getTime() + TimeUnit.MINUTES.toMillis(15)))
//                    .withSchedule(SimpleScheduleBuilder
//                            .repeatHourlyForever((int) oneWeekInHours))
//                    .build();
//
//            scheduler.scheduleJob(job, trigger);
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
    }

}
