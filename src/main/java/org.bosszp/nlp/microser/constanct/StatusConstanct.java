package org.bosszp.nlp.microser.constanct;

/**
 * Created by wanglin on 18-4-8.
 * status字段的状态码
 */
public interface StatusConstanct {
    int create = 0;
    int init = 1;
    int running = 2;
    int success = 3;
    int fail = 4;
    int stop = 5;

    String createMsg = "create";
    String initMsg = "init";
    String runningMsg = "running";
    String successMsg = "success";
    String failMsg = "fail";
    String stopMsg = "stop";
    String unDefined = "undefined";
}
