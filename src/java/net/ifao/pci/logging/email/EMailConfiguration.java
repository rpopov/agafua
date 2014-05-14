/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 12.05.2014
 */
package net.ifao.pci.logging.email;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.ifao.pci.logging.internal.NetworkSender;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Connector;

/**
 * Specific configuration parameters to deliver log records by email.
 * The emails are sent from &lt;application id&gt;@&lt;local host name&gt;
 * Allows providing as system properties from the command line, any parameters to the 
 * underlying Java Mail component, as of http://www.oracle.com/technetwork/java/javamail/index.html
 * The property values provided in the configuration file override those provided as system properties:<ul>
 * <li> <b>host</b> configuration property overrides the  <b>mail.host</b> system property, if ones specified
 * <li> <b>user</b> configuration property overrides the <b>mail.user</b> system property, if ones provided.
 *      If not provided, the applicationId is used instead.     
 * <li> <b>protocol</b> configuration property overrides <b>mail.transport.protocol</b> system property  
 * </ul>
 */
public class EMailConfiguration extends Configuration {
  private String to;
  private String subject;
  
  private String protocol = "smtp";

  private String user;
  private String password;


  /**
   * Establish a new mail session though Java Mail API
   * @return not null session
   */
  public Session constructSession() {
    Session result;
    Properties props;
    Authenticator auth;
    
    props = new Properties( System.getProperties() );

    put( props, "mail.host", getHost() );
    
    if ( getUser() != null ) {
      put( props, "mail.user", getUser() );
    } else { 
      put( props, "mail.user", getApplicationId() );      
    }
    put( props, "mail.transport.protocol", getProtocol() );

    if (getPassword() == null) { // no authentication 
      auth = null;      
    } else {
      auth = new javax.mail.Authenticator() {
        /**
         * @see javax.mail.Authenticator#getPasswordAuthentication()
         */
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication( getUser(), getPassword() );
        }
      };
    }
    result = Session.getInstance( props, auth );
    
    return result;
  }

  /**
   * Set and validate the property is not empty 
   * @param props
   * @param string
   * @param remoteHostName
   */
  private void put(Properties props, String key, String value) {
    String systemValue; 
    
    if ( value == null || value.trim().isEmpty() ) { // no value set in logging.properties, check the system ones 
      systemValue = props.getProperty( key );
      if ( systemValue == null || systemValue.trim().isEmpty() ) {
        throw new IllegalArgumentException("Expected property '"+key+"' is set either in the logging.properties file or as a system property");
      }
    }    
    props.put(key, value);      
  }

  /**
   * @throws MessagingException
   */
  public MimeMessage createMessage(Session mailSession) throws MessagingException {
    MimeMessage msg = new MimeMessage(mailSession);
    String from;
    
    from = getApplicationId()
           + "@"
           + getLocalHostName();
    
    msg.setFrom(new InternetAddress(from));
    msg.setRecipients(Message.RecipientType.TO, to);
    msg.setSubject(subject);
    return msg;
  }


  /**
   * @see com.agafua.syslog.sender.BasicConfiguration#constructSender(Connector<C>)
   */
  protected <C extends Configuration> NetworkSender<C> constructSender(Connector<C> connector) {
    return (NetworkSender<C>) new EMailSender( (Connector<EMailConfiguration>) connector );
  }


  /**
   * @see #to
   */
  public final String getTo() {
    return to;
  }


  /**
   * @see #to
   */
  public final void setTo(String to) {
    if ( to != null && !to.isEmpty() ) {
      // guarantee there are no line breaks in the subject      
      this.to = to.replace( '\n', ' ' ).replace('\r', ' ');      
    }
  }

  /**
   * @see #subject
   */
  public final String getSubject() {
    return subject;
  }


  /**
   * @see #subject
   */
  public final void setSubject(String subject) {
    if ( subject != null && !subject.isEmpty() ) {
      // guarantee there are no line breaks in the subject      
      this.subject = subject.replace( '\n', ' ' ).replace('\r', ' ');
    }
  }


  /**
   * @see #user
   */
  public final String getUser() {
    return user;
  }


  /**
   * @see #user
   */
  public final void setUser(String smtpUsername) {
    if ( smtpUsername != null && !smtpUsername.isEmpty() ) {
      this.user = smtpUsername;
    }
  }


  /**
   * @see #password
   */
  public final String getPassword() {
    return password;
  }


  /**
   * @see #password
   */
  public final void setPassword(String smtpPassword) {
    if ( smtpPassword != null && !smtpPassword.isEmpty() ) {
      this.password = smtpPassword;
    }
  }


  /**
   * @see #protocol
   */
  public final String getProtocol() {
    return protocol;
  }


  /**
   * @see #protocol
   */
  public final void setProtocol(String protocol) {
    if ( protocol != null && !protocol.trim().isEmpty() ) {
      this.protocol = protocol;
    }
  }  
}