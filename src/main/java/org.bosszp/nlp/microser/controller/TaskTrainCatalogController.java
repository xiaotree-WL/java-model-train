package org.bosszp.nlp.microser.controller;

import org.apache.log4j.helpers.FileWatchdog;
import org.bosszp.nlp.microser.interfaces.TaskTrainCatalogInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wanglin on 18-4-8.
 * 模型训练的相关目录管理
 */
@Service
public class TaskTrainCatalogController implements TaskTrainCatalogInterface {
    private static final Logger logger = LoggerFactory.getLogger(TaskTrainCatalogController.class);
    private static String fileSuffix = ".log"; //日志后缀名
    private static int maxCacheSize = 1000; //日志记录的大小
    private static Map<String, Long> lastTimeFileSizeMap = new ConcurrentHashMap<>();
    private static Map<String, ArrayBlockingQueue<String>> logMap = new HashMap<>();
    private static Map<String, GloablConfig> configMap = new ConcurrentHashMap<>(); // 启动线程监控文件变化

    @Override
    /**
     * 创建目录
     */
    public String createCatalog(String oldCatalog, String fileName) throws IOException {
        String resultCatalog;
        String separator = File.separator;
        String directory = oldCatalog + separator + fileName;
        Path pathDirec = Paths.get(directory);
        Path newPathDirec;
        if(!Files.exists(pathDirec)){
            newPathDirec = Files.createDirectory(pathDirec); // 创建目录
        }else{
            newPathDirec = pathDirec;
        }
        resultCatalog = newPathDirec.toString();
        return resultCatalog;
    }

    @Override
    /**
     * 创建文件
     */
    public String createFile(String path, String fileName) throws IOException {
        Path newPathFile;
        String resultCatalog;
        String separator = File.separator;
        String file = fileName + fileSuffix;
        String fileDirec = path + separator + file; // 得到全路径
        Path pathfile = Paths.get(fileDirec);
        if(!Files.exists(pathfile)){
            newPathFile = Files.createFile(pathfile); // 创建文件
        }else{
            newPathFile = pathfile;
        }
        resultCatalog = newPathFile.toString();
        return resultCatalog;
    }

    @Override
    /**
     * 监控日志的状态并且获取更新的内容
     */
    public void monitorLogAndRetrieveAlter(String logName) {
        if (!configMap.containsKey(logName)) {
            GloablConfig gloablconfig = new GloablConfig(logName);
            configMap.put(logName, gloablconfig);
            gloablconfig.setDelay(10000);
            gloablconfig.start();
        }
    }

    public void stopMonitorLog(String logName) {
        if (configMap.containsKey(logName)) {
            GloablConfig gloablconfig = configMap.get(logName);
            if (!gloablconfig.isAlive()) {
                configMap.remove(logName); // 终止线程
            }
        }
    }

    @Override
    /**
     * 返回最近的line行日志
     */
    public String getLog(String taskName, int line) throws InterruptedException {
        StringBuffer stringBuffer = new StringBuffer();
        boolean contains = logMap.containsKey(taskName);
        int size = 0;
        ArrayBlockingQueue<String> arrayBlockingQueueNew = new ArrayBlockingQueue<>(maxCacheSize);
        if (contains) {
            ArrayBlockingQueue<String> arrayBlockingQueue = logMap.get(taskName);
            size = arrayBlockingQueue.size();
            if (line > size) {
                line = size;
            }
            for (int i = 0; i < size; i++) {
                String temp = arrayBlockingQueue.poll();
                if (i >= size - line) {
                    stringBuffer.append(temp);
                    if (i != size -1) {
                        stringBuffer.append(":");
                    }
                }
                arrayBlockingQueueNew.put(temp);
            }
            logMap.put(taskName, arrayBlockingQueueNew);
        }
        return stringBuffer.toString();
    }

    public static class GloablConfig extends FileWatchdog
    {
        protected GloablConfig(String filename)
        {
            super(filename);
        }
        @Override
        protected void doOnChange()
        {
            System.out.println(filename);
            File logFile = new File(filename);
            String fileName = logFile.getName();
            String taskName = fileName.substring(0, fileName.lastIndexOf("."));
            try {
                final RandomAccessFile randomFile = new RandomAccessFile(logFile,"rw");
                boolean containsSize = lastTimeFileSizeMap.containsKey(taskName);
                long fileSize = 0;
                if (containsSize) {
                    fileSize = lastTimeFileSizeMap.get(taskName);
                }
                randomFile.seek(fileSize);
                String tmp;
                ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(maxCacheSize);
                boolean contains = logMap.containsKey(taskName);
                int size = 0;
                if (contains) {
                    arrayBlockingQueue = logMap.get(taskName);
                    size = arrayBlockingQueue.size();
                }
                while( (tmp = randomFile.readLine())!= null) {
                    if (size >= maxCacheSize) {
                        arrayBlockingQueue.poll();
                        arrayBlockingQueue.put(new String(tmp.getBytes("ISO8859-1")));
                    } else {
                        arrayBlockingQueue.put(new String(tmp.getBytes("ISO8859-1")));
                    }
                    logMap.put(taskName, arrayBlockingQueue);
                    System.out.println(new String(tmp.getBytes("ISO8859-1")));
                }
                fileSize = randomFile.length();
                lastTimeFileSizeMap.put(taskName,fileSize);
            } catch (FileNotFoundException e) {
                logger.error(e.toString());
            } catch (UnsupportedEncodingException e) {
                logger.error(e.toString());
            } catch (IOException e) {
                logger.error(e.toString());
            } catch (InterruptedException e) {
                logger.error(e.toString());
            }
        }
    }
}
