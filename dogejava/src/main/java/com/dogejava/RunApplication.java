package com.dogejava;

import com.dogejava.bean.TableInfo;
import com.dogejava.builder.*;

import java.util.List;

public class RunApplication {
    public static void main(String[] args) {
        List<TableInfo> tablesInfoList = BuildTable.getTables();
        BuildBase.execute();
        for(TableInfo tableInfo:tablesInfoList){
            BuildPo.execute(tableInfo);

            BuildQuery.execute(tableInfo);
            BuildMapper.execute(tableInfo);
            BuildMapperXml.execute(tableInfo);
            BuildService.execute(tableInfo);
            BuildServiceImpl.execute(tableInfo);
            BuildController.execute(tableInfo);
        }
    }
}
