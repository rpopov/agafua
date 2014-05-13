/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by i:FAO AG as part
 * of a product of i:FAO AG for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information.
 * 
 * Created on 13.05.2014
 */
package net.ifao.pci.logging.smtp;

import java.io.IOException;
import java.util.logging.LogRecord;

import com.agafua.syslog.sender.Connector;

import net.ifao.pci.logging.NetworkSender;

/**
 * 
 * TODO Describe the purpose of this class
 * TODO Describe the class' usage
 * TODO Mention any concurrency considerations
 * TODO List all known bugs
 *
 * @author rpopov
 * @version $Revision$ $Date$
 */
public class SmtpSender extends NetworkSender<SmtpConfiguration> {

  public SmtpSender(Connector<SmtpConfiguration> connector) {
    super( connector );
  }


  protected String describeConnection() {
    //TODO Complete this.
    return null;
  }


  @Override
  protected void establishConnection() throws IOException {
    //TODO Complete this.

  }


  @Override
  protected void sendMessage(LogRecord record) throws IOException {
    //TODO Complete this.

  }


  @Override
  protected void releaseResources() {
    //TODO Complete this.

  }

}
