package com.sctrcd.qzr;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sctrcd.qzr.web.json.JsonJodaDateTimeSerializer;
import com.sctrcd.qzr.web.json.JsonJodaLocalDateSerializer;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties
public class Qzr {

    private static Logger log = LoggerFactory.getLogger(Qzr.class);

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

    @Bean
    public ObjectMapper objectMapper(SimpleModule jacksonJodaModule) {
        jacksonJodaModule.addSerializer(DateTime.class, new JsonJodaDateTimeSerializer());
        jacksonJodaModule.addSerializer(LocalDate.class, new JsonJodaLocalDateSerializer());
        
        ObjectMapper om = new ObjectMapper();
        om.registerModule(jacksonJodaModule);
        return om;
    }

    @Bean
    public SerializationConfig serializationConfig(ObjectMapper objectMapper) {
        return objectMapper.getSerializationConfig();
    }

}