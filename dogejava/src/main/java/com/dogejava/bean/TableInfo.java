package com.dogejava.bean;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableInfo {
//    表名
    private String tableName;

//    bean的名称
    private String beanName;

//    参数名称
    private String beanParamName;

//    表的注释
    private String comment;

//    字段信息
    private List<FieldInfo> fieldInfoList;
//      扩展字段信息
    private List<FieldInfo> fieldExtends;

//    唯一索引集合

    private Map<String, List<FieldInfo>> keyIndexMap =new  LinkedHashMap();

//   是否有date类型

    private Boolean haveDate;

//    是否有时间类型

    private Boolean haveDateTime;


//    是否有bigDecimal类型

    private Boolean haveBigDecimal;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanParamName() {
        return beanParamName;
    }

    public void setBeanParamName(String beanParamName) {
        this.beanParamName = beanParamName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }

    public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
        this.fieldInfoList = fieldInfoList;
    }

    public Map<String, List<FieldInfo>> getKeyIndexMap() {
        return keyIndexMap;
    }

    public void setKeyIndexMap(Map<String, List<FieldInfo>> keyIndexMap) {
        this.keyIndexMap = keyIndexMap;
    }

    public Boolean getHaveDate() {
        return haveDate;
    }

    public void setHaveDate(Boolean haveDate) {
        this.haveDate = haveDate;
    }

    public Boolean getHaveDateTime() {
        return haveDateTime;
    }

    public void setHaveDateTime(Boolean haveDateTime) {
        this.haveDateTime = haveDateTime;
    }

    public Boolean getHaveBigDecimal() {
        return haveBigDecimal;
    }

    public void setHaveBigDecimal(Boolean haveBigDecimal) {
        this.haveBigDecimal = haveBigDecimal;
    }

    public List<FieldInfo> getFieldExtends() {
        return fieldExtends;
    }

    public void setFieldExtends(List<FieldInfo> fieldExtends) {
        this.fieldExtends = fieldExtends;
    }
}
