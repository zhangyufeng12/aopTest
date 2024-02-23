package org.example.AopMock.common.utils;
/*
*@author  zhangyufeng
*@data 2024/2/23 15:26
*/

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class IfaceUtils {
    private static Set<String> ifaceSet;
    private static String mockIface = System.getenv("mockIface");

    static {
        if (null != mockIface) {
            ifaceSet = Arrays.stream(mockIface.split(",")).collect(Collectors.toSet());
        } else {
            ifaceSet = new HashSet<>();
        }
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
