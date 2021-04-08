package io.github.mavenreposs.component.log.logger;


import io.github.mavenreposs.component.log.contracts.LoggerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 控制台输出Log
 */
public class ConsoleLogger implements LoggerInterface {

    @Override
    public void debug(String tag, String message) {
        Logger logger = LoggerFactory.getLogger(Object.class);
        logger.info(message);
    }

    @Override
    public void error(String tag, String message) {
        Logger logger = LoggerFactory.getLogger(Object.class);
        logger.error(message);
    }

    @Override
    public void assertError(String tag, String message) {
        error(tag, message);
    }

    @Override
    public void warn(String tag, String message) {
        Logger logger = LoggerFactory.getLogger(Object.class);
        logger.warn(message);
    }

    @Override
    public void verbose(String tag, String message) {
        debug(tag, message);
    }

    @Override
    public void info(String tag, String message) {
        Logger logger = LoggerFactory.getLogger(Object.class);
        logger.info(message);
    }

}
