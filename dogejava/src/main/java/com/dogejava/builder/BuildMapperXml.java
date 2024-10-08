package com.dogejava.builder;

import com.dogejava.bean.Constants;
import com.dogejava.bean.FieldInfo;
import com.dogejava.bean.TableInfo;
import com.dogejava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildMapperXml {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);
    private static final String BASE_COLUMN_LIST = "base_column_list";
    private static final String BASE_QUERY_CONDITION = "base_query_condition";
    private static final String BASE_QUERY_CONDITION_EXTENDS = "base_query_condition_extends";
    private static final String QUERY_CONDITION = "query_condition";
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPERS_XMLS);
        if (!folder.exists()) {
            folder.mkdirs();

        }
        String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
        File poFile= new File(folder,className+".xml");
        OutputStream out = null;
        OutputStreamWriter osw=null;
        BufferedWriter bw = null;
        try{
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(osw);




            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            bw.newLine();
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"");
            bw.newLine();
            bw.write("\t\t\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            bw.write("<mapper namespace=\""+Constants.PACKAGE_MAPPERS+"."+className+"\">");
            bw.newLine();

            bw.write("\t<!--实体映射-->");
            bw.newLine();

            String poClassName=Constants.PACKAGE_PO+"."+tableInfo.getBeanName();
            bw.write("\t<resultMap id=\"base_result_map\" type=\""+poClassName+"\">");
            bw.newLine();

            FieldInfo idField = null;
            Map<String,List<FieldInfo>> KeyIndexMap = tableInfo.getKeyIndexMap();
            for(Map.Entry<String,List<FieldInfo>> entry: KeyIndexMap.entrySet()){
                if("PRIMARY".equals(entry.getKey())){
                    List<FieldInfo> fieldInfoList = entry.getValue();
                    if(fieldInfoList.size()==1){
                        idField = fieldInfoList.get(0);
                        break;
                    }
                }
            }
            for(FieldInfo fieldInfo : tableInfo.getFieldInfoList()){
                bw.write("\t<!--"+fieldInfo.getComment()+"-->");
                bw.newLine();
                String key ="";
                if(idField!=null && fieldInfo.getPropertyName().equals(idField.getPropertyName())){
                    key="id";
                }else {
                    key="result";
                }
                bw.write("\t<"+key+" column=\""+fieldInfo.getFieldName()+"\" property=\""+fieldInfo.getPropertyName()+"\" />");
                bw.newLine();
            }

            bw.write("\t</resultMap>");
            bw.newLine();

            //通用查询列
            bw.write("\t<!--通用查询结果列-->");
            bw.newLine();


            bw.write("\t<sql id=\""+BASE_COLUMN_LIST+"\">");
            bw.newLine();
            bw.write("\t\t");
            StringBuilder sb = new StringBuilder();
            for(FieldInfo fieldInfo : tableInfo.getFieldInfoList()){
                sb.append(fieldInfo.getFieldName()).append(",");
            }
           String sbstr =sb.substring(0,sb.lastIndexOf(","));
            bw.write("\t"+sbstr);
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            //基础查询条件
            bw.write("\t<!--通用查询结果列-->");
            bw.newLine();
            bw.write("\t<sql id=\""+BASE_QUERY_CONDITION+"\">");
            bw.newLine();

            for(FieldInfo fieldInfo : tableInfo.getFieldInfoList()){
                String stringQuery="";
                if(ArrayUtils.contains(Constants.SQL_STRING_TYPES,fieldInfo.getSqlType())){
                    stringQuery=" and query."+fieldInfo.getPropertyName()+"!=''";

                }
                bw.newLine();
                bw.write("\t\t<if test=\"query."+fieldInfo.getPropertyName()+"!=null"+stringQuery+"\">");
                bw.newLine();
                bw.write("\t\t\tand "+fieldInfo.getFieldName()+" = #{query."+fieldInfo.getPropertyName()+"}");
                bw.newLine();
                bw.write("\t\t</if>");
            }

            bw.write("\t</sql>");
            bw.newLine();

            //扩展查询条件
            bw.write("\t<!--扩展查询条件-->");
            bw.newLine();

            bw.write("\t<sql id=\""+BASE_QUERY_CONDITION_EXTENDS+"\">");
            bw.newLine();

            for(FieldInfo fieldInfo : tableInfo.getFieldExtends()){
                String andWhere="";
                if(ArrayUtils.contains(Constants.SQL_STRING_TYPES,fieldInfo.getSqlType())){
                    andWhere="and "+fieldInfo.getFieldName()+" like concat('%', #{query."+fieldInfo.getPropertyName()+"}, '%')";

                } else if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,fieldInfo.getSqlType())||ArrayUtils.contains(Constants.SQL_DATE_TYPES,fieldInfo.getSqlType())) {
                    if(fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_START)){
                        andWhere="<![CDATA[ and "+fieldInfo.getFieldName()+" >= str_to_date(#{query."+fieldInfo.getPropertyName()+"}, '%Y-%m-%d %H:%i:%s')]]>";

                    }
                    else if(fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_END)){
                        andWhere="<![CDATA[ and "+fieldInfo.getFieldName()+" < date_sub(str_to_date(#{query."+fieldInfo.getPropertyName()+"}, '%Y-%m-%d %H:%i:%s',interval-1 day))]]>";

                    }
                }
                bw.newLine();
                bw.write("\t\t<if test=\"query."+fieldInfo.getPropertyName()+"!=null and query."+fieldInfo.getPropertyName()+"!='' \">");
                bw.newLine();
                bw.write("\t\t\t"+andWhere);
                bw.newLine();
                bw.write("\t\t</if>");
                bw.newLine();

            }
            bw.write("\t</sql>");
            bw.newLine();

            //通用查询条件
            bw.write("\t<!--通用查询条件-->");
            bw.newLine();
            bw.write("\t<sql id=\""+QUERY_CONDITION+"\">");
            bw.newLine();
            bw.write("\t\t<where>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\""+BASE_QUERY_CONDITION+"\"/>");
            bw.newLine();
            bw.write("\t\t\t<include refid=\""+BASE_QUERY_CONDITION_EXTENDS+"\"/>");
            bw.newLine();
            bw.write("\t\t</where>");
            bw.newLine();
            bw.write("\t</sql>");
            bw.newLine();

            //查询列表
            bw.write("\t<!--查询列表-->");
            bw.newLine();
            bw.write("\t<select id = \"selectList\" resultMap=\"base_result_map\">");
            bw.newLine();
            bw.write("\t\tSELECT");
            bw.newLine();
            bw.write("\t\t<include refid=\""+BASE_COLUMN_LIST+"\"/>");
            bw.newLine();
            bw.write("\t\tFROM "+tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\""+QUERY_CONDITION+"\"/>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.orderBy!=null\">order by ${query.orderBy}</if>");
            bw.newLine();
            bw.write("\t\t<if test=\"query.simplePage!=null\">limit  #{query.simplePage.start},#{query.simplePage.end}</if>");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();


