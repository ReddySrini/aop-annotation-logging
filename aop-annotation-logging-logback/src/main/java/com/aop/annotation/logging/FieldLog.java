package com.aop.annotation.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ashok Goli (agoli)
 * The Interface FieldLog.
 * Used for Field Level Logging
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface FieldLog {

    /**
     * Read. If true, Log's the field value whenever the field is read
     *
     * @return true, if successful
     */
    boolean read() default false;
    
    /**
     * Write. If true, Log's the field value whenever the field is set/modified
     *
     * @return true, if successful
     */
    boolean write() default true;
    
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