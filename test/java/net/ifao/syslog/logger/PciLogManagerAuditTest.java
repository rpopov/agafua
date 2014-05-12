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

public class PciLogManagerAuditTest extends TestCase {

  /**
   * Just lookup the loggers
   */
  public void test() {
    Logger.getLogger("pci.logger.log.audit");
  }
}
