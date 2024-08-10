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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BuildMapper {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);
    public static void execute(TableInfo tableInfo){
        File folder = new File(Constants.PATH_MAPPERS);
        if(!folder.exists()){
            folder.mkdirs();

        }
        String className= tableInfo.getBeanName()+Constants.SUFFIX_MAPPERS;
        File poFile= new File(folder,className+".java");
        OutputStream out = null;
        OutputStreamWriter osw=null;
        BufferedWriter bw = null;
        try{
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(osw);

            bw.write("package "+Constants.PACKAGE_MAPPERS+";");
            bw.newLine();
            bw.newLine();

            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();






//          构建类注释
            BuildComment.createClassComment(bw,tableInfo.getComment()+"Mapper");
            bw.write("public interface "+className+"<T,P> extends BaseMapper {");
            bw.newLine();


            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for(Map.Entry<String,List<FieldInfo>> entry: keyIndexMap.entrySet()){
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                Integer index = 0;
                StringBuilder methodNames = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                for(FieldInfo fieldInfo : keyFieldInfoList){
                    index++;
                    methodNames.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if(index<keyFieldInfoList.size()){
                        methodNames.append("And");
                    }


                    methodParams.append("@Param"+"(\""+ fieldInfo.getPropertyName()+"\") "+fieldInfo.getJavaType()+" "+ fieldInfo.getPropertyName());
                    if(index<keyFieldInfoList.size()){
                        methodParams.append(", ");
                    }
                }
                BuildComment.createFieldComment(bw,"根据"+methodNames+"查询");
                bw.write("\t T selectBy"+methodNames+"("+methodParams+");");
                bw.newLine();


                BuildComment.createFieldComment(bw,"根据"+methodNames+"更新");
                bw.write("\t Integer updateBy"+methodNames+"(@Param(\"bean\") T t ,"+methodParams+");");
                bw.newLine();

                BuildComment.createFieldComment(bw,"根据"+methodNames+"删除");
                bw.write("\t Integer deleteBy"+methodNames+"("+methodParams+");");
                bw.newLine();
            }


            bw.write("}");
            bw.flush();
        }catch (Exception e){
            logger.info("创建MAPPERS失败！");
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
