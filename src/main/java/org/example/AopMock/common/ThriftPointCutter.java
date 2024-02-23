package org.example.AopMock.common;

import org.apache.thrift.TBase;
import org.example.AopMock.common.unit.ThriftUnit;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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

    private SrcReqRecordContext srcReqRecordContext = new SrcReqRecordContext();


//    public ThriftPointCutter(SrcReqRecordContext srcReqRecordContext) {
//        this.srcReqRecordContext = srcReqRecordContext;
//    }

    //获取录制thrift请求结果
    public Object findResult(String signature, Object[] methodArg) {
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

        //根据signature + method 计算，随机获取返回对象
        List<ThriftUnit> thriftUnitList = new ArrayList<>(thriftUnits);
        int index = getUnitIndex(thriftUnitList,Arrays.toString(methodArg));
        return thriftUnitList.get(index).getResult();
    }




    private int getUnitIndex(List<ThriftUnit> thriftUnitList, String s) {
        int index = 0;

        //转MD5
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte[] digest = md.digest();
            BigInteger bi = new BigInteger(1,digest);

            //转10进制
            BigInteger dec = new BigInteger(bi.toString(10));

            //除以集合大小取余数
            index = dec.mod(BigInteger.valueOf(thriftUnitList.size())).intValue();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return index;
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
