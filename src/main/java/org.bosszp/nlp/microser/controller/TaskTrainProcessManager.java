package org.bosszp.nlp.microser.controller;

import com.sun.jna.Platform;
import org.bosszp.nlp.microser.interfaces.Kernel32;
import org.bosszp.nlp.microser.interfaces.TaskTrainCatalogInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wanglin on 18-4-8.
 * 模型训练的相关进程管理
 */
@Component
public class TaskTrainProcessManager {
    private Map<String, ProcessInfo> processInfoMap = new ConcurrentHashMap<>();
    @Autowired
    TaskTrainCatalogInterface taskTrainCatalogInterface;

    public void startProcess(String taskName, String cmd, String logName) throws IOException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        taskTrainCatalogInterface.monitorLogAndRetrieveAlter(logName);
        Process pr = Runtime.getRuntime().exec(cmd);
        long pid = getPID(pr);
        ProcessInfo processInfo = new ProcessInfo(taskName, pid, pr, logName);
        processInfoMap.put(processInfo.taskName, processInfo);
        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
    }

    /**
     * 根据系统的不同获取pid的方法不一样
     * @param pr
     * @return
     */
    private long getPID(Process pr) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        long pid = -1;
        Field field;
        if (Platform.isWindows()) {
            field = pr.getClass().getDeclaredField("handle");
            field.setAccessible(true);
            pid = Kernel32.INSTANCE.GetProcessId((Long) field.get(pr));
        } else if (Platform.isLinux() || Platform.isAIX()) {
            Class<?> clazz = Class.forName("java.lang.UNIXProcess");
            field = clazz.getDeclaredField("pid");
            field.setAccessible(true);
            pid = (Integer) field.get(pr);
        } else {
        }
        return pid;
    }

    public void stopProcess(String taskName) throws IOException, InterruptedException {
        if (!processInfoMap.containsKey(taskName))
            return;
        ProcessInfo processInfo = processInfoMap.get(taskName);
        if (processInfo.getProcess().isAlive()) {
            try {
                if (Platform.isWindows()) {
                    String cmd = "cmd.exe /c taskkill /PID " + processInfo.getPid() + " /F /T ";
                    Runtime rt = Runtime.getRuntime();
                    Process killP = rt.exec(cmd);
                    killP.waitFor();
                    killP.destroy();
                } else if (Platform.isLinux() || Platform.isAIX()) {
                    String cmdChildren = "pkill -9 -P "+ processInfo.getPid(); //根据父进程PID终止所有子进程PID，保留父进程
                    String cmdParent = "kill -9 "+ processInfo.getPid(); //终止父进程
                    Runtime rt = Runtime.getRuntime();
                    Process killChildren = rt.exec(cmdChildren);
                    killChildren.waitFor();
                    killChildren.destroy();
                    Process killParent = rt.exec(cmdParent);
                    killParent.waitFor();
                    killParent.destroy();
                } else {
                }
            } catch (IOException e) {
                throw e;
            } catch (InterruptedException e) {
                throw e;
            } finally {
                taskTrainCatalogInterface.stopMonitorLog(processInfo.getLogName());
                processInfoMap.remove(processInfo.taskName);
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    public void checkProcessStatus() {
        Iterator<Map.Entry<String, ProcessInfo>> iter = processInfoMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, ProcessInfo> item = iter.next();
            if (!item.getValue().getProcess().isAlive()) {
                taskTrainCatalogInterface.stopMonitorLog(item.getValue().getLogName());
                iter.remove();
            }
        }
    }
}
