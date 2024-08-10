package com.dogejava.builder;

import com.alibaba.fastjson.support.odps.CodecCheck;
import com.dogejava.bean.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BuildBase {

    private static final Logger logger = LoggerFactory.getLogger(BuildBase.class);
    public static void execute(){
        List<String> headInfoList = new ArrayList();

//        生成Date枚举
        headInfoList.add("package "+Constants.PACKAGE_ENUMS);
        build( headInfoList,"DateTimePatternEnum", Constants.PATH_ENUMS);

        headInfoList.clear();
        headInfoList.add("package "+Constants.PACKAGE_UTILS);
        build( headInfoList,"DateUtils", Constants.PATH_UTILS);

//        生成mapper
        headInfoList.clear();
        headInfoList.add("package "+Constants.PACKAGE_MAPPERS);
        build( headInfoList,"BaseMapper", Constants.PATH_MAPPERS);

//        生成PageSize枚举

        headInfoList.clear();
        headInfoList.add("package "+Constants.PACKAGE_QUERY);
        headInfoList.add("import  "+Constants.PACKAGE_ENUMS+".PageSize");

        build( headInfoList,"SimplePage", Constants.PATH_QUERY);
//        生成PageSize枚举

        headInfoList.clear();
        headInfoList.add("package "+Constants.PACKAGE_ENUMS);
        build( headInfoList,"PageSize", Constants.PATH_ENUMS);
//        生成PageSize枚举

        headInfoList.clear();
        headInfoList.add("package "+Constants.PACKAGE_QUERY);
        build( headInfoList,"BaseQuery", Constants.PATH_QUERY);

        //        生成PaginationResultVO

        headInfoList.clear();
        headInfoList.add("package "+Constants.PACKAGE_VO);
        build( headInfoList,"PaginationResultVO", Constants.PATH_VO);

        //        生ResponseVO枚举

        headInfoList.clear();
        headInfoList.add("package "+Constants.PACKAGE_VO);
        build( headInfoList,"ResponseVO", Constants.PATH_VO);


        //        生成exception

        headInfoList.clear();

        headInfoList.add("package "+Constants.PACKAGE_EXCEPTION);
        headInfoList.add("import "+Constants.PACKAGE_ENUMS+". ResponseCodeEnum;");
        build( headInfoList,"BusinessException", Constants.PATH_EXCEPTION);


        //        生成ResponseCodeEnum

        headInfoList.clear();
        headInfoList.add("package "+Constants.PACKAGE_ENUMS);
        build( headInfoList,"ResponseCodeEnum", Constants.PATH_ENUMS);

        //        ABaseController.txt

        headInfoList.clear();
        headInfoList.add("package "+Constants.PACKAGE_CONTROLLER);
        headInfoList.add("import  "+Constants.PACKAGE_ENUMS+".ResponseCodeEnum");
        headInfoList.add("import  "+Constants.PACKAGE_VO+".ResponseVO");
        build( headInfoList,"ABaseController", Constants.PATH_CONTROLLER);

        //        AGlobalExceptionHandlerController

          /*      import com.doge.enums.ResponseCodeEnum;
        import com.doge.entity.vo.ResponseVO;
        import com.doge.exception.BusinessException;*/


        headInfoList.clear();
        headInfoList.add("package "+Constants.PACKAGE_CONTROLLER);
        headInfoList.add("import  "+Constants.PACKAGE_ENUMS+".ResponseCodeEnum");
        headInfoList.add("import  "+Constants.PACKAGE_VO+".ResponseVO");
        headInfoList.add("import  "+Constants.PACKAGE_EXCEPTION+".BusinessException");
        build( headInfoList,"AGlobalExceptionHandlerController", Constants.PATH_CONTROLLER);


    }

    private static  void build(List<String> headerInfoList,String fileName, String outPutPath){
        File folder = new File(outPutPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        File javaFile =new File(outPutPath,fileName+".java");
        OutputStream os = null;
        OutputStreamWriter otw = null;
        BufferedWriter bw = null;

        InputStream is = null;
        InputStreamReader isr =null;
        BufferedReader br = null;

        try{
            os =new FileOutputStream(javaFile);
            otw=new OutputStreamWriter(os,"utf8");
            bw = new BufferedWriter(otw);

            String templatePath = BuildBase.class.getClassLoader().getResource("template/"+fileName+".txt").getPath();
            is = new FileInputStream(templatePath);
            isr = new InputStreamReader(is,"utf8");
            br = new BufferedReader(isr);

            for(String head:headerInfoList){
                bw.write(head+";");
                bw.newLine();
                if(head.contains("package")){
                    bw.newLine();
                }

            }
            String lineInfo = null;
            while((lineInfo= br.readLine())!=null){
                bw.write(lineInfo);
                bw.newLine();
            }
            bw.flush();

        }
        catch (Exception e){
           logger.error("生成基础类：{},失败：",fileName,e);

        }finally{

            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(isr!=null){
                try {
                    isr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(otw!=null){
                try {
                    otw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
