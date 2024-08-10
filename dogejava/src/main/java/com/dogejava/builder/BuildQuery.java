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
import java.util.ArrayList;
import java.util.List;

public class BuildQuery {
    private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);
    public static void execute (TableInfo tableInfo){
        File folder = new File(Constants.PATH_QUERY);
        if(!folder.exists()){
            folder.mkdirs();

        }
        String className = tableInfo.getBeanName()+Constants.SUFFIX_BEAN_QUERY;
        File poFile= new File(folder,className+".java");
        OutputStream out = null;
        OutputStreamWriter osw=null;
        BufferedWriter bw = null;
        try{
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(osw);

            bw.write("package "+Constants.PACKAGE_QUERY+";");
            bw.newLine();
            bw.newLine();

            if(tableInfo.getHaveDate()||tableInfo.getHaveDateTime()){



                bw.write("import java.util.Date;");
                bw.newLine();
            }


            if(tableInfo.getHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;");

            }
            bw.newLine();
            bw.newLine();


//          构建类注释
            BuildComment.createClassComment(bw,tableInfo.getComment()+"查询对象");
            bw.write("public class "+className+" extends BaseQuery{");
            bw.newLine();


            for(FieldInfo field:tableInfo.getFieldInfoList()){
                BuildComment.createFieldComment(bw,field.getComment());
                bw.write("\tprivate "+field.getJavaType()+" "+field.getPropertyName()+";");
                bw.newLine();
                bw.newLine();

                if(ArrayUtils.contains(Constants.SQL_STRING_TYPES,field.getSqlType())){
                    String propertyName =field.getPropertyName()+Constants.SUFFIX_BEAN_QUERY_FUZZY;
                    bw.write("\tprivate "+field.getJavaType()+" "+propertyName+";");
                    bw.newLine();
                    bw.newLine();

                }

                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES,field.getSqlType())||ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,field.getSqlType())){
                    bw.write("\tprivate String"+" "+field.getPropertyName()+Constants.SUFFIX_BEAN_QUERY_TIME_START+";");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tprivate String"+" "+field.getPropertyName()+Constants.SUFFIX_BEAN_QUERY_TIME_END+";");
                    bw.newLine();
                    bw.newLine();

                }

            }

                buildGetSet(tableInfo.getFieldInfoList(),bw);
                buildGetSet(tableInfo.getFieldExtends(),bw);
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
    private static void buildGetSet(List<FieldInfo> fieldInfoList,BufferedWriter bw) throws Exception{
        for(FieldInfo field:fieldInfoList){
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
    }
}
