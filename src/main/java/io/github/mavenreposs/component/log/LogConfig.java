package io.github.mavenreposs.component.log;

import java.util.ArrayList;
import java.util.List;

/**
 * Log打印默认配置
 */
public final class LogConfig {

    public static final String DEFAULT_TAG = "RCLOG";

    //日志的Tag
    private String tag;

    //显示方法调用栈数量
    private int methodCount = 1;

    //是否显示线程信息
    private boolean showThreadInfo = true;

    //是否输出到文件
    private boolean printToFile = false;

    private List<LogInterface> logger = new ArrayList<>();

    /**
     * 日志级别，只有大于等于logLevel的日志才会打印
     * <p/>
     * 参考：{@link LogLevel}
     */
    private int logLevel = LogLevel.VERBOSE;

    public LogConfig() {
        logger.add(new ConsoleLogTool());
        logger.add(new FileLogTool());
    }

    public LogConfig tag(String tag) {
        this.tag = tag;
        return this;
    }

    public LogConfig showThreadInfo(boolean isShow) {
        showThreadInfo = isShow;
        return this;
    }

    public LogConfig methodCount(int methodCount) {
        if (methodCount < 0) {
            methodCount = 0;
        }
        this.methodCount = methodCount;
        return this;
    }

    public LogConfig printToFile(boolean printToFile) {
        this.printToFile = printToFile;
        return this;
    }

    public LogConfig addLogger(LogInterface logInterface) {
        if (logInterface != null && !logger.contains(logInterface)) {
            logger.add(logInterface);
        }
        return this;
    }

    int getMethodCount() {
        return methodCount;
    }

    boolean isShowThreadInfo() {
        return showThreadInfo;
    }

    int getLogLevel() {
        return logLevel;
    }

    public LogConfig setLogLevel(int logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    String getTag() {
        return tag;
    }

    boolean isPrintToFile() {
        return printToFile;
    }

    List<LogInterface> getLoggers() {
        return logger;
    }
}
