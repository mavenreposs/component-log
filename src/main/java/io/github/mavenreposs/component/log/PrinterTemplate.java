package io.github.mavenreposs.component.log;

import io.github.mavenreposs.component.log.contracts.LoggerInterface;
import io.github.mavenreposs.component.log.logger.FileLogger;

import java.util.List;

public class PrinterTemplate {

    /**
     * The minimum stack trace index, starts at this class after two native calls.
     */
    private static final int MIN_STACK_OFFSET = 3;

    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    private int logType;

    private String tag;

    private String message;

    private boolean isPrintToFile;

    private LogConfig config;

    private LogPrinter printer;

    public PrinterTemplate(LogPrinter printer, int logType, String tag, String message, boolean isPrintToFile) {
        this.printer = printer;
        this.config = printer.getLogConfig();
        this.logType = logType;
        this.tag = tag;
        this.message = message;
        this.isPrintToFile = isPrintToFile;
    }

    public void println()
    {
        int methodCount = getMethodCount();

        List<String> appendMsgList = printer.getLocalMessageList().get();
        printer.getLocalMessageList().remove();

        if (methodCount <= 0 && (appendMsgList == null || appendMsgList.size() == 0) && !message.contains(System.getProperty("line.separator"))) {
            singlePrintln();
        } else {
            multiPrintln(methodCount, appendMsgList);
        }
    }

    //单行打印
    private void singlePrintln()
    {
        //如果只是单行日志，则不加边框直接输出
        logChunk(logType, tag, message, isPrintToFile);
    }

    //多行打印
    private void multiPrintln(int methodCount, List<String> appendMsgList)
    {
        logTopBorder(logType, tag, isPrintToFile);
        logHeaderContent(logType, tag, methodCount, isPrintToFile);

        if (methodCount > 0) {
            logDivider(logType, tag, isPrintToFile);
        }

        if (appendMsgList != null && appendMsgList.size() > 0) {
            for (String appendMsg : appendMsgList) {
                logContent(logType, tag, appendMsg, isPrintToFile);
                logDivider(logType, tag, isPrintToFile);
            }
        }

        logContent(logType, tag, message, isPrintToFile);
        logBottomBorder(logType, tag, isPrintToFile);
    }

    private void logTopBorder(int logType, String tag, boolean printToFile) {
        logChunk(logType, tag, TOP_BORDER, printToFile);
    }

    private void logHeaderContent(int logType, String tag, int methodCount, boolean printToFile) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String level = "";

        int stackOffset = getStackOffset(trace);

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("║ ")
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            logChunk(logType, tag, builder.toString(), printToFile);
        }
    }

    private void logBottomBorder(int logType, String tag, boolean printToFile) {
        logChunk(logType, tag, BOTTOM_BORDER, printToFile);
    }

    private void logDivider(int logType, String tag, boolean printToFile) {
        logChunk(logType, tag, MIDDLE_BORDER, printToFile);
    }

    private void logContent(int logType, String tag, String chunk, boolean printToFile) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + " " + line, printToFile);
        }
    }

    private void logChunk(int logType, String tag, String chunk, boolean printToFile) {
        List<LoggerInterface> loggerInterfaces = config.getLoggers();
        for (LoggerInterface loggerInterface : loggerInterfaces) {
            if (!(loggerInterface instanceof FileLogger) || printToFile) {
                switch (logType) {
                    case LogLevel.ERROR:
                        loggerInterface.error(tag, chunk);
                        break;
                    case LogLevel.INFO:
                        loggerInterface.info(tag, chunk);
                        break;
                    case LogLevel.WARN:
                        loggerInterface.warn(tag, chunk);
                        break;
                    case LogLevel.DEBUG:
                        loggerInterface.debug(tag, chunk);
                        break;
                    case LogLevel.VERBOSE:
                        loggerInterface.verbose(tag, chunk);
                        break;
                    case LogLevel.ASSERT:
                        loggerInterface.assertError(tag, chunk);
                        break;
                    default:
                        loggerInterface.verbose(tag, chunk);
                        break;
                }
            }
        }

    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LogPrinter.class.getName()) && !name.equals(RCLog.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private int getMethodCount() {
        Integer count = printer.getLocalMethodCount().get();
        int result = config.getMethodCount();

        if (count != null) {
            printer.getLocalMethodCount().remove();
            result = count;
        }

        if (result < 0) {
            throw new IllegalStateException("methodCount cannot be negative");
        }

        return result;
    }

}
