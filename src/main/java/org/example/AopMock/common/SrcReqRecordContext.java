package org.example.AopMock.common;

import org.example.AopMock.common.unit.ThriftUnit;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SrcReqRecordContext {

    private ReqRecordLoad reqRecordLoad = new ReqRecordLoad();

    //根据signature匹配录制数据
    public Collection<ThriftUnit> getThriftUnitBySignature(String signature){
        List<ThriftUnit> allList =reqRecordLoad.getFinalThriftUnits();
        if (!allList.isEmpty()){
            return allList.stream().filter(u->signature.equals(u.getSignature())).collect(Collectors.toList());
        }
        return null;
    }
}
