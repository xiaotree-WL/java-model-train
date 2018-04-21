package org.bosszp.nlp.microser.model;

/**
 * Created by wanglin on 18-4-8.
 * MongoDB数据库字段的脚本信息
 */
public class ParamInfo {
    String paramName; // 脚本名称
    String sourceAddress; // 脚本地址
    String toAddress;  // 生成模型地址
    String tempFuture; //以后使用的参数
    String configJson; // 执行脚本命令后面的参数

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getTempFuture() {
        return tempFuture;
    }

    public void setTempFuture(String tempFuture) {
        this.tempFuture = tempFuture;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }
}
