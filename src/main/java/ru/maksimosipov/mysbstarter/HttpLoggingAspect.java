package ru.maksimosipov.mysbstarter;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class HttpLoggingAspect {
    private final HttpLoggingProperties properties;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerMethods() {
    }

    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();
        String args = Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.joining(","));

        logMessage(properties.getLevel(), "Request: {} {} | Headers: {} | Body: {}",
                request.getMethod(), request.getRequestURI(), getHeaderAsString(request), args);
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logResponse(Object result) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletResponse response = attributes.getResponse();
        logMessage(properties.getLevel(), "Response Code: {} | Body: {}", response.getStatus(), result);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "e")
    public void logError(Throwable e) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();
        logMessage("ERROR", "Error in Request: {} {} | Exception: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
    }

    private void logMessage(String level, String message, Object... args) {
        switch (level.toUpperCase()) {
            case "DEBUG":
                log.debug(message, args);
                break;
            case "INFO":
                log.info(message, args);
                break;
            case "WARN":
                log.warn(message, args);
                break;
            case "ERROR":
                log.error(message, args);
                break;
            default:
                log.warn("Unknown logging level: {}, defaulting to INFO", level);
                log.info(message, args);
        }
    }

    private String getHeaderAsString(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .map(headerName -> headerName + ": " + request.getHeader(headerName))
                .collect(Collectors.joining("\n"));
    }
}