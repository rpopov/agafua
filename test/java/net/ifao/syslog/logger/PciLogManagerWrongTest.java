/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by i:FAO AG as part
 * of a product of i:FAO AG for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information.
 * 
 * Created on 10.05.2014
 */
package net.ifao.syslog.logger;

import java.util.logging.Logger;

import junit.framework.TestCase;

/**
 * Check how wrong parameters are parsed 
 */
public class PciLogManagerWrongTest extends TestCase {

  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    System.err.println("Note: EXPECTED are exceptions printed in the system error and this DOES NOT FAIL the tests");
  }

  public void testNotExistingLogger() {
    Logger.getLogger("pci.logger.not.existing");
      
    // the root logger should be used and the client is not interrupted
    assertTrue("Success", true);
  }

  public void testWrongHandlerName() {
    Logger.getLogger("pci.logger.wrong.handler.name");

    // the root logger should be used and the client is not interrupted
    assertTrue("Success", true);
  }

  public void testWrongHandlerClassName() {
    Logger.getLogger("pci.logger.wrong.handler.class.name");
 
    // the root logger should be used and the client is not interrupted
    assertTrue("Success", true);
  }

  public void testWrongPropertyName() {
    Logger.getLogger("pci.logger.wrong.property.name");

    // the root logger should be used and the client is not interrupted
    assertTrue("Success", true);
  }

  public void testWrongPropertyValue() {
    Logger.getLogger("pci.logger.wrong.property.value");

    // the root logger should be used and the client is not interrupted
    assertTrue("Success", true);
  }
}
