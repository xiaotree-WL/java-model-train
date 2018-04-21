package org.bosszp.nlp.microser.interfaces;


import org.bosszp.nlp.microser.model.TaskInfo;

public interface TaskTrainInterface {
    TaskInfo openCreate(String name);
    boolean updateItem(String name, String item, String itemValue);
    boolean insertTaskInfo(String taskName, String cmd, String configJson, String paramName, String sourceAddress);
    boolean updateStatus(String name, int status);
    boolean startTrain(String name);
    String getLog(String name, int i);
    boolean stopTrain(String taskName);
    String getModel(String taskName);
}
