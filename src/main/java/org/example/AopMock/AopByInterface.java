package org.example.AopMock;

import com.alibaba.fastjson.JSONObject;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.AopMock.common.ThriftPointCutter;
import org.example.AopMock.common.unit.ThriftUnit;
import org.example.AopMock.common.utils.IfaceUtils;
import org.example.AopMock.recordRes.utils.ReqRecordLogger;

import java.lang.reflect.Method;
import java.util.Arrays;

/*
*@author  zhangyufeng
*@data 2024/2/23 15:09
*/


@Aspect
public class AopByInterface {
    private IfaceUtils ifaceUtils = new IfaceUtils();
    private ThriftPointCutter thriftPointCutter = new ThriftPointCutter();

    private static boolean openRecord = false;
    private static boolean openPlay = false;

    static {
        if (StringUtils.isNotBlank(System.getenv("xx"))){
            openPlay = true;
        } else openRecord= true;
    }

    @Pointcut("execution(* org.apache.thrift.TServiceClient.*(..))")
    public void receiveBasePonitCut() {

    }

    /**
     * 流量回放
     */
    @Around("receiveBasePonitCut()")
    public Object aroundPointCut(ProceedingJoinPoint joinPoint) throws Throwable {
        if (openPlay){
            try {
                Object[] methodArg = null;
                Object[] args = joinPoint.getArgs();
                Object arg0 = null;
                String signature = null;
                if (args != null && args.length>0){
                    arg0 = args[0];
                    Method method = ((MethodInvocation)arg0).getMethod();
                    if (!ifaceUtils.isNoNeedAop(method.getDeclaringClass())){
                        String interfaceName = method.getDeclaringClass().getName().toString();
                        String methodName = method.getName();
                        signature = interfaceName + "." + methodName;
                        methodArg = ((MethodInvocation)arg0).getArguments();
                    }
                }

                if (methodArg!=null){
                    //根据方法签名和请求参数查找匹配的结果
                    Object res = thriftPointCutter.findResult(signature,methodArg);
                    if (null != res ){
                        Class<?> returnType = ((MethodInvocation)arg0).getMethod().getReturnType();
                        return JSONObject.parseObject(res.toString(),returnType);
                    }
                }else {
                    throw new Exception("mock失败");
                }
            }catch (Exception e){
                throw e;
            }
        }
        return joinPoint.proceed();
    }


    @AfterReturning(value = "receiveBasePonitCut()",returning = "resultDate")
    public void afterPointCut(JoinPoint joinPoint, Object resultDate){
        if (openRecord) {
            Object[] methodArg = null;
            Object[] args = joinPoint.getArgs();
            Object arg0 = null;
            String signature = null;
            if (args != null && args.length > 0) {
                arg0 = args[0];
                Method method = ((MethodInvocation) arg0).getMethod();
                if (!ifaceUtils.isNoNeedAop(method.getDeclaringClass())) {
                    String interfaceName = method.getDeclaringClass().getName().toString();
                    String methodName = method.getName();
                    signature = interfaceName + "." + methodName;
                    methodArg = ((MethodInvocation) arg0).getArguments();
                }
            }
            if (methodArg != null) {
                //构建日志对象
                ThriftUnit unit = ThriftUnit.builder()
                        .signature(signature)
                        .arg(Arrays.toString(methodArg))
                        .result(resultDate)
                        .build();
                //上报日志
                ReqRecordLogger.logUnit(unit);
            }
        }
    }
}
