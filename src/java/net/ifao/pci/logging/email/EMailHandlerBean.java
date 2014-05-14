/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 14.05.2014
 */
package net.ifao.pci.logging.email;

import java.util.logging.SimpleFormatter;

import net.ifao.pci.logging.PciLogManager;
import net.ifao.pci.logging.internal.AsynchronousHandlerBean;

/**
 * Implementation of java.util.logging.Handler to send log records by email. 
 * Requires explicit initialization through the setter methods. 
 * Requires PciLogManager to be used instead of the standard java.util.logging.LogMandager,
 * which allows multiple handler instances configured separately, in contrast to the common 
 * configuration the standard imposes.
 * Handler configuration properties (see {@link PciLogManager}):<ul>
 * <li>class = net.ifao.pci.logging.email.EMailHandlerBean 
 * <li>applicationId - the unique application that sends the log messages
 * <li>formatter - the class name of the standard log Formatter to apply for each message
 * <li>host - the mail server host
 * <li>password - the password to connect the mail server host
 * <li>port- the port to connect the mail server host at
 * <li>protocol - the mail sending protocol to use
 * <li>subject - the subject of the mail messages to send
 * <li>to - the email address to deliver the messages to
 * <li>user  - the user to connect the mail server host
 * </ul>
 * NOTE: <ul>
 * <li> <b>remoteHostName</b> configuration property overrides the  <b>mail.host</b> system property, if ones specified
 * <li> <b>user</b> configuration property overrides the <b>mail.user</b> system property, if ones provided
 * <li> <b>protocol</b> configuration property overrides <b>mail.transport.protocol</b> system property  
 * </ul>
 * This handler requires lib/mail.jar in system classpath
 * @see PciLogManager
 * @see EMailHandler 
 */
public class EMailHandlerBean extends AsynchronousHandlerBean<EMailConfiguration> {

  public EMailHandlerBean() {
    super( new EMailConfiguration() );
    setFormatter( new SimpleFormatter() );
  }
  
  /**
   * @param to
   * @see net.ifao.pci.logging.email.EMailConfiguration#setTo(java.lang.String)
   */
  public final void setTo(String to) {
    getConfiguration().setTo( to );
  }

  /**
   * @param subject
   * @see net.ifao.pci.logging.email.EMailConfiguration#setSubject(java.lang.String)
   */
  public final void setSubject(String subject) {
    getConfiguration().setSubject( subject );
  }

  /**
   * @param smtpUsername
   * @see net.ifao.pci.logging.email.EMailConfiguration#setUser(java.lang.String)
   */
  public final void setUser(String smtpUsername) {
    getConfiguration().setUser( smtpUsername );
  }

  /**
   * @param smtpPassword
   * @see net.ifao.pci.logging.email.EMailConfiguration#setPassword(java.lang.String)
   */
  public final void setPassword(String smtpPassword) {
    getConfiguration().setPassword( smtpPassword );
  }

  /**
   * @param protocol
   * @see net.ifao.pci.logging.email.EMailConfiguration#setProtocol(java.lang.String)
   */
  public final void setProtocol(String protocol) {
    getConfiguration().setProtocol( protocol );
  }  
}