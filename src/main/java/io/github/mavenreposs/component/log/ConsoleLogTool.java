package io.github.mavenreposs.component.log;


import org.apache.log4j.Logger;

/**
 * 控制台输出Log
 */
public class ConsoleLogTool implements LogInterface {
    @Override
    public void d(String tag, String message) {
        Logger logger = Logger.getLogger(this.getClass());
        logger.debug(message);
    }

    @Override
    public void e(String tag, String message) {
        Logger logger = Logger.getLogger(this.getClass());
        logger.error(message);
    }

    @Override
    public void w(String tag, String message) {
        Logger logger = Logger.getLogger(this.getClass());
        logger.warn(message);
    }

    @Override
    public void i(String tag, String message) {
        Logger logger = Logger.getLogger(this.getClass());
        logger.info(message);
    }

    @Override
    public void v(String tag, String message) {

    }

    @Override
    public void wtf(String tag, String message) {

    }
}
