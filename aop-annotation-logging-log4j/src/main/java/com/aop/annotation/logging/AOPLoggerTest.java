package com.aop.annotation.logging;

import java.awt.Color;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * The Class AOPLoggerTest.
 */
public class AOPLoggerTest {

  /** The Constant log. */
  private static final Logger log = Logger.getLogger(AOPLoggerTest.class);

  /**
   * The main method.
   * 
   * @param args the arguments
   */
  public static void main(String[] args) {
    /*
     * Initialize the Log4J logger
     */
    BasicConfigurator.configure();
    @SuppressWarnings("rawtypes")
    Enumeration appenders = Logger.getRootLogger().getAllAppenders();
    while (appenders.hasMoreElements()) {
      Appender appender = (Appender) appenders.nextElement();
      appender.setLayout(new PatternLayout("[%-5p] %m%n"));
    }

    Logger.getRootLogger().setLevel(Level.DEBUG);
    TestPojo testPojo = new TestPojo("Ashok", "Goli", Color.BLACK, 26);

    testPojo.setFirstName("Ashok");
    testPojo.setLastName("Kumar");
    testPojo.setAge(69);

    log.debug(testPojo.getFirstName());
    log.debug(testPojo.getLastName());

    /*
     * None of this will be logged because of the annotations.
     */
    Logger.getRootLogger().setLevel(Level.WARN);

    TestPojo notLogged = new TestPojo("Gopi", "Dara", Color.WHITE, 25);

    log.debug(notLogged.getFirstName());
    log.debug(notLogged.getLastName());
    // This warning will be logged.
    log.warn("notLogged.getAge(): " + notLogged.getAge());

  }
}
