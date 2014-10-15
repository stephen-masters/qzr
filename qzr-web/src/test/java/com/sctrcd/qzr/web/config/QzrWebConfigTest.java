package com.sctrcd.qzr.web.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sctrcd.qzr.config.QzrWebConfig;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QzrWebConfig.class, loader = AnnotationConfigWebContextLoader.class)
@ActiveProfiles("drools")
public class QzrWebConfigTest {
    
    @Test
    public void shouldInjectBeans() {
    }
    
}
