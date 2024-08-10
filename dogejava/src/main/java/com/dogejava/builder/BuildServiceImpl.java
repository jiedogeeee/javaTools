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

public class BuildServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_SERVICE_IMPL);
        if (!folder.exists()) {
            folder.mkdirs();

        }
        String className = tableInfo.getBeanName() + "ServiceImpl";
        String interfaceName = tableInfo.getBeanName() + "Service";
        File poFile = new File(folder, className + ".java");
        OutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            osw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_SERVICE_IMPL + ";");
            bw.newLine();
            bw.newLine();

//            bw.write("import com.doge.entity.vo.PaginationResultVO;");
//            bw.newLine();
//            if(tableInfo.getHaveDate()||tableInfo.getHaveDateTime()){
//                bw.write(Constants.BEAN_DATE_FORMAT_CLASS+";");
//                bw.newLine();
//                bw.write(Constants.BEAN_DATE_UNFORMAT_CLASS+";");
//                bw.newLine();
//                bw.write("import "+Constants.PACKAGE_ENUMS+".DateTimePatternEnum;");
//                bw.newLine();
//                bw.write("import "+Constants.PACKAGE_UTILS+".DateUtils;");
//                bw.newLine();
//                bw.write("import java.util.Date;");
//                bw.newLine();
//            }
            String mapperName = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
            String mapperBeanName = StringUtils.lowerCaseFirstLetter(mapperName);
            bw.write("import " + Constants.PACKAGE_QUERY + ".SimplePage;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_ENUMS + ".PageSize;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PaginationResultVO;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_MAPPERS + "." + mapperName + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + interfaceName + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanParamName() + ";");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();

            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            bw.write("import javax.annotation.Resource;");
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Service");
            bw.write("@Service(\"" +StringUtils.lowerCaseFirstLetter( tableInfo.getBeanName()) + "Service\")");
            bw.newLine();

            bw.write("public class " + className + " implements " + interfaceName + " {");
            bw.newLine();
            bw.newLine();

            bw.write("\t@Resource");
            bw.newLine();

            bw.write("\tprivate " + mapperName + "<" + tableInfo.getBeanName() + "," + tableInfo.getBeanParamName() + ">" + StringUtils.lowerCaseFirstLetter(mapperName) + ";");
            bw.newLine();

            BuildComment.createFieldComment(bw, "根据条件查询列表");
            bw.newLine();
            bw.newLine();
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic List<" + tableInfo.getBeanName() + "> findListByParam( " + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".selectList(query);");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.newLine();


            BuildComment.createFieldComment(bw, "根据条件查询数量");
            bw.newLine();
            bw.newLine();
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer findCountByParam( " + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".selectCount(query);");
            bw.newLine();
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "分页查询");
            bw.newLine();
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic PaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + tableInfo.getBeanParamName() + " query) {");
            bw.newLine();
            bw.write("\t\tInteger count = this.findCountByParam(query);");
            bw.newLine();
            bw.write("\t\tInteger pageSize = query.getPageSize() == null? PageSize.SIZE15.getSize():query.getPageSize(); ");

            bw.newLine();
            bw.write("\t\tSimplePage page = new SimplePage(query.getPageNo(),count,pageSize);");
            bw.newLine();
            bw.write("\t\tquery.setSimplePage(page);");
            bw.newLine();
            bw.write("\t\tList<" + tableInfo.getBeanName() + "> list =  this.findListByParam(query);");
            bw.newLine();
            bw.write("\t\tPaginationResultVO<" + tableInfo.getBeanName() + "> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),list);");
            bw.newLine();
            bw.write("\t\treturn result;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "新增");
            bw.newLine();
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer add(" + tableInfo.getBeanName() + " bean) {");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insert(bean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();


            BuildComment.createFieldComment(bw, "批量新增");
            bw.newLine();
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer addBatch(List<" + tableInfo.getBeanName() + "> ListBean) {");
            bw.newLine();
            bw.write("\tif(ListBean ==null || ListBean.isEmpty() ){");
            bw.newLine();
            bw.write("\t\treturn 0;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insertBatch(ListBean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();


            BuildComment.createFieldComment(bw, "批量新增或者更新");
            bw.newLine();
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer addBatchOrUpdate(List<" + tableInfo.getBeanName() + "> ListBean) {");
            bw.newLine();
            bw.write("\tif(ListBean ==null || ListBean.isEmpty() ) {");
            bw.newLine();
            bw.write("\t\treturn 0;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.write("\t\treturn this." + mapperBeanName + ".insertOrUpdateBatch(ListBean);");
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

                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic " + tableInfo.getBeanName() + " getBy" + methodNames + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".selectBy" + methodNames + "(" + paramsBuilder + ");");
                bw.newLine();

                bw.write("\t}");
                bw.newLine();
                bw.newLine();


                BuildComment.createFieldComment(bw, "根据" + methodNames + "更新");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic Integer updateBy" + methodNames + "(" + tableInfo.getBeanName() + " bean ," + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".updateBy" + methodNames + "(bean," + paramsBuilder + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodNames + "删除");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic Integer deleteBy" + methodNames + "(" + methodParams + ") {");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".deleteBy" + methodNames + "(" + paramsBuilder + ");");
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
