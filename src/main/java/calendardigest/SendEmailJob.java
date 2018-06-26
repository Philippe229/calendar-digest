package calendardigest;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public final class SendEmailJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        final ApplicationContext context = AppContextProvider.getApplicationContext();
        context.getBean(CalendarDigest.class).send();
    }

}
