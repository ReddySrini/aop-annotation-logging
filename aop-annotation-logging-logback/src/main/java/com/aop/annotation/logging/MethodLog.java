package com.aop.annotation.logging;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ashok Goli (agoli)
 * The Interface MethodLog.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface MethodLog {

    /**
     * Entry. If true, logs before entering method.
     *
     * @return true, if successful
     */
    boolean entry() default true;
    
    /**
     * Exit. If true, logs after exiting method.
     *
     * @return true, if successful
     */
    boolean exit() default true;
    
    /**
     * Params. If true, prints the parameter values (Arrays.toString)
     *
     * @return true, if successful
     */
    boolean params() default false;
    
    /**
     * Return val. If true, prints the return object value
     *
     * @return true, if successful
     */
    boolean returnVal() default false;
    
    
    /**
     * Prefix. Attaches the specified prefix to the Logging statement at the beginning
     *
     * @return the string
     */
    String prefix() default "";
    
    /**
     * Suffix. Attaches the specified suffix to the Logging statement at the end
     *
     * @return the string
     */
    String suffix() default "";
    
    /**
     * Level. Specify the Log Level. This logging level enabled is checked before printing logging statements
     *
     * @return the log level
     */
    LogLevel level() default LogLevel.DEBUG;

}