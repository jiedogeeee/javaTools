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
import java.util.List;
import java.util.Map;

public class BuildService {
    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    public static void execute (TableInfo tableInfo){
        File folder = new File(Constants.PATH_SERVICE);
        if(!folder.exists()){
            folder.mkdirs();

        }
        String className = tableInfo.getBeanName()+"Service";
        File poFile= new File(folder,className+".java");
        OutputStream out = null;
        OutputStreamWriter osw=null;
        BufferedWriter bw = null;
        try{
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(osw);

            bw.write("package "+Constants.PACKAGE_SERVICE+";");
            bw.newLine();
            bw.newLine();

            bw.write("import com.doge.entity.vo.PaginationResultVO;");
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
            bw.write("import "+Constants.PACKAGE_PO+"."+tableInfo.getBeanName()+";");
            bw.newLine();
            bw.write("import "+Constants.PACKAGE_QUERY+"."+tableInfo.getBeanParamName()+";");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();

            BuildComment.createClassComment(bw,tableInfo.getComment()+"Service");
            bw.write("public interface "+className+" {");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw,"根据条件查询列表");
            bw.newLine();
            bw.newLine();
            bw.write("\t\tList<"+tableInfo.getBeanName()+"> findListByParam( "+tableInfo.getBeanParamName()+" query); ");
            bw.newLine();
            bw.newLine();


            BuildComment.createFieldComment(bw,"根据条件查询数量");
            bw.newLine();
            bw.newLine();
            bw.write("\t\tInteger findCountByParam( "+tableInfo.getBeanParamName()+" query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw,"分页查询");
            bw.newLine();
            bw.write("\t\tPaginationResultVO<"+tableInfo.getBeanName()+"> findListByPage("+tableInfo.getBeanParamName()+" query);");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw,"新增");
            bw.newLine();
            bw.write("\t\tInteger add("+tableInfo.getBeanName()+" bean);");
            bw.newLine();
            bw.newLine();


            BuildComment.createFieldComment(bw,"批量新增");
            bw.newLine();
            bw.write("\t\tInteger addBatch(List<"+tableInfo.getBeanName()+"> ListBean);");
            bw.newLine();
            bw.newLine();


            BuildComment.createFieldComment(bw,"批量新增或者更新");
            bw.newLine();
            bw.write("\t\tInteger addBatchOrUpdate(List<"+tableInfo.getBeanName()+"> ListBean);");
            bw.newLine();
            bw.newLine();



            for(Map.Entry<String,List<FieldInfo>> entry: tableInfo.getKeyIndexMap().entrySet()){
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


                    methodParams.append(fieldInfo.getJavaType()+" "+ fieldInfo.getPropertyName());
                    if(index<keyFieldInfoList.size()){
                        methodParams.append(", ");
                    }
                }
                BuildComment.createFieldComment(bw,"根据"+methodNames+"查询");
                bw.write("\t"+tableInfo.getBeanName()+" getBy"+methodNames+"("+methodParams+");");
                bw.newLine();


                BuildComment.createFieldComment(bw,"根据"+methodNames+"更新");
                bw.write("\t Integer updateBy"+methodNames+"("+tableInfo.getBeanName()+" bean ,"+methodParams+");");
                bw.newLine();

                BuildComment.createFieldComment(bw,"根据"+methodNames+"删除");
                bw.write("\t Integer deleteBy"+methodNames+"("+methodParams+");");
                bw.newLine();
            }





            bw.write("}");

            bw.flush();
        }catch (Exception e){
            logger.info("创建service失败！");
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
