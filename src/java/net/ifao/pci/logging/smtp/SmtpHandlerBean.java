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
 * Implementation of java.util.logging.Handler for syslog protocol.
 * Requires explicit initialization through the setter methods.
 * The initialization is assumed complete when publish() is called the first time
 */
public class SmtpHandlerBean extends AsynchronousHandler<SmtpConfiguration> {

  public SmtpHandlerBean() {
    super( new SmtpConfiguration() );
  }
  
  // TODO: add specific setters for SMTP config.
}