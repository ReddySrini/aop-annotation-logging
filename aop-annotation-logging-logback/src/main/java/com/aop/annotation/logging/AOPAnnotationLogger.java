package com.aop.annotation.logging;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

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
        boolean enabledForLevel = ((Logger)LoggerFactory.getLogger(classname)).isEnabledFor(level);

        if (enabledForLevel && methodLog.entry()) {
            String enterMsg = "ENTER: " 
                    + methodLog.prefix() 
                    + proceedingJoinPoint.getSignature().toShortString()  
                    + methodLog.suffix();
            ((Logger)LoggerFactory.getLogger(classname)).log(null, null, convertToLocationAwareLevelInt(level), enterMsg, null, null);
            if(methodLog.params()){
              String parmsMsg = "\tPARAMS: " + Arrays.toString(args);
              ((Logger)LoggerFactory.getLogger(classname)).log(null, null, convertToLocationAwareLevelInt(level), parmsMsg, null, null);
            }
            
        }
        Object methodResult = proceedingJoinPoint.proceed();
        if (enabledForLevel && methodLog.exit()) {
            String exitMsg = "EXIT: " 
                    + methodLog.prefix() 
                    + proceedingJoinPoint.getSignature().toShortString()  
                    + methodLog.suffix();
            ((Logger)LoggerFactory.getLogger(classname)).log(null, null, convertToLocationAwareLevelInt(level), exitMsg, null, null);
            if(methodLog.returnVal()){
              String rtrnMsg = "\tRETURNING: " 
                      + (methodResult == null ? "null" : methodResult.toString());
              ((Logger)LoggerFactory.getLogger(classname)).log(null, null, convertToLocationAwareLevelInt(level), rtrnMsg, null, null);
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
        if (((Logger)LoggerFactory.getLogger(classname)).isEnabledFor(level) && fieldLog.read()) {
            String readMsg = "READING: " 
                    + fieldLog.prefix() 
                    + joinPoint.getSignature().toShortString()  
                    + fieldLog.suffix();
            ((Logger)LoggerFactory.getLogger(classname)).log(null, null, convertToLocationAwareLevelInt(level), readMsg, null, null);
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
        if (((Logger)LoggerFactory.getLogger(classname)).isEnabledFor(level) && fieldLog.write()) {
            String writeMsg = "ASSIGNING: " 
                    + fieldLog.prefix() 
                    + joinPoint.getSignature().toShortString()  
                    + " = " 
                    + "'" + (newval == null ? "null" : newval.toString()) + "'" 
                    + fieldLog.suffix();
            ((Logger)LoggerFactory.getLogger(classname)).log(null, null, convertToLocationAwareLevelInt(level), writeMsg, null, null);
        }
    }
    
    private int convertToLocationAwareLevelInt(Level loglevel){
      int level;
      String string = loglevel.toString();
      if (LogLevel.TRACE.toString().equals(string)) {
        level = LocationAwareLogger.TRACE_INT;
      } else if (LogLevel.DEBUG.toString().equals(string)) {
        level = LocationAwareLogger.DEBUG_INT;
      } else if (LogLevel.INFO.toString().equals(string)) {
        level = LocationAwareLogger.INFO_INT;
      } else if (LogLevel.WARN.toString().equals(string)) {
        level = LocationAwareLogger.WARN_INT;
      } else if (LogLevel.ERROR.toString().equals(string)) {
        level = LocationAwareLogger.ERROR_INT;
      } else {
        throw new IllegalArgumentException(loglevel.toString() + " not a valid level value");
      }
      return level;
    }

}