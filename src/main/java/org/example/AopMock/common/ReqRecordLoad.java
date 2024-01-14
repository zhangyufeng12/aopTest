package org.example.AopMock.common;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.example.AopMock.common.unit.RecordInterfaceType;
import org.example.AopMock.common.unit.ReqRecordUnit;
import org.example.AopMock.common.unit.ThriftUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.example.AopMock.common.ReqRecordsFileUtil.typeLogFileNameMap;

public class ReqRecordLoad {
    private static List<ThriftUnit> finalThriftUnits = new ArrayList<>();
    public List<ThriftUnit> getFinalThriftUnits(){
        return finalThriftUnits;
    }

    //加载录制数据
    public void loadFeatureTopology(String data){
        //日志转换
        List<ThriftUnit> thriftUnits = readUnitFromLocalFile(data, RecordInterfaceType.thrift,ThriftUnit.class);
        //对象存储
        for (ThriftUnit units : thriftUnits){
            finalThriftUnits.clear();
            if (units.getTraceId()!= null){
                finalThriftUnits.add(units);
            }
        }
        //finalTraces.clear();
    }

    //将日志文件加载至list
    private <T extends ReqRecordUnit> List<T> readUnitFromLocalFile(String data, RecordInterfaceType recordInterfaceType, Class<T> resUnitClass) {
        String dirPath = ReqRecordsFileUtil.getRecordFileDirPath(data);
        File dirFile = new File(dirPath);
        List<T> units = Lists.newArrayList();
        for (File file : dirFile.listFiles()){
            if (file.getName().contains(typeLogFileNameMap.get(recordInterfaceType))){
                continue;
            }
            try (FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader)){
                String line;
                while ((line=bufferedReader.readLine())!=null){
                    //处理每一行数据
                    if (StringUtils.isNotBlank(line)&&line.length()>"HH:mm:ss.SSS".length()) {
                        String content = line.substring("HH:mm:ss.SSS".length());
                        T unit = null;
                        try {
                            unit = JSON.parseObject(content, resUnitClass);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (unit !=null && StringUtils.isNotBlank(unit.getTraceId())){
                            units.add(unit);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            return units;
    }


}
