package org.bosszp.nlp.microser.controller;

public class ProcessInfo {
    String taskName;
    long pid;
    Process process;
    String logName;

    public ProcessInfo(String taskName, long pid, Process process, String logName) {
        this.taskName = taskName;
        this.pid = pid;
        this.process = process;
        this.logName = logName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }
}
