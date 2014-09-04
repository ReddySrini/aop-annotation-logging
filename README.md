aop-annotation-logging
======================

Implementing AOP combined with Java Annotations Logging.

To execute this project, run the main method in AOPLoggerTest.java

Please make sure you have AspectJ Tooling enabled for your project for correct results.
To do this in Spring Tool Suite, 
  - Import the the appropriate project into STS, 
  - Right click on the project and select Spring Tools > Enable Spring Aspects Tooling.
This will add AspectJ Runtime Library to your project's build path. 

Executing the project after this, would enable the aspects.

Usage:
 - Import this project as a dependency.
 - Add @MethodLog Annotation with the appropriate params passed.
```
    @MethodLog(prefix="Constructor", level=LogLevel.INFO)
    public TestPojo(String firstName, String lastName, Color favoriteColor, int age) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.favoriteColor = favoriteColor;
        this.age = age;
    }

    @MethodLog
    public String getFirstName() {
        return firstName;
    }
```
 - Add @FieldLog Annotation with the appropriate params passed.
```
    @FieldLog(write=false)
    private String firstName;

    @FieldLog(read=false)
    private String lastName; 

    @FieldLog(prefix="Don't be shy. Pick your favorite color: ")
    private Color favoriteColor; 

    @FieldLog(suffix=". You are Ancient. Honestly.")
    private int age;
```


Code:
MethodLog Annotation
```
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface MethodLo    @MethodLog(prefix="Constructor", level=LogLevel.INFO)
    public TestPojo(String firstName, String lastName, Color favoriteColor, int age) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.favoriteColor = favoriteColor;
        this.age = age;
    }

    @MethodLog
    public String getFirstName() {
        return firstName;
    }g {

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
```

FieldLog Annotation
```
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
```

The AOP Implementation for annotations. Below code is a sample for Log4j framework
Note: This will be logging framework specific:
```
/**
 * @author Ashok Goli (agoli) The Class AopLogger.
 * 
 */
@Aspect
public class AOPAnnotationLogger {

	/**
	 * Log method based on passed annotation values. Method works only for
	 * specified package
	 * 
	 * @param proceedingJoinPoint
	 *            the pjp
	 * @param methodLog
	 *            the method logging
	 * @return the object
	 * @throws Throwable
	 *             the throwable
	 */
	@Around(value = "execution(@MethodLog * *(..)) && @annotation(methodLog)", argNames = "methodLog")
	public Object logMethod(ProceedingJoinPoint proceedingJoinPoint, MethodLog methodLog) throws Throwable {
		Level level = Level.toLevel(methodLog.level().toString());
		StaticPart sp = proceedingJoinPoint.getStaticPart();
		String classname = sp.getSignature().getDeclaringTypeName();
		Object[] args = proceedingJoinPoint.getArgs();
		boolean enabledForLevel = Logger.getLogger(classname).isEnabledFor(level);

		if (enabledForLevel && methodLog.entry()) {
			String enterMsg = "ENTER: " + methodLog.prefix() + proceedingJoinPoint.getSignature().toShortString() + methodLog.suffix();
			Logger.getLogger(classname).log(level, enterMsg);
			if (methodLog.params()) {
				String parmsMsg = "\tPARAMS: " + Arrays.toString(args);
				Logger.getLogger(classname).log(level, parmsMsg);
			}

		}
		Object methodResult = proceedingJoinPoint.proceed();
		if (enabledForLevel && methodLog.exit()) {
			String exitMsg = "EXIT: " + methodLog.prefix() + proceedingJoinPoint.getSignature().toShortString() + methodLog.suffix();
			Logger.getLogger(classname).log(level, exitMsg);
			if (methodLog.returnVal()) {
				String rtrnMsg = "\tRETURNING: " + (methodResult == null ? "null" : methodResult.toString());
				Logger.getLogger(classname).log(level, rtrnMsg);
			}
		}
		return methodResult;
	}

	/**
	 * Log field read.
	 * 
	 * @param joinPoint
	 *            the jp
	 * @param fieldLog
	 *            the field logging
	 */
	@Before("get(@FieldLog * *) && @annotation(fieldLog)")
	public void logFieldRead(JoinPoint joinPoint, FieldLog fieldLog) {
		Level level = Level.toLevel(fieldLog.level().toString());
		StaticPart sp = joinPoint.getStaticPart();
		String classname = sp.getSignature().getDeclaringTypeName();
		if (Logger.getLogger(classname).isEnabledFor(level) && fieldLog.read()) {
			String readMsg = "READING: " + fieldLog.prefix() + joinPoint.getSignature().toShortString() + fieldLog.suffix();
			Logger.getLogger(classname).log(level, readMsg);
		}
	}

	/**
	 * Log field write.
	 * 
	 * @param joinPoint
	 *            the jp
	 * @param fieldLog
	 *            the field logging
	 * @param newval
	 *            the newval
	 */
	@Before("set(@FieldLog * *) && @annotation(fieldLog) && args(newval)")
	public void logFieldWrite(JoinPoint joinPoint, FieldLog fieldLog, Object newval) {
		Level level = Level.toLevel(fieldLog.level().toString());
		StaticPart sp = joinPoint.getStaticPart();
		String classname = sp.getSignature().getDeclaringTypeName();
		if (Logger.getLogger(classname).isEnabledFor(level) && fieldLog.write()) {
			String writeMsg = "ASSIGNING: " + fieldLog.prefix() + joinPoint.getSignature().toShortString() + " = " + "'" + (newval == null ? "null" : newval.toString()) + "'" + fieldLog.suffix();
			Logger.getLogger(classname).log(level, writeMsg);
		}
	}

}
```
