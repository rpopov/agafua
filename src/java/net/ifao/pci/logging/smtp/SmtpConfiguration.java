package net.ifao.pci.logging.smtp;

import net.ifao.pci.logging.NetworkSender;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Connector;

/**
 * Specific configuration parameters to deliver log records by email
 */
public class SmtpConfiguration extends Configuration {

  /**
   * @see com.agafua.syslog.sender.BasicConfiguration#constructSender(Connector<C>)
   */
  protected <C extends Configuration> NetworkSender<C> constructSender(Connector<C> connector) {
    return (NetworkSender<C>) new SmtpSender( (Connector<SmtpConfiguration>) connector );
  }  
}