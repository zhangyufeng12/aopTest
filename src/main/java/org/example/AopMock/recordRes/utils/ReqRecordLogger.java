package org.example.AopMock.recordRes.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.example.AopMock.common.unit.RecordInterfaceType;
import org.example.AopMock.common.unit.ReqRecordUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ReqRecordLogger {
    private static Map<RecordInterfaceType, Logger> typeLoggerMap = Maps.newHashMap();

    static {
        typeLoggerMap.put(RecordInterfaceType.thrift, LoggerFactory.getLogger("ReqRecord_thirftLogger"));
    }

    private static ExecutorService executorService = new ThreadPoolExecutor(
            10,10,10L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(102400),
            new ThreadFactoryBuilder().setNameFormat("ReqRecordLogger-pool-%d").build(),
            (r, executor) -> {
                // do
            });

    //打印请求unit结果
    public static void logUnit(ReqRecordUnit unit){
        String traceId = "获取traceId";
        //只记录有Trace信息的数据
        if (StringUtils.isBlank(traceId) || !typeLoggerMap.containsKey(unit.type())){
            return;
        }
        unit.setTraceId(traceId);
        //将对象上传至info级别日志  apender为ReqRecord_thirftLogger
        executorService.submit(()-> typeLoggerMap.get(unit.type()).info(JSON.toJSONString(unit)));
    }
}
