/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 09.05.2014
 */
package net.ifao.pci.logging.smtp;

import net.ifao.pci.logging.AsynchronousHandler;

/**
 * Implementation of java.util.logging.Handler for syslog protocol. Requires explicit initialization
 * through the setter methods. The initialization is assumed complete when publish() is called the
 * first time
 * @see SmtpConfiguration for configuration details 
 */
public class SmtpHandlerBean extends AsynchronousHandler<SmtpConfiguration> {

  public SmtpHandlerBean() {
    super( new SmtpConfiguration() );
  }
  
  SmtpConfiguration config;

  /**
   * @param to
   * @see net.ifao.pci.logging.smtp.SmtpConfiguration#setTo(java.lang.String)
   */
  public final void setTo(String to) {
    config.setTo( to );
  }

  /**
   * @param subject
   * @see net.ifao.pci.logging.smtp.SmtpConfiguration#setSubject(java.lang.String)
   */
  public final void setSubject(String subject) {
    config.setSubject( subject );
  }

  /**
   * @param smtpUsername
   * @see net.ifao.pci.logging.smtp.SmtpConfiguration#setSmtpUsername(java.lang.String)
   */
  public final void setSmtpUsername(String smtpUsername) {
    config.setSmtpUsername( smtpUsername );
  }

  /**
   * @param smtpPassword
   * @see net.ifao.pci.logging.smtp.SmtpConfiguration#setSmtpPassword(java.lang.String)
   */
  public final void setSmtpPassword(String smtpPassword) {
    config.setSmtpPassword( smtpPassword );
  }

  /**
   * @param protocol
   * @see net.ifao.pci.logging.smtp.SmtpConfiguration#setProtocol(java.lang.String)
   */
  public final void setProtocol(String protocol) {
    config.setProtocol( protocol );
  }
}