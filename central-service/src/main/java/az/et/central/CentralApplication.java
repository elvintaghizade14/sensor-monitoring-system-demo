package az.et.central;

import az.et.central.util.LogUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class CentralApplication {

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(CentralApplication.class);
        final Environment env = app.run(args).getEnvironment();
        LogUtil.logApplicationStartup(env);
    }
}
