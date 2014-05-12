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

import java.util.logging.Handler;
import java.util.logging.Logger;

import junit.framework.TestCase;

public class PciLogManagerInspectTest extends TestCase {
  /**
   * Just lookup the loggers
   */
  public void test() {
    Logger logger;
    boolean syslogHandlerFound;
    
    logger = Logger.getLogger("pci.logger.kernel");
    
    assertTrue("Expected handlers are bound to the logger", logger.getHandlers().length > 0);
    
    // check there is a syslog handler for that logger
    syslogHandlerFound = false;
    for (Handler handler: logger.getHandlers()) {
      syslogHandlerFound |= handler instanceof net.ifao.syslog.logger.SyslogHandlerBean;
    }
    assertTrue("Expected a syslog handler bound to the logger", syslogHandlerFound);
  }  
}
