package ru.maksimosipov.mysbstarter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({HttpLoggingProperties.class})
@Slf4j
public class HttpLoggingAutoConfiguration {


    @Bean
    @ConditionalOnProperty(prefix = "http.logging", name = "enabled", havingValue = "true")
    public HttpLoggingAspect loggingAspect(HttpLoggingProperties properties) {
        return new HttpLoggingAspect(properties);
    }

}
