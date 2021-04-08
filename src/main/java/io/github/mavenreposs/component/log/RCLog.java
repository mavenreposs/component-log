package io.github.mavenreposs.component.log;

import io.github.mavenreposs.component.log.contracts.LoggerInterface;

/**
 * 日志输出
 */
public final class RCLog {
    //日志打印机
    private static LogPrinter printer = new LogPrinter();

    private RCLog() {
    }

    /**
     * 初始化全局配置
     *
     * @param tag 全局标签（默认为UEUEO）
     */
    public static void init(String tag) {
        init(tag, 1);
    }

    /**
     * 初始化全局配置
     *
     * @param tag         全局标签（默认为UEUEO）
     * @param methodCount 全局显示方法调用栈数量（默认为1）
     */
    public static void init(String tag, int methodCount) {
        init(tag, methodCount, LogLevel.VERBOSE);
    }

    /**
     * 初始化全局配置
     *
     * @param tag         全局Tag（默认为UEUEO）
     * @param methodCount 全局显示方法调用栈数量（默认为1）
     * @param level       全局日志输出等级
     */
    public static void init(String tag, int methodCount, int level) {
        init(tag, methodCount, level, false);
    }

    /**
     * 初始化全局配置
     *
     * @param tag         全局Tag（默认为UEUEO）
     * @param methodCount 全局显示方法调用栈数量（默认为1）
     * @param level       全局日志输出等级
     * @param printToFile 全局是否输出到文件中（默认为否），如果要打印到文件需要申请文件读写权限
     */
    public static void init(String tag, int methodCount, int level, boolean printToFile) {
        printer.getLogConfig().tag(tag).methodCount(methodCount).setLogLevel(level).printToFile(printToFile);
    }

    /**
     * 添加新的日志输入工具
     *
     * @param loggerInterface
     */
    public static void addLogger(LoggerInterface loggerInterface) {
        printer.getLogConfig().addLogger(loggerInterface);
    }

    /**
     * 指定当前这条Log信息打印的tag，不受全局配置影响
     *
     * @param tag
     * @return
     */
    public static LogPrinter tag(String tag) {
        return printer.tag(tag);
    }

    /**
     * 指定当前这条Log信息打印的方法调用栈数量，不受全局配置影响
     *
     * @param methodCount
     * @return
     */
    public static LogPrinter method(int methodCount) {
        return printer.method(methodCount);
    }

    /**
     * 指定当前这条Log信息是否打印到文件，不受全局配置影响
     *
     * @param file
     * @return
     */
    public static LogPrinter file(boolean file) {
        return printer.file(file);
    }

    /**
     * 拼接日志，每条日志之间会有分割线分割
     *
     * @param message
     * @param args
     * @return
     */
    public static LogPrinter append(String message, Object... args) {
        printer.append(message, args);
        return printer;
    }

    public static LogPrinter appendJson(String json) {
        printer.appendJson(json);
        return printer;
    }

    public static LogPrinter appendXml(String xml) {
        printer.appendXml(xml);
        return printer;
    }

    public static LogPrinter appendObject(Object object) {
        printer.appendObject(object);
        return printer;
    }

    public static void debug(String message, Object... args) {
        printer.debug(message, args);
    }

    public static void error(String message, Object... args) {
        printer.error(null, message, args);
    }

    public static void error(Throwable throwable, String message, Object... args) {
        printer.error(throwable, message, args);
    }

    public static void info(String message, Object... args) {
        printer.info(message, args);
    }

    public static void warn(String message, Object... args) {
        printer.warn(message, args);
    }

    public static void json(String json) {
        printer.json(json);
    }

    public static void xml(String xml) {
        printer.xml(xml);
    }

    public static void object(Object obj) {
        printer.object(obj);
    }
}
