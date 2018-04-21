package org.bosszp.nlp.microser.model;

import org.bosszp.nlp.microser.constanct.StatusConstanct;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by wanglin on 18-4-8.
 * MongoDB数据库字段
 */
@Document(collection = "wlnlptest")
public class TaskInfo {
    @Id
    String id;
    String name;
    String errorMessage;
    String cmd;
    int status; // 0:创建 1:初始化 2:运行 3:成功 4:错误 5:停止
    ParamInfo paramInfo; // Json格式的脚本信息
    Long createDate; // 创建时间
    Long updateDate; // 更新时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ParamInfo getParamInfo() {
        return paramInfo;
    }

    public void setParamInfo(ParamInfo paramInfo) {
        this.paramInfo = paramInfo;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        switch(status){
            case StatusConstanct
                    .create:
                builder.append(" ");
                builder.append(StatusConstanct.createMsg);
                break;
            case StatusConstanct.init:
                builder.append(" ");
                builder.append(StatusConstanct.initMsg);
                break;
            case StatusConstanct.running:
                builder.append(" ");
                builder.append(StatusConstanct.runningMsg);
                break;
            case StatusConstanct.success:
                builder.append(" ");
                builder.append(StatusConstanct.successMsg);
                break;
            case StatusConstanct.fail:
                builder.append(" ");
                builder.append(StatusConstanct.failMsg);
                break;
            case StatusConstanct.stop:
                builder.append(" ");
                builder.append(StatusConstanct.stopMsg);
                break;
            default:
                builder.append(" ");
                builder.append(StatusConstanct.unDefined);
                break;
        }
        if (cmd != null && !cmd.isEmpty()) {
            builder.append(" ");
            builder.append(cmd);
        }
        return builder.toString();
    }
}
