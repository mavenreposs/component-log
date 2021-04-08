package io.github.mavenreposs.component.log;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLog {

    @Test
    public void testLog() {
        RCLog.init("AAA");
//        RCLog.init("AAA", 2);
//        RCLog.init("AAA", 3, LogLevel.INFO);
//        RCLog.init("AAA", 4, LogLevel.INFO, true);

        //输出Json字符串
        RCLog.json("{\"id\":221,\"name\":\"my name is ueueo\",\"desc\":\"this is description!\"}");
        //输出Xml字符串
        RCLog.xml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><html><title>this is a title</title><body>这个是网页</body></html>");

        //创建Java对象
        User user = new User();
        user.id = 102;
        user.name = "RCLOG";
        user.age = 22;
        //输出对象
        RCLog.object(user);


//        Logger logger = LoggerFactory.getLogger(this.getClass());
//        logger.info("test");

        System.out.println("Done.");
    }

    @Test
    public void test2() {
        RCLog.i("第一行日志 \n 换行输出日志");

        try {
            Object obj = null;
            obj.toString();
        } catch (Exception e) {
            RCLog.e(e, "空指针异常");
        }
    }


    private static class User {

        public Integer id;

        public String name;

        public Integer age;

    }

}
