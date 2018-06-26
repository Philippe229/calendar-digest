package calendardigest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class AppContextProvider implements ApplicationContextAware {

    private static ApplicationContext context;

    static ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext ctx) {
        context = ctx;
    }

}
