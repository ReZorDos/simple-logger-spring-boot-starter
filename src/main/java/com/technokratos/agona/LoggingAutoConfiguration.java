package com.technokratos.agona;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import com.technokratos.agona.enums.Output;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration()
@ConditionalOnClass(LoggingAspect.class)
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LoggingAspect loggingAspect(LoggingProperties properties, Logger myLogger) {
        return new LoggingAspect(properties, myLogger);
    }

    @Bean
    public Logger myLogger(LoggingProperties loggingProperties) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger myLogger = context.getLogger("myLogger");
        myLogger.setAdditive(false);
        myLogger.detachAndStopAllAppenders();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level --- %msg%n");
        encoder.start();

        if (loggingProperties.getOutput().equals(Output.CONSOLE)) {
            ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
            consoleAppender.setContext(context);
            consoleAppender.setName("APPEND_CONSOLE");

            consoleAppender.setEncoder(encoder);
            consoleAppender.start();

            myLogger.addAppender(consoleAppender);

        } else if (loggingProperties.getOutput().equals(Output.FILE)) {
            FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
            fileAppender.setContext(context);
            fileAppender.setName("APPEND_FILE");
            fileAppender.setFile(loggingProperties.getFilePath());
            fileAppender.setAppend(true);
            fileAppender.setEncoder(encoder);
            fileAppender.start();
            myLogger.addAppender(fileAppender);
        }

        myLogger.setLevel(ch.qos.logback.classic.Level.DEBUG);

        return myLogger;
    }

}