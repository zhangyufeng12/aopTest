package org.example.AopMock.recordPlay;


import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TBase;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.AopMock.common.ThriftPointCutter;
import org.example.AopMock.common.utils.ApplicationContextUtil;

/**
 * 流量回放
 */
@Aspect
public class ThriftAop {

    private static boolean shouldplay;

    static {
        if (StringUtils.isNotBlank(System.getenv(""))){
            shouldplay = true;
        }
    }

    //用于记录请求的args，做request和response的匹配    TBase是thrift的request和response参数类型
    private static final ThreadLocal<TBase> THREAD_LOCAL = new ThreadLocal<>();

    //发送请求处做切面
    @Pointcut("execution(void org.apache.thrift.TServiceClient.sendBase(java.lang.String, org.apache.thrift.TBase))")
    public void sendBasePointCut() {

    }

    //返回结果做切面
    @Pointcut("execution(void org.apache.thrift.TServiceClient.receiveBase(org.apache.thrift.TBase, java.lang.String ))")
    public void receiveBasePointCut() {

    }

    @Before("sendBasePointCut()")
    public void beforeSendBasePointCut(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();
        if (thriftPointCutter().isNoNeedAop(joinPoint.getTarget().getClass())){
            joinPoint.proceed(args);
            THREAD_LOCAL.remove();
            return;
        }
        //将请求参数设置到ThreadLocal中
        THREAD_LOCAL.set((TBase) args[1]);
    }

    @Before("receiveBasePointCut()")
    public void beforeReceiveBasePointCut(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();
        if (thriftPointCutter().isNoNeedAop(joinPoint.getTarget().getClass())){
            joinPoint.proceed(args);
            THREAD_LOCAL.remove();
            return;
        }
        try {
            String interfaceName = joinPoint.getTarget().getClass().getName();
            String method = (String) args[1];
            String signature = interfaceName + "." + method;

            //获取请求参数
            TBase methodArg = THREAD_LOCAL.get();
            if (methodArg!=null){
                //根据方法签名和请求参数查找匹配结果
                TBase res = thriftPointCutter().findResult(signature,methodArg);
                //如果存在匹配结果
                if (res != null){
                    args[0] = res;
                    return;
                }else {
                    joinPoint.proceed(args);
                    THREAD_LOCAL.remove();
                    return;
                }
            }
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
