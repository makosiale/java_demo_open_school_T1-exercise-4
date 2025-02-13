package ru.maksimosipov.mysbstarter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "http.logging")
@Getter
@Setter
public class HttpLoggingProperties {
    private boolean enabled =false;
    private String level= "INFO";
}
