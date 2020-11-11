package org.landy.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @author ：MCI10935
 * @date ：Created in 11/9/2020 2:03 PM
 * @description：
 * @version:
 */
public class TestWatcherService extends Thread{

    private WatchService watcher;
    Instrumentation inst;

    public TestWatcherService(String path) throws IOException {
        this.path = path;
    }

    String path;

    public TestWatcherService(String path, Instrumentation inst)
            throws IOException {
        this.inst = inst;
        this.path = path;
    }

    List<String> list = null;


    @Override
    public void run() {

        try {
            // ff.listPath;
            FileUtil.readfile(path, null);

            list = FileUtil.arr;

            watcher = FileSystems.getDefault().newWatchService();

            Path path2 = Paths.get(path);

            path2.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            for (String p : list) {
                Path path = Paths.get(p);
                path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            }

            handleEvents();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void handleEvents() throws InterruptedException {
        while (true) {
            WatchKey key = watcher.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // 事件可能lost or discarded
                if (kind == OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> e = (WatchEvent<Path>) event;
                Path fileName = e.context();
                new ReloadTask(inst).run();
                System.out.printf(
                        "重载类 Event %s has happened,which fileName is %s%n",
                        kind.name(), fileName);
            }
            if (!key.reset()) {
                break;
            }
        }
    }

    public static void main(String args[]) throws IOException,
            InterruptedException {
        TestWatcherService d = new TestWatcherService(("D:\\ziyuan"));

        d.start();
        /*
         * if(args.length!=1){ System.out.println("请设置要监听的文件目录作为参数");
         * System.exit(-1); }
         */
        Hello hello= new Hello();
        while (true){
            TimeUnit.SECONDS.sleep(1);
            hello.say();
        }

    }


}
