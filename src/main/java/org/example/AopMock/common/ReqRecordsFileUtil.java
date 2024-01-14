package org.example.AopMock.common;

import com.google.common.collect.Maps;
import org.example.AopMock.common.unit.RecordInterfaceType;

import java.util.Map;

public class ReqRecordsFileUtil {

    public static Map<RecordInterfaceType, String> typeLogFileNameMap = Maps.newHashMap();

    //词表文件的绝对路径
    private static final String RECORD_FILE_DIR_PATH = "/opt/logs/req-record";
    static {
        typeLogFileNameMap.put(RecordInterfaceType.thrift,"thrift");
    }

    //生成本地文件名
    public static String getRecordFileDirPath(String data){
        return String.format("%s%s",RECORD_FILE_DIR_PATH,data);
    }
}
