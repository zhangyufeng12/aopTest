package org.example.AopMock.recordRes;

import org.apache.thrift.TBase;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.AopMock.common.ThriftPointCutter;
import org.example.AopMock.common.unit.ThriftUnit;
import org.example.AopMock.common.utils.ApplicationContextUtil;
import org.example.AopMock.recordRes.utils.ReqRecordLogger;

/**
 * 流量录制
 */
@Aspect
public class ThriftAop {
    private static final ThreadLocal<TBase> THREAD_LOCAL = new ThreadLocal<>();
    private static ReqRecordLogger reqRecordLogger = new ReqRecordLogger();

    //发送请求处做切面
    @Pointcut("execution(void org.apache.thrift.TServiceClient.sendBase(java.lang.String, org.apache.thrift.TBase)")
    public void sendBasePointCut() {

    }

    //返回结果做切面
    @Pointcut("execution(void org.apache.thrift.TServiceClient.receiveBase(org.apache.thrift.TBase, java.lang.String )")
    public void receiveBasePointCut() {

    }

    @After("sendBasePointCut()")
    public void afterSendBasePointCut(JoinPoint joinPoint) throws Throwable{
        try {
            if (!thriftPointCutter().isNoNeedAop(joinPoint.getTarget().getClass())){
                //将请求参数设置到ThreadLocal中
                Object[] args = joinPoint.getArgs();
                THREAD_LOCAL.set((TBase) args[1]);
            }
        }catch (Exception e){
            throw e;
        }
    }
    @After("receiveBasePointCut()")
    public void afterReceiveBasePointCut(JoinPoint joinPoint) throws Throwable{
        try {
            if (thriftPointCutter().isNoNeedAop(joinPoint.getTarget().getClass())){
                return;
            }
            Object[] args = joinPoint.getArgs();
            String interfaceName = joinPoint.getTarget().getClass().getName();
            String method = (String) args[1];
            String signature = interfaceName + "." + method;

            //获取请求参数
            TBase methodArg = THREAD_LOCAL.get();
            //构建日志对象
            ThriftUnit unit = ThriftUnit.builder()
                    .signature(signature)
                    .arg(methodArg)
                    .result((TBase) args[0])
                    .build();
            //上报日志
            reqRecordLogger.logUnit(unit);



        }catch (Exception e){
            throw e;
        }finally {
            THREAD_LOCAL.remove();
        }
    }

    private ThriftPointCutter thriftPointCutter(){
        return (ThriftPointCutter) ApplicationContextUtil.applicationContext.getBean("thriftPointCutter");
    }
}
