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

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

/**
 * Test a connection to a real host with syslog configured for vegas project's purposes  
 * @author rpopov
 */
public class PciLogManagerAlertSmtpVegasTest extends TestCase {

  /**
   * Just lookup the loggers
   * @throws InterruptedException 
   */
  public void test() throws InterruptedException {
    Logger logger; 
    
    /// There must be no communication established
    logger = Logger.getLogger("vegas.pci.logger.log.alert");
    
    logger.log(Level.INFO, "Test message1");
    
    Thread.sleep( 10000 );
    
    // the root logger should be used and the client is not interrupted
    assertTrue("Success", true);
    System.err.println("verifiy manually in STDERR that NO messages were reporeted as not delivered");
  }
}
