package org.bosszp.nlp.microser;

import org.bosszp.nlp.microser.interfaces.TaskTrainInterface;
import org.bosszp.nlp.microser.model.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.io.File;

@Controller
public class TrainModelController {

    @Autowired
    private TaskTrainInterface taskTrainInterface;

    @RequestMapping("/stopTrain/{taskName}")
    public String stopTrain(ModelMap map, @PathVariable("taskName") String taskName) {
        // 停止训练模型
        boolean result = taskTrainInterface.stopTrain(taskName);
        // 加入一个属性，用来在模板中读取
        map.addAttribute("host", result);
        // return模板文件的名称，对应src/main/resources/templates/hello.html
        return "hello";
    }

    @RequestMapping("/updateInfo/{name}/{cmd}")
    public String updateInfo(ModelMap map, @PathVariable("name") String name, @PathVariable("cmd") String cmd) {
        // 更新configUrl信息
        boolean result = taskTrainInterface.updateItem(name, "cmd", cmd);
        // 加入一个属性，用来在模板中读取
        map.addAttribute("host", result);
        // return模板文件的名称，对应src/main/resources/templates/hello1.html
        return "hello1";
    }

    @RequestMapping("/startTrain/{name}")
    public String startTrain(ModelMap map, @PathVariable("name") String name) {
        // 步骤：startTrain，开始训练模型，然后输出训练好的模型
        boolean result = taskTrainInterface.startTrain(name);
        // 加入一个属性，用来在模板中读取
        map.addAttribute("host", result);
        // return模板文件的名称，对应src/main/resources/templates/hello2.html
        return "hello2";
    }

    @RequestMapping("/getLog/{name}/{line}")
    public String getLog(ModelMap map, @PathVariable("name") String name, @PathVariable("line") int line) {
        // 步骤：getLog，获取训练模型的日志
        String result = taskTrainInterface.getLog(name, line);
        // 加入一个属性，用来在模板中读取
        map.addAttribute("host", result.isEmpty() ? "nothingToShow":result );
        // return模板文件的名称，对应src/main/resources/templates/hello3.html
        return "hello3";
    }

    @RequestMapping("/getTask/{taskName}")
    public String getTask(ModelMap map, @PathVariable("taskName") String taskName) {
        // 获取数据库信息
        TaskInfo taskInfo = taskTrainInterface.openCreate(taskName);
        // 加入一个属性，用来在模板中读取
        map.addAttribute("host", taskInfo.toString());
        // return模板文件的名称，对应src/main/resources/templates/index.html
        return "index";
    }

    @RequestMapping("/insert/{taskName}")
    public String insert(ModelMap map, @PathVariable("taskName") String taskName) {
        String cmdString = "python E:/taskTrain/taskTrainShell/test.py";
        String configJson = "INFO tasktest1";
        String paramName = "test.py";
        String sourceAddress = "E:/taskTrain/taskTrainShell/test.py";
        boolean result = taskTrainInterface.insertTaskInfo(taskName, cmdString, configJson, paramName, sourceAddress);
        // 加入一个属性，用来在模板中读取
        map.addAttribute("host", result);
        return "insert";
    }

    @RequestMapping("/getModel/{taskName}")
    public void getModel(HttpServletResponse response, ModelMap map, @PathVariable("taskName") String taskName) {
        String result = taskTrainInterface.getModel(taskName);
        File file = new File(result);
        String filename = file.getName();
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
        int len = 0;
        byte[] buffer = new byte[1024];
        try {
            InputStream in = new FileInputStream(result);
            OutputStream out = response.getOutputStream();
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer,0,len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

