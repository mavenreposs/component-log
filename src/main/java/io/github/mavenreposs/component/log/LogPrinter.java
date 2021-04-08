package io.github.mavenreposs.component.log;

import io.github.mavenreposs.component.log.messager.JsonMessager;
import io.github.mavenreposs.component.log.messager.ObjectMessager;
import io.github.mavenreposs.component.log.messager.XmlMessager;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志打印机
 * <p>
 * 负责日志的格式化和打印
 * </p>
 */
public final class LogPrinter {

    /**
     * Localize single tag and method count for each thread
     */
    private final ThreadLocal<String> localTag = new ThreadLocal<>();
    private final ThreadLocal<Integer> localMethodCount = new ThreadLocal<>();
    private final ThreadLocal<Boolean> localIsPrintToFile = new ThreadLocal<>();

    private final ThreadLocal<List<String>> localMessageList = new ThreadLocal<>();

    /**
     * It is used to determine log settings such as method count, thread info visibility
     */
    private final LogConfig mLogConfig = new LogConfig();

    public LogConfig getLogConfig() {
        return mLogConfig;
    }

    public ThreadLocal<Integer> getLocalMethodCount() {
        return localMethodCount;
    }

    public ThreadLocal<List<String>> getLocalMessageList() {
        return localMessageList;
    }

    public LogPrinter tag(String tag) {
        if (tag != null) {
            localTag.set(tag);
        }
        return this;
    }

    public LogPrinter method(int methodCount) {
        localMethodCount.set(methodCount);
        return this;
    }

    public LogPrinter file(boolean isPrintToFile) {
        localIsPrintToFile.set(isPrintToFile);
        return this;
    }



    public LogPrinter append(String message, Object... args) {
        String msg = createMessage(message, args);
        if (!LogUtil.isEmpty(msg)) {
            List<String> msgList = localMessageList.get();
            if (msgList == null) {
                msgList = new ArrayList<>();
                localMessageList.set(msgList);
            }
            msgList.add(msg);
        }
        return this;
    }

    public LogPrinter appendJson(String json) {
        String msg = parseJsonMessage(json);
        if (!LogUtil.isEmpty(msg)) {
            List<String> msgList = localMessageList.get();
            if (msgList == null) {
                msgList = new ArrayList<>();
                localMessageList.set(msgList);
            }
            msgList.add(msg);
        }
        return this;
    }


    /**
     * @param xml XML字符串
     * @return 返回日志对象
     */
    public LogPrinter appendXml(String xml) {
        String msg = parseXmlMessage(xml);
        if (!LogUtil.isEmpty(msg)) {
            List<String> msgList = localMessageList.get();
            if (msgList == null) {
                msgList = new ArrayList<>();
                localMessageList.set(msgList);
            }
            msgList.add(msg);
        }
        return this;
    }

    public LogPrinter appendObject(Object obj) {
        String msg = parseObjectMessage(obj);
        if (!LogUtil.isEmpty(msg)) {
            List<String> msgList = localMessageList.get();
            if (msgList == null) {
                msgList = new ArrayList<>();
                localMessageList.set(msgList);
            }
            msgList.add(msg);
        }
        return this;
    }

    public void debug(String message, Object... args) {
        log(LogLevel.DEBUG, message, args);
    }

    public void error(String message, Object... args) {
        error(null, message, args);
    }

    public void error(Throwable throwable, String message, Object... args) {
        if (throwable != null && message != null) {
            message += " : " + LogUtil.getStackTraceString(throwable);
        }
        if (throwable != null && message == null) {
            message = throwable.toString();
        }
        if (message == null) {
            message = "No message/exception is set";
        }
        log(LogLevel.ERROR, message, args);
    }

    public void warn(String message, Object... args) {
        log(LogLevel.WARN, message, args);
    }

    public void info(String message, Object... args) {
        log(LogLevel.INFO, message, args);
    }

    public void json(String json) {
        debug(parseJsonMessage(json));
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public void xml(String xml) {
        debug(parseXmlMessage(xml));
    }

    /**
     * Formats the obj content and print it
     *
     * @param obj the xml content
     */
    public void object(Object obj) {
        debug(parseObjectMessage(obj));
    }

    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    private synchronized void log(int logType, String msg, Object... args) {
        if (logType < mLogConfig.getLogLevel()) {
            return;
        }
        String tag = getTag();
        boolean isPrintToFile = getIsPrintToFile();
        if (mLogConfig.isShowThreadInfo()) {
            tag += "[" + Thread.currentThread().getName() + "]";
        }
        String message = createMessage(msg, args);


        if (LogUtil.isEmpty(message)) {
            message = "Empty/NULL log message";
        }

        new PrinterTemplate(this, logType, tag, message, isPrintToFile).println();

    }

    /**
     * @return the appropriate tag based on local or global
     */
    private String getTag() {
        String tag = localTag.get();
        if (!LogUtil.isEmpty(tag)) {
            localTag.remove();
            return tag;
        }
        tag = mLogConfig.getTag();
        if (!LogUtil.isEmpty(tag)) {
            return tag;
        } else {
            return LogConfig.DEFAULT_TAG;
        }
    }

    private boolean getIsPrintToFile() {
        Boolean printToFile = localIsPrintToFile.get();
        if (printToFile != null) {
            localIsPrintToFile.remove();
            return printToFile;
        }
        return this.mLogConfig.isPrintToFile();
    }

    private String createMessage(String message, Object... args) {
        return args.length == 0 ? message : String.format(message, args);
    }

    /**
     * 格式化xml字符串
     *
     * @param xml XML字符串
     * @return 消息内容
     */
    private String parseXmlMessage(String xml) {
        if (LogLevel.DEBUG < mLogConfig.getLogLevel()) {
            //因为对象输出是以debug级别输出的，所以如果日志级别配置高于DEBUG等级，则不会输出，所以也不需要进行字符串格式化
            return null;
        }

        return new XmlMessager(xml).getParseMessage();
    }

    /**
     * 格式化json字符串
     *
     * @param json JSON字符串
     * @return 消息内容
     */
    private String parseJsonMessage(String json) {
        if (LogLevel.DEBUG < mLogConfig.getLogLevel()) {
            //因为对象输出是以debug级别输出的，所以如果日志级别配置高于DEBUG等级，则不会输出，所以也不需要进行字符串格式化
            return null;
        }
        return new JsonMessager(json).getParseMessage();
    }

    /**
     * 格式化对象
     *
     * @param obj 对象
     * @return 消息内容
     */
    private String parseObjectMessage(Object obj) {
        if (LogLevel.DEBUG < mLogConfig.getLogLevel()) {
            //因为对象输出是以debug级别输出的，所以如果日志级别配置高于DEBUG等级，则不会输出，所以也不需要进行字符串格式化
            return null;
        }
        return new ObjectMessager(obj).getParseMessage();
    }

}
