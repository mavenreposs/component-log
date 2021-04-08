package io.github.mavenreposs.component.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 控制台输出Log
 */
public class ConsoleLogTool implements LogInterface {
    @Override
    public void d(String tag, String message) {
        Logger logger = LoggerFactory.getLogger(Object.class);
        logger.info(message);
    }

    @Override
    public void e(String tag, String message) {
        Logger logger = LoggerFactory.getLogger(Object.class);
        logger.error(message);
    }

    @Override
    public void w(String tag, String message) {
        Logger logger = LoggerFactory.getLogger(Object.class);
        logger.warn(message);
    }

    @Override
    public void i(String tag, String message) {
        Logger logger = LoggerFactory.getLogger(Object.class);
        logger.info(message);
    }

    @Override
    public void v(String tag, String message) {
        Logger logger = LoggerFactory.getLogger(Object.class);
        logger.debug(message);
    }

    @Override
    public void wtf(String tag, String message) {
        Logger logger = LoggerFactory.getLogger(Object.class);
        logger.debug(message);
    }
}
