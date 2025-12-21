package az.et.warehouse;

import az.et.warehouse.util.LogUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class WarehouseApplication {

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(WarehouseApplication.class);
        final Environment env = app.run(args).getEnvironment();
        LogUtil.logApplicationStartup(env);
    }

}
