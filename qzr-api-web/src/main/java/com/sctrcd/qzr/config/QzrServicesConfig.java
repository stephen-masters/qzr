package com.sctrcd.qzr.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.sctrcd.qzr.rules.HealthQuizKieConfig;

@Configuration
@ComponentScan(basePackages = { "com.sctrcd.qzr.services" })
@Import({ HealthQuizKieConfig.class })
public class QzrServicesConfig {

}
