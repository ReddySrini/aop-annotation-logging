package com.aop.annotation.logging;

import java.awt.Color;

import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

/**
 * The Class AOPLoggerTest.
 */
public class AOPLoggerTest {

  /** The Constant log. */
  private static final org.slf4j.Logger log = LoggerFactory.getLogger(AOPLoggerTest.class);

  /**
   * The main method.
   * 
   * @param args the arguments
   */
  public static void main(String[] args) {
    
    TestPojo testPojo = new TestPojo("Ashok", "Goli", Color.BLACK, 26);

    testPojo.setFirstName("Ashok");
    testPojo.setLastName("Kumar");
    testPojo.setAge(69);

    log.debug(testPojo.getFirstName());
    log.debug(testPojo.getLastName());

    /*
     * None of this will be logged because of the annotations.
     */
    ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.WARN);

    TestPojo notLogged = new TestPojo("Gopi", "Dara", Color.WHITE, 25);

    log.debug(notLogged.getFirstName())
    ;
    log.debug(notLogged.getLastName());
    // This warning will be logged.
    log.warn("notLogged.getAge(): " + notLogged.getAge());

  }
}

