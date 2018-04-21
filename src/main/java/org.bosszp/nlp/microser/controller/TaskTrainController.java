package org.bosszp.nlp.microser.controller;

import org.bosszp.nlp.microser.constanct.StatusConstanct;
import org.bosszp.nlp.microser.interfaces.TaskTrainCatalogInterface;
import org.bosszp.nlp.microser.interfaces.TaskTrainInterface;
import org.bosszp.nlp.microser.model.ParamInfo;
import org.bosszp.nlp.microser.model.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by wanglin on 18-4-8.
 * 模型训练的相关操作管理
 */
@Service
public class TaskTrainController implements TaskTrainInterface {
    private static final Logger logger = LoggerFactory.getLogger(TaskTrainController.class);

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TaskTrainCatalogInterface taskTrainCatalogInterface;
    @Autowired
    private TaskTrainProcessManager taskTrainProcessManager;

    @Value("#{ '${train.model.relative-model-path}' }")
    private String modelPath; //模型存放相对路径
    @Value("#{ '${train.model.relative-log-path}' }")
    private String logPath; //日志存放相对路径
    @Value("#{ '${train.model.relative-shell-path}' }")
    private String shellPath; //脚本存放相对路径

    @Override
    /**
     * 根据task的名称查询mongo数据库，得到task的相关信息
     */
    public TaskInfo openCreate(String name) {
        TaskInfo taskInfo = taskRepository.findByName(name);
        return taskInfo;
    }

    @Override
    /**
     * 更新task的某个字段值
     */
    public boolean updateItem(String name, String item, String itemValue) {
        Query query = new Query();
        Criteria criteria = Criteria.where("name").in(name);
        query.addCriteria(criteria);
        Update update = new Update().set(item, itemValue);
        update.set("updateDate", System.currentTimeMillis());
        mongoTemplate.upsert(query, update, TaskInfo.class);
        return true;
    }

    @Override
    /**
     * 保存task信息
     */
    public boolean insertTaskInfo(String taskName, String cmd, String configJson, String paramName, String sourceAddress) {
        // 设置task信息
        TaskInfo taskInfo = new TaskInfo();
        ParamInfo paramInfo = new ParamInfo();
        taskInfo.setName(taskName);
        taskInfo.setCmd(cmd);
        taskInfo.setStatus(StatusConstanct.create);
        paramInfo.setParamName(paramName);
        paramInfo.setConfigJson(configJson);
        paramInfo.setSourceAddress(sourceAddress);
        taskInfo.setParamInfo(paramInfo);
        mongoTemplate.insert(taskInfo);
        return true;
    }

    @Override
    /**
     * 更新task的status字段
     */
    public boolean updateStatus(String name, int status) {
        Query query = new Query();
        Criteria criteria = Criteria.where("name").in(name);
        query.addCriteria(criteria);
        Update update = new Update().set("status", status);
        update.set("updateDate", System.currentTimeMillis());
        mongoTemplate.upsert(query, update, TaskInfo.class);
        return true;
    }

    @Override
    /**
     * 训练模型
     */
    public boolean startTrain(String name) {
        TaskInfo taskInfo = this.openCreate(name);
        String taskName = taskInfo.getName();
        String cmd = taskInfo.getCmd();
        String configJson = taskInfo.getParamInfo().getConfigJson();
        try {
            // 创建以task命名的日志文件
            String logCatalog = taskTrainCatalogInterface.createCatalog(logPath, taskName);
            String logName = taskTrainCatalogInterface.createFile(logCatalog, taskName);
            // 创建以task命名的模型文件
            String modelName = taskTrainCatalogInterface.createCatalog(modelPath, taskName);
            String configCommand = cmd + " " + logName + " " + modelName + " " + configJson;
            // 更新task的status
            updateStatus(taskName, StatusConstanct.running);
            taskTrainProcessManager.startProcess(taskName, configCommand, logName);
            System.out.println("end");
            updateStatus(taskName, StatusConstanct.success);
        } catch (IOException e) {
            updateStatus(taskName, StatusConstanct.fail);
            logger.error(e.toString());
        } catch (IllegalAccessException e) {
            updateStatus(taskName, StatusConstanct.fail);
            logger.error(e.toString());
        } catch (ClassNotFoundException e) {
            updateStatus(taskName, StatusConstanct.fail);
            logger.error(e.toString());
        } catch (NoSuchFieldException e) {
            updateStatus(taskName, StatusConstanct.fail);
            logger.error(e.toString());
        }
        return true;
    }

    @Override
    /**
     * 获取日志
     */
    public String getLog(String name, int i) {
        // 获取数据库信息
        TaskInfo taskInfo = this.openCreate(name);
        String resultLog = null;
        try {
            resultLog = taskTrainCatalogInterface.getLog(taskInfo.getName(), i);
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
        return resultLog;
    }

    @Override
    /**
     * java会在后台进程中开启一个cmd.exe的进程，当我们想要杀死该进程的时候使用process.destroy(),
     * 只能杀死java程序启动的cmd.exe,无法杀死cmd.exe创建的进程，也就是说无法杀死进程树,通过taskkill杀死进程树
     */
    public boolean stopTrain(String taskName) {
        try {
            taskTrainProcessManager.stopProcess(taskName);
            updateStatus(taskName, StatusConstanct.stop);
        } catch (Exception e) {
            updateStatus(taskName, StatusConstanct.fail);
            logger.error("根据pid杀死进程树错误！");
            logger.error(e.toString());
        }
        return true;
    }

    @Override
    /**
     * 获取模型
     */
    public String getModel(String taskName) {
        String separator = File.separator;
        String directory = modelPath + separator + taskName;
        File file = new File(directory); //获得指定文件对象
        File[] files = file.listFiles(); // 获得该文件夹内的所有文件
        String pathName = null; //获得文件的绝对路径
        try {
            pathName = files[0].getCanonicalPath(); //只取第一个文件，后面可以根据传入的文件名获取指定的文件
        } catch (IOException e) {
            logger.error(e.toString());
        }
        return pathName;
    }

}
