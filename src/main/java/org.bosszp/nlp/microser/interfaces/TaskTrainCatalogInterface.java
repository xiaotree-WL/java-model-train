package org.bosszp.nlp.microser.interfaces;

import java.io.IOException;

public interface TaskTrainCatalogInterface {
    String createCatalog(String oldCatalog, String fileName) throws IOException;
    String createFile(String path, String fileName) throws IOException;
    void monitorLogAndRetrieveAlter(String logName);
    String getLog(String taskName, int line) throws InterruptedException;
    void stopMonitorLog(String logName);
}
