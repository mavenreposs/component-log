package io.github.mavenreposs.component.log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 日志打印机
 * <p>
 * 负责日志的格式化和打印
 * </p>
 */
public final class LogPrinter {

    /**
     * Json缩紧
     */
    private static final int JSON_INDENT = 4;

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
    private LogConfig mLogConfig = new LogConfig();

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

    LogConfig getLogConfig() {
        return mLogConfig;
    }

    public LogPrinter append(String message, Object... args) {
        String msg = createMessage(message, args);
        if (!isEmpty(msg)) {
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
        if (!isEmpty(msg)) {
            List<String> msgList = localMessageList.get();
            if (msgList == null) {
                msgList = new ArrayList<>();
                localMessageList.set(msgList);
            }
            msgList.add(msg);
        }
        return this;
    }

    public LogPrinter appendXml(String xml) {
        String msg = parseXmlMessage(xml);
        if (!isEmpty(msg)) {
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
        if (!isEmpty(msg)) {
            List<String> msgList = localMessageList.get();
            if (msgList == null) {
                msgList = new ArrayList<>();
                localMessageList.set(msgList);
            }
            msgList.add(msg);
        }
        return this;
    }

    public void d(String message, Object... args) {
        log(LogLevel.DEBUG, message, args);
    }

    public void e(String message, Object... args) {
        e(null, message, args);
    }

    public void e(Throwable throwable, String message, Object... args) {
        if (throwable != null && message != null) {
            message += " : " + getStackTraceString(throwable);
        }
        if (throwable != null && message == null) {
            message = throwable.toString();
        }
        if (message == null) {
            message = "No message/exception is set";
        }
        log(LogLevel.ERROR, message, args);
    }

    public void w(String message, Object... args) {
        log(LogLevel.WARN, message, args);
    }

    public void i(String message, Object... args) {
        log(LogLevel.INFO, message, args);
    }

    public void v(String message, Object... args) {
        log(LogLevel.VERBOSE, message, args);
    }

    public void wtf(String message, Object... args) {
        log(LogLevel.ASSERT, message, args);
    }

    public void json(String json) {
        d(parseJsonMessage(json));
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public void xml(String xml) {
        d(parseXmlMessage(xml));
    }

    /**
     * Formats the obj content and print it
     *
     * @param obj the xml content
     */
    public void object(Object obj) {
        d(parseObjectMessage(obj));
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
        int methodCount = getMethodCount();

        if (isEmpty(message)) {
            message = "Empty/NULL log message";
        }

        List<String> appendMsgList = localMessageList.get();
        localMessageList.remove();

        if (methodCount <= 0 && (appendMsgList == null || appendMsgList.size() == 0) && !message.contains(System.getProperty("line.separator"))) {
            //如果只是单行日志，则不加边框直接输出
            logChunk(logType, tag, message, isPrintToFile);
        } else {
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
        List<LogInterface> logInterfaces = mLogConfig.getLoggers();
        for (LogInterface logInterface : logInterfaces) {
            if (!(logInterface instanceof FileLogTool) || printToFile) {
                switch (logType) {
                    case LogLevel.ERROR:
                        logInterface.e(tag, chunk);
                        break;
                    case LogLevel.INFO:
                        logInterface.i(tag, chunk);
                        break;
                    case LogLevel.VERBOSE:
                        logInterface.v(tag, chunk);
                        break;
                    case LogLevel.WARN:
                        logInterface.w(tag, chunk);
                        break;
                    case LogLevel.ASSERT:
                        logInterface.wtf(tag, chunk);
                        break;
                    case LogLevel.DEBUG:
                        logInterface.d(tag, chunk);
                        break;
                    default:
                        logInterface.v(tag, chunk);
                        break;
                }
            }
        }

    }

    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    /**
     * @return the appropriate tag based on local or global
     */
    private String getTag() {
        String tag = localTag.get();
        if (!isEmpty(tag)) {
            localTag.remove();
            return tag;
        }
        tag = mLogConfig.getTag();
        if (!isEmpty(tag)) {
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

    private int getMethodCount() {
        Integer count = localMethodCount.get();
        int result = mLogConfig.getMethodCount();
        if (count != null) {
            localMethodCount.remove();
            result = count;
        }
        if (result < 0) {
            throw new IllegalStateException("methodCount cannot be negative");
        }
        return result;
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

    /**
     * 格式化xml字符串
     *
     * @param xml
     * @return
     */
    private String parseXmlMessage(String xml) {
        if (LogLevel.DEBUG < mLogConfig.getLogLevel()) {
            //因为对象输出是以debug级别输出的，所以如果日志级别配置高于DEBUG等级，则不会输出，所以也不需要进行字符串格式化
            return null;
        }
        if (!isEmpty(xml)) {
            try {
                Source xmlInput = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(xmlInput, xmlOutput);
                return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
            } catch (TransformerException e) {
                return "Invalid xml content";
            }
        } else {
            return "Empty/Null xml content";
        }
    }

    /**
     * 格式化json字符串
     *
     * @param json
     * @return
     */
    private String parseJsonMessage(String json) {
        if (LogLevel.DEBUG < mLogConfig.getLogLevel()) {
            //因为对象输出是以debug级别输出的，所以如果日志级别配置高于DEBUG等级，则不会输出，所以也不需要进行字符串格式化
            return null;
        }
        if (!isEmpty(json)) {
            try {
                json = json.trim();
                if (json.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(json);
                    String message = jsonObject.toString(JSON_INDENT);
                    return message;
                } else if (json.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(json);
                    String message = jsonArray.toString(JSON_INDENT);
                    return message;
                } else {
                    return "Invalid json content";
                }
            } catch (JSONException e) {
                return "Invalid json content";
            }
        } else {
            return "Empty/Null json content";
        }
    }

    /**
     * 格式化对象
     *
     * @param obj
     * @return
     */
    private String parseObjectMessage(Object obj) {
        if (LogLevel.DEBUG < mLogConfig.getLogLevel()) {
            //因为对象输出是以debug级别输出的，所以如果日志级别配置高于DEBUG等级，则不会输出，所以也不需要进行字符串格式化
            return null;
        }
        if (obj != null) {
            try {
                if (obj instanceof List) {
                    JSONArray jsonArray = new JSONArray();
                    for (Object o : (List) obj) {
                        JSONObject jo = new JSONObject(new Gson().toJson(o));
                        jsonArray.put(jo);
                    }
                    String message = jsonArray.toString(JSON_INDENT);
                    return message;
                } else if (obj instanceof Map) {
                    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                    JSONObject jsonObject = new JSONObject(gson.toJson(obj));
                    String message = jsonObject.toString(JSON_INDENT);
                    return message;
                } else {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(obj));
                    String message = jsonObject.toString(JSON_INDENT);
                    return message;
                }
            } catch (JSONException e) {
                return "Invalid object content";
            }
        } else {
            return "Null object content";
        }
    }

    /**
     * Returns true if the string is null or 0-length.
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     * @param tr An exception to log
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

}
