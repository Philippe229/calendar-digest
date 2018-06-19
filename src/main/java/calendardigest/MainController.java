package calendardigest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    private final OAuthManager oAuthManager;

    @Autowired
    public MainController(OAuthManager oAuthManager) {
        this.oAuthManager = oAuthManager;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String submit(@RequestBody String body) {
        System.out.println(body);
        oAuthManager.test();
        return "{ \"message\": \"hello from server\" }";
    }

}
