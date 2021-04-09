# component-log

## RCLog

美化终端日志打印输出。

#### 用来打印调试信息

```text
RCLog::debug("用来打印调试信息");
```

#### 用来打印一般提示信息

```text
RCLog::info("用来打印一般提示信息");
```

#### 用来打印警告信息，这种信息一般是提示开发者需要注意，有可能会出现问题！

```text
RCLog::warn("用来打印警告信息，这种信息一般是提示开发者需要注意，有可能会出现问题！");
```

#### 用来打印错误崩溃日志信息，例如在try-catch的catch中输出捕获的错误信息。

```text
RCLog::error("用来打印错误崩溃日志信息，例如在try-catch的catch中输出捕获的错误信息。");
```

#### 用来打印xml字符串格式的调试信息

```text
RCLog::xml("<?xml version="1.0" encoding="UTF-8"?><html><title>this is a title</title><body>这个是网页</body></html>");
```

#### 用来打印json字符串格式的调试信息

```text
RCLog::json("{"id":221,"name":"my name is RCLog","desc":"this is description!"}");
```

#### 用来打印Map/List/POJO可以被JSON化的Object对象调试信息

```text
User user = new User();
user.id = 102;
user.name = "RCLog";
user.age = 22;
RCLog::object(user);
```

#### 用来打印换行日志
```text
RCLog.info("第一行日志\n换行输出日志");
```

#### 用来打印异常信息
```text
try {
    Object obj = null;
    obj.toString();
} catch (Exception e) {
    RCLog.error(e, "空指针异常");
}
```