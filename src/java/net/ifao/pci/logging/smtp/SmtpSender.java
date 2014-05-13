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
import java.util.Date;
import java.util.logging.LogRecord;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import net.ifao.pci.logging.NetworkSender;

import com.agafua.syslog.sender.Connector;

/**
 * Asynchronously send the logged records as email messages 
 * @author rpopov
 */
public class SmtpSender extends NetworkSender<SmtpConfiguration> {

  /**
   * Cache the mail session
   */
  private Session mailSession; 
  
  /**
   * @param connector
   */
  public SmtpSender(Connector<SmtpConfiguration> connector) {
    super( connector );
  }


  protected void establishConnection() throws IOException {
    if ( mailSession == null ) {
      mailSession = getConfiguration().constructSession();
    }
  }

  /**
   * Send the record as an email
   * @see net.ifao.pci.logging.NetworkSender#sendMessage(java.util.logging.LogRecord)
   */
  protected void sendMessage(LogRecord record) throws IOException {
    MimeMessage message; 

    try {
      message = getConfiguration().createMessage( mailSession );
            
      message.setSentDate(new Date(record.getMillis()));
      
      message.setText( getConfiguration().getFormatter().format( record ) );
      
      Transport.send(message);      
    } catch (Exception ex) {
      throw new IOException(ex);
    }
  }


  /**
   * @see net.ifao.pci.logging.NetworkSender#releaseResources()
   */
  protected void releaseResources() {
    // no need of releasing the session
  }

  /**
   * @see net.ifao.pci.logging.NetworkSender#describeConnection()
   */
  protected String describeConnection() {
    return  "Sending email to host:"+getConfiguration().getRemoteHostName()
           +" and port:"+ getConfiguration().getPort()
           +" using protocol:"+getConfiguration().getProtocol();
  }
}