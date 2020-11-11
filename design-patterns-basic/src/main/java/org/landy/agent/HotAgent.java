package org.landy.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.net.URLDecoder;
import java.util.Set;
import java.util.TreeSet;
/**
 * @author ：MCI10935
 * @date ：Created in 11/9/2020 2:06 PM
 * @description：
 * @version:
 */
public class HotAgent {
    protected static Set<String> clsnames = new TreeSet<String>();

    protected static String path;

    protected static String packages;
    public static void premain(String agentArgs, Instrumentation inst)
            throws Exception {

        String arr[] = agentArgs.split(",");

        for (String s2 : arr) {
            if (s2.startsWith("path")) {
                path = s2.split("=")[1];
            } else if (s2.startsWith("package")) {
                packages = s2.split("=")[1];
                packages=  packages.replace(".","/");
            }
        }
        if (packages == null) {
            System.err
                    .print("路径找不到哦 请指定路径：如  -javaagent:D:\\agent.jar=path=d,package=d:\\test\\classes");
        }
        if (path != null) {
        } else {

            try {

                String d2 = Thread.currentThread().getContextClassLoader()
                        .getResource("")
                        + "";
                System.out.println("class path" + d2);
                path = d2.substring(6);
                path=URLDecoder.decode(path,"utf-8");//关键啊 ！
            } catch (Exception ex) {
                System.err
                        .print("路径找不到哦 请指定路径：如  -javaagent:D:\\agent.jar=path=d,package=d:\\test\\classes");
            }
        }

        System.out.println("path:" + path+" \r\n packages:"+packages);
        ClassFileTransformer transformer = new ClassTransform(inst);

        inst.addTransformer(transformer);
        new ReloadTask(inst).run();
        System.out.println("是否支持类的重定义：" + inst.isRedefineClassesSupported());

        // Timer timer=new Timer();
        // D:\\Server-SGame\\trunk\\sgame-controllers\\target\\classes
        TestWatcherService d = new TestWatcherService((path), inst);
        d.start();
        // timer.schedule(new ReloadTask(inst),2000,2000);

    }

}
