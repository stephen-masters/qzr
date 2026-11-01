package com.sctrcd.qzr;

import com.sctrcd.qzr.web.json.JsonJodaDateTimeSerializer;
import com.sctrcd.qzr.web.json.JsonJodaLocalDateSerializer;
import java.util.Arrays;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleModule;

/**
 * By running this main class as a Java application, Spring Boot will find and
 * wire up all configurations and beans in the application. It will also run up
 * an embedded Tomcat web server for the REST API controllers and web pages.
 *
 * @author Stephen Masters
 */
@SpringBootApplication
public class Qzr {

    private static final Logger log = LoggerFactory.getLogger(Qzr.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Qzr.class, args);

        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        StringBuilder sb = new StringBuilder("\n\n    Active profiles:\n");
        if (activeProfiles.length == 0) {
            sb.append("        No active profiles.\n");
        } else {
            for (String profile : activeProfiles) {
                sb.append("        " + profile + "\n");
            }
        }

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);

        sb.append("\n    Application beans:\n");
        for (String beanName : beanNames) {
            sb.append("        " + beanName + "\n");
        }
        log.info(sb.toString());
    }

    /**
     * By defining the {@link KieContainer} as a bean here, we ensure that
     * Drools will hunt out the kmodule.xml and rules on application startup.
     *
     * @return The {@link KieContainer}.
     */
    @Bean
    public KieContainer kieContainer() {
        return KieServices.Factory.get().getKieClasspathContainer();
    }

    /**
     * Spring Boot adds every {@link JacksonModule} bean to the JSON mapper it
     * configures. By adding custom serialisers for Joda {@link DateTime} and
     * {@link LocalDate} objects, we control the JSON representation of these
     * objects globally.
     */
    @Bean
    public JacksonModule jodaModule() {
        SimpleModule module = new SimpleModule("qzr-joda");
        module.addSerializer(DateTime.class, new JsonJodaDateTimeSerializer());
        module.addSerializer(LocalDate.class, new JsonJodaLocalDateSerializer());
        return module;
    }
}
