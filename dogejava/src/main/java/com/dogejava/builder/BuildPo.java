package com.dogejava.builder;

import com.dogejava.bean.Constants;
import com.dogejava.bean.FieldInfo;
import com.dogejava.bean.TableInfo;
import com.dogejava.utils.DateUtils;
import com.dogejava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class BuildPo{
    private static final Logger logger = LoggerFactory.getLogger(BuildPo.class);
    public static void execute (TableInfo tableInfo){
        File folder = new File(Constants.PATH_PO);
        if(!folder.exists()){
            folder.mkdirs();

        }
        File poFile= new File(folder,tableInfo.getBeanName()+".java");
        OutputStream out = null;
        OutputStreamWriter osw=null;
        BufferedWriter bw = null;
        try{
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(osw);

            bw.write("package "+Constants.PACKAGE_PO+";");
            bw.newLine();
            bw.newLine();

            if(tableInfo.getHaveDate()||tableInfo.getHaveDateTime()){


                bw.write(Constants.BEAN_DATE_FORMAT_CLASS+";");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_UNFORMAT_CLASS+";");
                bw.newLine();

                bw.write("import "+Constants.PACKAGE_ENUMS+".DateTimePatternEnum;");
                bw.newLine();
                bw.write("import "+Constants.PACKAGE_UTILS+".DateUtils;");
                bw.newLine();
                bw.write("import java.util.Date;");
                bw.newLine();
            }
            bw.write("import java.io.Serializable;");
            bw.newLine();
            bw.newLine();
//            为忽略属性导包
            Boolean haveIgnoreBean = false;
            for(FieldInfo field:tableInfo.getFieldInfoList()){
                if(ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FIELD.split(","),field.getPropertyName())){
                    haveIgnoreBean=true;
                    break;

                }

            }
            if(haveIgnoreBean){
                bw.write(Constants.IGNORE_BEAN_TOJSON_CLASS+";");
                bw.newLine();
            }
            if(tableInfo.getHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;");

            }
            bw.newLine();
            bw.newLine();


//          构建类注释
            BuildComment.createClassComment(bw,tableInfo.getComment());
            bw.write("public class "+tableInfo.getBeanName()+" implements Serializable {");
            bw.newLine();


            for(FieldInfo field:tableInfo.getFieldInfoList()){
                BuildComment.createFieldComment(bw,field.getComment());



                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,field.getSqlType())){
                    bw.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                    bw.write("\t"+String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();

                }
                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,field.getSqlType())){
                    bw.write("\t"+String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                    bw.write("\t"+String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();

                }


                if(ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FIELD.split(","),field.getPropertyName())){
                    bw.write("\t"+Constants.IGNORE_BEAN_TOJSON_EXPRESSION);
                    bw.newLine();
                }
                bw.write("\tprivate "+field.getJavaType()+" "+field.getPropertyName()+";");
                bw.newLine();
                bw.newLine();
            }

            for(FieldInfo field:tableInfo.getFieldInfoList()){
                String fieldTemp = StringUtils.upperCaseFirstLetter(field.getPropertyName());
                bw.write("\tpublic void set"+fieldTemp+"("+field.getJavaType()+" "+ field.getPropertyName()+")"+ " {");
                bw.newLine();
                bw.write("\t\tthis."+field.getPropertyName()+" = "+field.getPropertyName()+";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                bw.write("\tpublic "+field.getJavaType()+" get"+fieldTemp+"()"+ " {");
                bw.newLine();
                bw.write("\t\treturn this."+field.getPropertyName()+";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

            }

            /**
             * 重写toString方法
             */
            StringBuffer toString = new StringBuffer();
            Integer index=0;
            for(FieldInfo field:tableInfo.getFieldInfoList()){
                index++;
                String properName = field.getPropertyName();
                if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,field.getSqlType())){
                    properName = "DateUtils.format("+properName+", DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())";

                } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES,field.getSqlType())) {
                    properName = "DateUtils.format("+properName+", DateTimePatternEnum.YYYY_MM_DD.getPattern())";
                }
                toString.append(field.getComment()+":"+"\""+" + ("+field.getPropertyName()+" == null ? \"空\" : "+properName+")");
                if(index<tableInfo.getFieldInfoList().size()){
                    toString.append(" + ").append("\",");
                }

            }
            String toStringStr= toString.toString();
            toStringStr="\""+toStringStr;
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write("\t\treturn "+ toStringStr+";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            bw.write("}");
            bw.flush();
        }catch (Exception e){
            logger.info("创建PO失败！");
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