//            查询数量
            bw.write("\t<!--查询数量-->");
            bw.newLine();
            bw.write("\t\t<select id =\"selectCount\" resultType=\"java.lang.Integer\">");
            bw.newLine();
            bw.write("\t\t\tSELECT count(1) FROM  "+tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<include refid=\""+QUERY_CONDITION+"\"/>");
            bw.newLine();
            bw.write("\t\t</select>");
            bw.newLine();

//            单条插入
            bw.write("\t<!--插入（匹配有值的字段）-->");
            bw.newLine();
            bw.write("\t\t<insert id =\"insert\" parameterType=\""+poClassName+"\">");
            bw.newLine();


            FieldInfo autoIncrementField  =null;
            for(FieldInfo fieldInfo : tableInfo.getFieldInfoList()){
                if(fieldInfo.getAutoIncrement() != null && !fieldInfo.getAutoIncrement()) {
                    autoIncrementField=fieldInfo;
                    break;
                }

            }

            if(autoIncrementField != null){

                bw.write("\t\t\t<selectKey keyProperty=\"bean."+autoIncrementField.getPropertyName()+"\" resultType = \""+autoIncrementField.getJavaType()+"\" order=\"AFTER\">");
                bw.newLine();
                bw.write("\t\t\t\tSELECT LAST_INSERT_ID()");
                bw.newLine();
                bw.write("\t\t\t</selectKey>");
                bw.newLine();
            }
            bw.write("\t\t\t\tINSERT INTO  "+tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();

            for(FieldInfo fieldInfo: tableInfo.getFieldInfoList()){
                bw.write("\t\t\t<if test= \"bean."+fieldInfo.getPropertyName()+"!=null\"> ");
                bw.newLine();
                bw.write("\t\t\t\t"+fieldInfo.getFieldName()+",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }

            bw.write("\t\t</trim>");
            bw.newLine();
            bw.newLine();
            bw.newLine();


            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();

            for(FieldInfo fieldInfo: tableInfo.getFieldInfoList()){
                bw.write("\t\t\t<if test= \"bean."+fieldInfo.getPropertyName()+"!=null\"> ");
                bw.newLine();
                bw.write("\t\t\t\t#{bean."+fieldInfo.getPropertyName()+"},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }

            bw.write("\t\t</trim>");
            bw.newLine();


            bw.write("\t\t</insert>");
            bw.newLine();

//            插入或者更新
            bw.write("\t<!--插入或者更新（匹配有值的字段）-->");
            bw.newLine();
            bw.write("\t\t<insert id =\"insertOrUpdate\" parameterType=\""+poClassName+"\">");
            bw.newLine();


            bw.write("\t\t\t\tINSERT INTO  "+tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();

            for(FieldInfo fieldInfo: tableInfo.getFieldInfoList()){
                bw.write("\t\t\t<if test= \"bean."+fieldInfo.getPropertyName()+"!=null\"> ");
                bw.newLine();
                bw.write("\t\t\t\t"+fieldInfo.getFieldName()+",");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }

            bw.write("\t\t</trim>");
            bw.newLine();

            bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
            bw.newLine();

            for(FieldInfo fieldInfo: tableInfo.getFieldInfoList()){
                bw.write("\t\t\t<if test= \"bean."+fieldInfo.getPropertyName()+"!=null\"> ");
                bw.newLine();
                bw.write("\t\t\t\t#{bean."+fieldInfo.getPropertyName()+"},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }

            bw.write("\t\t</trim>");
            bw.newLine();

            bw.write("\t\t\ton DUPLICATE key update");
            bw.newLine();

            Map<String,String> KeyTempMap = new HashMap();
            for(Map.Entry<String,List<FieldInfo>> entry: KeyIndexMap.entrySet()){
                List<FieldInfo> fieldInfoList = entry.getValue();
                for (FieldInfo item: fieldInfoList){
                    KeyTempMap.put(item.getFieldName(),item.getFieldName());

                }
            }


            bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
            bw.newLine();
            for(FieldInfo fieldInfo: tableInfo.getFieldInfoList()){
                if(KeyTempMap.get(fieldInfo.getFieldName())!=null){
                    continue;
                }
                bw.write("\t\t\t<if test= \"bean."+fieldInfo.getPropertyName()+"!=null\"> ");
                bw.newLine();
                bw.write("\t\t\t\t"+fieldInfo.getFieldName()+" = VALUES("+fieldInfo.getFieldName()+"),");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</trim>");
            bw.newLine();
            bw.write("\t\t</insert>");
            bw.newLine();


//            批量插入
            bw.write("\t<!--批量插入-->");
            bw.newLine();
            bw.write("\t\t<insert id =\"insertBatch\" parameterType=\""+poClassName+"\">" );
            bw.newLine();
            StringBuffer insertFieldBuffer = new StringBuffer();
            for (FieldInfo fieldInfo:tableInfo.getFieldInfoList()){
                if(!fieldInfo.getAutoIncrement()){
                    continue;
                }
                insertFieldBuffer.append(fieldInfo.getFieldName()).append(",");
            }
            String 	insertFieldBufferStr= insertFieldBuffer.substring(0,insertFieldBuffer.lastIndexOf(","));
            bw.write("\t\t\tINSERT INTO "+tableInfo.getTableName()+"(" +insertFieldBufferStr+ ")values");
            bw.newLine();
            bw.write("\t\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">");
            bw.newLine();

            StringBuffer insertPropertyBuffer = new StringBuffer();
            for(FieldInfo fieldInfo:tableInfo.getFieldInfoList()){
                if(!fieldInfo.getAutoIncrement()){
                    continue;
                }
                insertPropertyBuffer.append("#{item."+fieldInfo.getPropertyName()+"}").append(",");
            }
            String insertPropertyBufferStr = insertPropertyBuffer.substring(0, insertPropertyBuffer.lastIndexOf(","));
            bw.write("\t\t\t("+insertPropertyBufferStr+")");

            bw.newLine();
            bw.write("\t\t\t</foreach>");
            bw.newLine();
            bw.write("\t\t</insert>");
            bw.newLine();
            bw.newLine();

//            批量插入或更新
            bw.write("\t<!--批量插入或更新-->");
            bw.newLine();

            bw.write("\t\t<insert id =\"insertOrUpdateBatch\" parameterType=\""+poClassName+"\">" );
            bw.newLine();
            StringBuffer insertOrUpdateFieldBuffer = new StringBuffer();
            StringBuffer insertOrUpdatePropertyBuffer = new StringBuffer();
            StringBuffer insertOrUpdatePropertyNameBuffer = new StringBuffer();
            for (FieldInfo fieldInfo:tableInfo.getFieldInfoList()){
                if(!fieldInfo.getAutoIncrement()){
                    continue;
                }
                insertFieldBuffer.append(fieldInfo.getFieldName()).append(",");
                insertPropertyBuffer.append("#{item."+fieldInfo.getPropertyName()+"}").append(",");
                insertOrUpdatePropertyNameBuffer.append("\t\t\t"+fieldInfo.getFieldName()+" = VALUES("+fieldInfo.getFieldName()+")").append(",\n");

            }
            String 	insertOrUpdateFieldBufferStr= insertFieldBuffer.substring(0,insertFieldBuffer.lastIndexOf(","));
            bw.write("\t\t\tINSERT INTO "+tableInfo.getTableName()+"("+insertFieldBufferStr+")values");
            bw.newLine();
            bw.write("\t\t\t<foreach collection=\"list\" item=\"item\" separator=\",\" >");
            bw.newLine();



            String insertOrUpdatePropertyBufferStr = insertPropertyBuffer.substring(0, insertPropertyBuffer.lastIndexOf(","));
            bw.write("\t\t\t("+insertPropertyBufferStr+")");

            bw.newLine();
            bw.write("\t\t\t</foreach>");
            bw.newLine();

            bw.write("\t\t\ton DUPLICATE key update");
            bw.newLine();


            String insertOrUpdatePropertyNameBufferStr= insertOrUpdatePropertyNameBuffer.substring(0, insertOrUpdatePropertyNameBuffer.lastIndexOf(","));
            bw.write(insertOrUpdatePropertyNameBufferStr);
            bw.newLine();
            bw.newLine();
            bw.write("\t\t</insert>");
            bw.newLine();

            //   根据主键id更新
            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for(Map.Entry<String,List<FieldInfo>> entry: keyIndexMap.entrySet()){
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                Integer index = 0;
                StringBuilder methodNames = new StringBuilder();
                StringBuffer paramsNames=new StringBuffer();
                for(FieldInfo fieldInfo : keyFieldInfoList){
                    index++;
                    methodNames.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    paramsNames.append(fieldInfo.getFieldName()+"=#{"+fieldInfo.getPropertyName()+"}");
                    if(index<keyFieldInfoList.size()){
                        methodNames.append(" And ");
                        paramsNames.append(" and ");
                    }




                }
                bw.newLine();
                bw.write("\t<!--根据"+methodNames+"查询-->");
                bw.newLine();
                bw.write("\t<select id = \"selectBy"+methodNames+"\" resultMap=\"base_result_map\">");
                bw.newLine();

                bw.write("\t\tselect <include refid=\""+BASE_COLUMN_LIST+"\"/> from "+tableInfo.getTableName()+"  where "+paramsNames);
                bw.newLine();
                bw.write("\t</select>");
                bw.newLine();

                bw.write("\t<!--根据"+methodNames+"更新-->");
                bw.newLine();

                bw.write("\t<update id =\"updateBy"+methodNames+"\" parameterType=\""+Constants.PACKAGE_PO+"."+tableInfo.getBeanName()+"\">");
                bw.newLine();
                bw.write("\t\tUPDATE "+tableInfo.getTableName());
                bw.newLine();
                bw.write("\t<set>");
                bw.newLine();
                for(FieldInfo fieldInfo:tableInfo.getFieldInfoList()){
                    if(!fieldInfo.getAutoIncrement()){
                        continue;
                    }
                    bw.write("\t\t<if test= \"bean."+fieldInfo.getPropertyName()+"!=null\"> ");
                    bw.newLine();
                    bw.write("\t\t\t\t"+fieldInfo.getFieldName()+" = #{bean."+fieldInfo.getPropertyName()+"},");
                    bw.newLine();
                    bw.write("\t\t</if>");
                    bw.newLine();
                }
                bw.write("\t</set>");
                bw.newLine();
                bw.write("\t\t\twhere "+paramsNames);
                bw.newLine();
                bw.write("\t</update>");
                bw.newLine();
                bw.newLine();
//
                bw.write("\t<!--根据"+methodNames+"删除-->");
                bw.newLine();

                bw.write("\t<delete id =\"deleteBy"+methodNames+"\">");
                bw.newLine();
                bw.write("\t\tdelete from "+tableInfo.getTableName()+" where "+paramsNames);
                bw.newLine();
                bw.write("\t</delete>");
                bw.newLine();
                bw.newLine();



            }
            bw.newLine();
            bw.write("</mapper>");

            bw.flush();
        }catch (Exception e){
            logger.info("创建MAPPERSXML失败！");
        }finally{
            if(bw!=null)
            {
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(osw!=null){
                try {
                    osw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}
