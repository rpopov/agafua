/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 14.05.2014
 */
package net.ifao.pci.logging.email;

import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

import net.ifao.pci.logging.PciLogManager;
import net.ifao.pci.logging.internal.AsynchronousHandler;

/**
 * Implementation of java.util.logging.Handler to send log records by email
 * It can be integrated directly in the standard logging mechanism of Java. 
 * The standard java logging mechanism imposes all handlers of this class to have the same configuration parameters.
 * Configuration properties:<ul>
 * <li>net.ifao.pci.logging.email.EMailHandler.applicationId - the unique application that sends the log messages
 * <li>net.ifao.pci.logging.email.EMailHandler.formatter - the class name of the standard log Formatter to apply for each message
 * <li>net.ifao.pci.logging.email.EMailHandler.host - the mail server host
 * <li>net.ifao.pci.logging.email.EMailHandler.password - the password to connect the mail server host
 * <li>net.ifao.pci.logging.email.EMailHandler.port- the port to connect the mail server host at
 * <li>net.ifao.pci.logging.email.EMailHandler.protocol - the mail sending protocol to use
 * <li>net.ifao.pci.logging.email.EMailHandler.subject - the subject of the mail messages to send
 * <li>net.ifao.pci.logging.email.EMailHandler.to - the email address to deliver the messages to
 * <li>net.ifao.pci.logging.email.EMailHandler.user  - the user to connect the mail server host
 * </ul>
 * Note:<ul>
 * <li> <b>remoteHostName</b> configuration property overrides the  <b>mail.host</b> system property, if ones specified
 * <li> <b>user</b> configuration property overrides the <b>mail.user</b> system property, if ones provided
 * <li> <b>protocol</b> configuration property overrides <b>mail.transport.protocol</b> system property  
 * </ul>
 * This handler requires lib/mail.jar in system classpath
 * @see PciLogManager
 * @see EMailHandlerBean as an alternative 
 */
public class EMailHandler extends AsynchronousHandler<EMailConfiguration> {

  public EMailHandler() {
    super( new EMailConfiguration() );
    LogManager logManager;    
    
    setFormatter( new SimpleFormatter() );

    logManager = LogManager.getLogManager();
    
    try {
      getConfiguration().setApplicationId( logManager.getProperty( this.getClass().getName()+"."+"applicationId" ) );
      parsePort(logManager);
      getConfiguration().setHost( logManager.getProperty( this.getClass().getName()+"."+"host" ) );
      getConfiguration().setProtocol( logManager.getProperty( this.getClass().getName()+"."+"protocol" ) );
      getConfiguration().setTo( logManager.getProperty( this.getClass().getName()+"."+"to" ) );
      getConfiguration().setSubject( logManager.getProperty( this.getClass().getName()+"."+"subject" ) );
      getConfiguration().setUser( logManager.getProperty( this.getClass().getName()+"."+"user" ) );
      getConfiguration().setPassword( logManager.getProperty( this.getClass().getName()+"."+"password" ) );
      parseFormatter(logManager);
    } catch (IllegalArgumentException ex) {
      System.err.print("Initialization of the email logging mechanism failed: ");
      ex.printStackTrace();
    }       
  }

  private void parsePort(LogManager logManager) throws IllegalArgumentException {
    String setting;
    int port;
    
    setting = logManager.getProperty( this.getClass().getName()+"."+"port" );
    if ( setting != null && !setting.trim().isEmpty()) {
      try {
        port = Integer.valueOf( setting ).intValue();
        
        getConfiguration().setPort( port );
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Parsing port: "+setting+" for "+this.getClass().getName()+"caused:", ex);
      }
    }
  } 
  
  private void parseFormatter(LogManager logManager) throws IllegalArgumentException {
    Formatter formatter;
    Class<? extends Formatter> formatterClass;
    
    String formatterClassName = logManager.getProperty( this.getClass().getName() + "." + "formatter" );
    if ( formatterClassName != null ) {
      try {
        formatterClass = Class.forName( formatterClassName ).asSubclass( Formatter.class );
        formatter = formatterClass.newInstance();
        
        getConfiguration().setFormatter( formatter );
      } catch (  ClassNotFoundException 
               | InstantiationException 
               | IllegalAccessException ex) {
        throw new IllegalArgumentException("Could not initialize java.util.logging Formatter class: "+formatterClassName, ex);
      }
    }
  }  
}