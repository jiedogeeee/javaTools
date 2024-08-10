package com.dogejava.builder;

import com.dogejava.bean.Constants;
import com.dogejava.bean.FieldInfo;
import com.dogejava.bean.TableInfo;
import com.dogejava.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuildController {
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_CONTROLLER);
        if (!folder.exists()) {
            folder.mkdirs();

        }
        String className = tableInfo.getBeanName() + "Controller";
        File poFile = new File(folder, className + ".java");
        OutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_CONTROLLER + ";");
            bw.newLine();
            bw.newLine();


            String serviceName = tableInfo.getBeanName() + "Service";
            String serviceBeanName = StringUtils.lowerCaseFirstLetter(serviceName);

            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + serviceName + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import "+Constants.PACKAGE_VO+".ResponseVO;");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();

            bw.write("import javax.annotation.Resource;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestBody;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RestController;");
            bw.newLine();
            bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Controller");
            bw.write("@RestController");
            bw.newLine();
            bw.write("@RequestMapping(\"/"+StringUtils.lowerCaseFirstLetter(tableInfo.getBeanName())+"\")");
            bw.newLine();
            bw.write("public class " + className + " extends ABaseController {");
            bw.newLine();
            bw.newLine();

            bw.write("\t@Resource");
            bw.newLine();

            bw.write("\tprivate " + serviceName +" "+ serviceBeanName+ ";");
            bw.newLine();

            BuildComment.createFieldComment(bw, "新增");
            bw.newLine();

            bw.write("\t@RequestMapping(\"loadDataList\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO loadDataList("+tableInfo.getBeanParamName()+" query) {");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO("+serviceBeanName+".findListByPage(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "新增");
            bw.newLine();
            bw.newLine();
            bw.write("\t@RequestMapping(\"add\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".add(bean));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();


            BuildComment.createFieldComment(bw, "批量新增");
            bw.newLine();
            bw.newLine();
            bw.write("\t@RequestMapping(\"addBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addBatch(@RequestBody List<" + tableInfo.getBeanName() + "> ListBean) {");
            bw.newLine();

            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".addBatch(ListBean));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();


            BuildComment.createFieldComment(bw, "批量新增或者更新");
            bw.newLine();
            bw.newLine();
            bw.write("\t@RequestMapping(\"addBatchOrUpdate\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addBatchOrUpdate(@RequestBody List<" + tableInfo.getBeanName() + "> ListBean) {");
            bw.newLine();

            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName+ ".addBatchOrUpdate(ListBean));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();


            for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                Integer index = 0;
                StringBuilder methodNames = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                StringBuilder paramsBuilder = new StringBuilder();
                for (FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodNames.append(StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if (index < keyFieldInfoList.size()) {
                        methodNames.append("And");
                    }


                    methodParams.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                    paramsBuilder.append(fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParams.append(", ");
                        paramsBuilder.append(",");

                    }
                }
                BuildComment.createFieldComment(bw, "根据" + methodNames + "查询");

                bw.newLine();
                bw.write("\t@RequestMapping(\"getBy" + methodNames +"\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO getBy" + methodNames + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".getBy" + methodNames + "(" + paramsBuilder + "));");
                bw.newLine();

                bw.write("\t}");
                bw.newLine();
                bw.newLine();


                BuildComment.createFieldComment(bw, "根据" + methodNames + "更新");
                bw.newLine();
                bw.write("\t@RequestMapping(\"updateBy" + methodNames +"\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO updateBy" + methodNames + "(" + tableInfo.getBeanName() + " bean ," + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".updateBy" + methodNames + "(bean," + paramsBuilder + "));");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodNames + "删除");
                bw.newLine();
                bw.write("\t@RequestMapping(\"deleteBy" + methodNames +"\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO deleteBy" + methodNames + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".deleteBy" + methodNames + "(" + paramsBuilder + "));");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
            }


            bw.write("}");

            bw.flush();
        } catch (Exception e) {
            logger.info("创建service Impl失败！");
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}
