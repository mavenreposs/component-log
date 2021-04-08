package io.github.mavenreposs.component.log.contracts;

public interface LoggerInterface {

    //用来打印输出价值比较低的信息
    void verbose(String tag, String message);

    //用来打印调试信息
    void debug(String tag, String message);

    //用来打印一般提示信息
    void info(String tag, String message);

    //用来打印警告信息，这种信息一般是提示开发者需要注意，有可能会出现问题！
    void warn(String tag, String message);

    //用来打印错误崩溃日志信息，例如在try-catch的catch中输出捕获的错误信息。
    void error(String tag, String message);

    //用来打印不太可能发生的错误，表明当前问题是个严重的等级
    void assertError(String tag, String message);

}
