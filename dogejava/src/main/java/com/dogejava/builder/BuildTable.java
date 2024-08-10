package com.dogejava.builder;

import com.dogejava.bean.Constants;
import com.dogejava.bean.TableInfo;
import com.dogejava.bean.FieldInfo;
import com.dogejava.utils.JsonUtils;
import com.dogejava.utils.PropertiesUtils;

import com.dogejava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildTable {

    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    private  static Connection connection =null;
    private static String  SQL_SHOW_TABLE_STATUS ="show table status";
    private static String  SQL_SHOW_TABLE_FIELDS ="show full FIELDS from %s";
    private static String  SHOW_TABLE_INDEX ="show index from %s";
    static {
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String username = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            logger.error("数据库链接失败");
        }

    }

    public static List<TableInfo> getTables()  {

        PreparedStatement ps = null;
        ResultSet  tableResult= null;

        //
        List<TableInfo> tableInfoList = new ArrayList();
        try{
            ps = connection.prepareStatement(SQL_SHOW_TABLE_STATUS);
            tableResult = ps.executeQuery();
            while(tableResult.next()){
                String tableName = tableResult.getString("name");
                String tableComment = tableResult.getString("comment");



                String beanName= tableName;
                if(Constants.IGNORE_TABLE_PREFIX){
                    beanName= tableName.substring(beanName.indexOf("_")+1);

                }
                beanName=processField(beanName,true);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(tableComment);
                tableInfo.setBeanParamName(beanName+Constants.SUFFIX_BEAN_QUERY);

                readFieldInfo(tableInfo);

                getKeyIndexInfo(tableInfo);
//                logger.info("tableInfo: "+JsonUtils.convertObj2Json(tableInfo));
                tableInfoList.add(tableInfo);

            }
        }catch (Exception e){
            logger.info("读取表失败");

        }finally{
            if(tableResult!=null){
                try {
                    tableResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return  tableInfoList;
    }


    private static void readFieldInfo(TableInfo tableInfo){
        PreparedStatement ps = null;
        ResultSet  fieldResult= null;


        List<FieldInfo> FieldInfoList = new ArrayList();
        List<FieldInfo> fieldExtendList=new ArrayList();
        try{
            ps = connection.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS,tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            Boolean haveDate=false;
            Boolean haveBigDecimal=false;
            Boolean haveDateTime=false;
            while(fieldResult.next()){
                String field = fieldResult.getString("field");
                String type = fieldResult.getString("type");
                String extra = fieldResult.getString("extra");
                String comment = fieldResult.getString("comment");

                if(type.indexOf("(")>0){
                    type=type.substring(0,type.indexOf("("));
                }
                String propertyName=processField(field,false);

                FieldInfo fieldInfo = new FieldInfo();
                FieldInfoList.add(fieldInfo);


                fieldInfo.setFieldName(field);
                fieldInfo.setSqlType(type);
                fieldInfo.setComment(comment);
                fieldInfo.setAutoIncrement("".equalsIgnoreCase(extra)?true:false);
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(processJavaType(type));

                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,type)){

                    haveDateTime=true;
                }
                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,type)){

                    haveDate=true;
                }

                if(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES,type)){
                    haveBigDecimal=true;
                }

                if(ArrayUtils.contains(Constants.SQL_STRING_TYPES,type)){

                    FieldInfo fuzzyField = new FieldInfo();
                    fuzzyField.setJavaType(fieldInfo.getJavaType());
                    fuzzyField.setPropertyName(propertyName+Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    fuzzyField.setFieldName(fieldInfo.getFieldName());
                    fuzzyField.setSqlType(type);
                    fieldExtendList.add(fuzzyField);
                }


                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,type)||ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,type)){


                    FieldInfo timeStartFiled = new FieldInfo();
                    timeStartFiled.setJavaType("String");
                    timeStartFiled.setPropertyName(propertyName+Constants.SUFFIX_BEAN_QUERY_TIME_START);
                    timeStartFiled.setFieldName(fieldInfo.getFieldName());
                    timeStartFiled.setSqlType(type);
                    fieldExtendList.add(timeStartFiled);

                    FieldInfo timeEndFiled = new FieldInfo();
                    timeEndFiled.setJavaType("String");
                    timeEndFiled.setPropertyName(propertyName+Constants.SUFFIX_BEAN_QUERY_TIME_END);
                    timeEndFiled.setFieldName(fieldInfo.getFieldName());
                    timeEndFiled.setSqlType(type);
                    fieldExtendList.add(timeEndFiled);
                }

//

            }
            tableInfo.setHaveBigDecimal(haveBigDecimal);
            tableInfo.setHaveDate(haveDate);
            tableInfo.setHaveDateTime(haveDateTime);
            tableInfo.setFieldInfoList(FieldInfoList);
            tableInfo.setFieldExtends(fieldExtendList);

        }catch (Exception e){
            logger.info("读取表失败");

        }finally{
            if(fieldResult!=null){
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

    private static List<FieldInfo> getKeyIndexInfo(TableInfo tableInfo){
        PreparedStatement ps = null;
        ResultSet  fieldResult= null;

        List<FieldInfo> FieldInfoList = new ArrayList();
        try{
            Map<String,FieldInfo> tempMap = new HashMap();
            for(FieldInfo fieldInfo:tableInfo.getFieldInfoList()){
                tempMap.put(fieldInfo.getFieldName(),fieldInfo);
            }

            ps = connection.prepareStatement(String.format(SHOW_TABLE_INDEX,tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            while(fieldResult.next()){

                String keyName = fieldResult.getString("Key_name");
                Integer nonUnique = fieldResult.getInt("Non_unique");
                String columnName = fieldResult.getString("Column_name");
                if(nonUnique==1){
                    continue;
                }

                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);
                if(null==keyFieldList){
                    keyFieldList=new ArrayList<>();
                    tableInfo.getKeyIndexMap().put(keyName,keyFieldList);
                }
//                for(FieldInfo fieldInfo:tableInfo.getFieldInfoList()){
//
//                    if(fieldInfo.getFieldName().equals(columnName)){
//                        keyFieldList.add(fieldInfo);
//
//                    }
//                }
                keyFieldList.add(tempMap.get(columnName));


            }
        }catch (Exception e){
            logger.info("读取索引失败");

        }finally{
            if(fieldResult!=null){
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return FieldInfoList;
    }


//    表的名称首字母转大写

    private static String processField (String field,Boolean upperCaseFirstLetter){
        StringBuffer sb =new StringBuffer();
        String [] fields = field.split("_");
        sb.append(upperCaseFirstLetter? StringUtils.upperCaseFirstLetter(fields[0]) :fields[0]);
        for(int i=1,len=fields.length;i<len;i++){
            sb.append(StringUtils.upperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }

    private static String processJavaType(String type){
        if(ArrayUtils.contains(Constants.SQL_STRING_TYPES,type)){
            return "String";
        }
        else if(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES,type)){
            return "BigDecimal";
        }
        else if(ArrayUtils.contains(Constants.SQL_LONG_TYPES,type)){
            return "Long";
        } else if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPES,type)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,type)||ArrayUtils.contains(Constants.SQL_DATE_TYPES,type)) {
            return "Date";
        }
        else{
            throw new RuntimeException("无法识别的类型："+type);
        }
    }
}
