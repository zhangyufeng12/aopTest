package org.example.AopMock.common;

import org.apache.thrift.TBase;
import org.example.AopMock.common.unit.ThriftUnit;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ThriftPointCutter {
    private static Set<String> ifaceSet;
    private static String mockIface = System.getenv("mockIface");

    static {
        if (null != mockIface) {
            ifaceSet = Arrays.stream(mockIface.split(",")).collect(Collectors.toSet());
        } else {
            ifaceSet = new HashSet<>();
        }
    }

    private SrcReqRecordContext srcReqRecordContext;

    public ThriftPointCutter(SrcReqRecordContext srcReqRecordContext) {
        this.srcReqRecordContext = srcReqRecordContext;
    }

    //获取录制thrift请求结果
    public TBase findResult(String signature, TBase methodArg) {
        if (null == signature) {
            return null;
        }
        Collection<ThriftUnit> thriftUnits = srcReqRecordContext.getThriftUnitBySignature(signature);
        if (thriftUnits.isEmpty()) {
            return null;
        }
        for (ThriftUnit thriftUnit : thriftUnits) {
            if (methodArg.equals(thriftUnit.getArg())) {
                return thriftUnit.getResult();
            }
        }
        return null;
    }

    public boolean isNoNeedAop(Class<?> thriftIface) {
        for (String s : ifaceSet) {
            if (thriftIface.getName().startsWith(s)) {
                return false;
            }
        }
        return true;
    }

}
