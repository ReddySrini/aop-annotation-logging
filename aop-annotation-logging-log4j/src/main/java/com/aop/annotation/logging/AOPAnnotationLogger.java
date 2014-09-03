package com.aop.annotation.logging;

import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author Ashok Goli (agoli)
 * The Class AopLogger.
 *
 */
@Aspect
public class AOPAnnotationLogger {

    /**
     * Log method based on passed annotation values. Method works only for specified package
     *
     * @param proceedingJoinPoint the pjp
     * @param methodLog the method logging
     * @return the object
     * @throws Throwable the throwable
     */
    @Around(value="execution(@MethodLog * *(..)) && @annotation(methodLog)", argNames="methodLog")
    public Object logMethod(ProceedingJoinPoint proceedingJoinPoint, MethodLog methodLog) throws Throwable {
        Level level = Level.toLevel(methodLog.level().toString());
        StaticPart sp = proceedingJoinPoint.getStaticPart();
        String classname = sp.getSignature().getDeclaringTypeName();
        Object[] args = proceedingJoinPoint.getArgs();
        boolean enabledForLevel = Logger.getLogger(classname).isEnabledFor(level);

        if (enabledForLevel && methodLog.entry()) {
            String enterMsg = "ENTER: " 
                    + methodLog.prefix() 
                    + proceedingJoinPoint.getSignature().toShortString()  
                    + methodLog.suffix();
            Logger.getLogger(classname).log(level, enterMsg);
            if(methodLog.params()){
              String parmsMsg = "\tPARAMS: " + Arrays.toString(args);
              Logger.getLogger(classname).log(level, parmsMsg);
            }
            
        }
        Object methodResult = proceedingJoinPoint.proceed();
        if (enabledForLevel && methodLog.exit()) {
            String exitMsg = "EXIT: " 
                    + methodLog.prefix() 
                    + proceedingJoinPoint.getSignature().toShortString()  
                    + methodLog.suffix();
            Logger.getLogger(classname).log(level, exitMsg);
            if(methodLog.returnVal()){
              String rtrnMsg = "\tRETURNING: " 
                      + (methodResult == null ? "null" : methodResult.toString());
              Logger.getLogger(classname).log(level, rtrnMsg);
            }
        }
        return methodResult;
    }

    /**
     * Log field read.
     *
     * @param joinPoint the jp
     * @param fieldLog the field logging
     */
    @Before("get(@FieldLog * *) && @annotation(fieldLog)")
    public void logFieldRead(JoinPoint joinPoint, FieldLog fieldLog) {
        Level level = Level.toLevel(fieldLog.level().toString());
        StaticPart sp = joinPoint.getStaticPart();
        String classname = sp.getSignature().getDeclaringTypeName();
        if (Logger.getLogger(classname).isEnabledFor(level) && fieldLog.read()) {
            String readMsg = "READING: " 
                    + fieldLog.prefix() 
                    + joinPoint.getSignature().toShortString()  
                    + fieldLog.suffix();
            Logger.getLogger(classname).log(level, readMsg);
        }
    }

    /**
     * Log field write.
     *
     * @param joinPoint the jp
     * @param fieldLog the field logging
     * @param newval the newval
     */
    @Before("set(@FieldLog * *) && @annotation(fieldLog) && args(newval)")
    public void logFieldWrite(JoinPoint joinPoint, FieldLog fieldLog, Object newval) {
        Level level = Level.toLevel(fieldLog.level().toString());
        StaticPart sp = joinPoint.getStaticPart();
        String classname = sp.getSignature().getDeclaringTypeName();
        if (Logger.getLogger(classname).isEnabledFor(level) && fieldLog.write()) {
            String writeMsg = "ASSIGNING: " 
                    + fieldLog.prefix() 
                    + joinPoint.getSignature().toShortString()  
                    + " = " 
                    + "'" + (newval == null ? "null" : newval.toString()) + "'" 
                    + fieldLog.suffix();
            Logger.getLogger(classname).log(level, writeMsg);
        }
    }

}