package io.github.mavenreposs.component.log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * 文件输出Log
 * <p>
 * 将日志输出到文件中
 * </p>
 * <p>
 * 日志文件存储在外部存储空间的UELOG文件夹下
 * </p>
 */
public class FileLogTool implements LogInterface {
    // 日志文件保存的文件夹目录
    private static String LOG_DIR_PATH;

    private static HashMap<String, File> mLogFiles = new HashMap<>();

    /**
     * 日志文件存储路径为外部存储UELog文件夹下
     */
    public FileLogTool() {
        File file = new File("storages", "RCLog");
        LOG_DIR_PATH = file.getAbsolutePath();
    }

    @Override
    public void d(String tag, String message) {
        writeToFile(LogLevel.DEBUG, tag, message);
    }

    @Override
    public void e(String tag, String message) {
        writeToFile(LogLevel.ERROR, tag, message);
    }

    @Override
    public void w(String tag, String message) {
        writeToFile(LogLevel.WARN, tag, message);
    }

    @Override
    public void i(String tag, String message) {
        writeToFile(LogLevel.INFO, tag, message);
    }

    @Override
    public void v(String tag, String message) {
        writeToFile(LogLevel.VERBOSE, tag, message);
    }

    @Override
    public void wtf(String tag, String message) {
        writeToFile(LogLevel.ERROR, tag, message);
    }

    /**
     * 将日志写入文件中
     *
     * @param priority
     * @param tag
     * @param msg
     */
    private synchronized void writeToFile(int priority, String tag, String msg) {
        String trueTag = tag.split("\\[")[0];
        File logFile = mLogFiles.get(trueTag);
        if (logFile == null) {
            File logDir = new File(LOG_DIR_PATH, trueTag);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date(System.currentTimeMillis());
            String fileName = format.format(date) + ".log";
            logFile = new File(logDir, fileName);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                }
            }
            mLogFiles.put(trueTag, logFile);
        }
        BufferedWriter bufWriter = null;
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8");
            bufWriter = new BufferedWriter(out);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = new Date(System.currentTimeMillis());
            String priorityName = null;
            if (priority == LogLevel.VERBOSE) {
                priorityName = "V";
            } else if (priority == LogLevel.INFO) {
                priorityName = "I";
            } else if (priority == LogLevel.DEBUG) {
                priorityName = "D";
            } else if (priority == LogLevel.WARN) {
                priorityName = "W";
            } else if (priority == LogLevel.ERROR) {
                priorityName = "E";
            } else {
                priorityName = "V";
            }
            bufWriter.write(format.format(date) + ": " + priorityName + "/" + tag + ": " + msg + "\r\n");
        } catch (Exception e) {
        } catch (Error error) {
        } finally {
            if (bufWriter != null) {
                try {
                    bufWriter.close();
                } catch (Exception e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
