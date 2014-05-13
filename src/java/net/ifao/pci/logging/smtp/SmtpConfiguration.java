package net.ifao.pci.logging.smtp;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.ifao.pci.logging.NetworkSender;

import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Connector;

/**
 * Specific configuration parameters to deliver log records by email.
 * The emails are sent from &lt;application id&gt;@&lt;local host name&gt;
 * Allows providing as system properties from the command line, any parameters to the 
 * underlying Java Mail component, as of http://www.oracle.com/technetwork/java/javamail/index.html
 * The property values provided in the configuration file override those provided as system properties. 
 */
public class SmtpConfiguration extends Configuration {
  private String to;
  private String subject;
  
  private String protocol = "smtp";

  private String smtpUsername;
  private String smtpPassword;


  /**
   * Establish a new mail session though Java Mail API
   * @return not null session
   */
  public Session constructSession() {
    Session result;
    Properties props;
    Authenticator auth;
    
    props = new Properties( System.getProperties() );

    put( props, "mail.host", getRemoteHostName() );
    put( props, "mail.user", getSmtpUsername() );
    put( props, "mail.transport.protocol", getProtocol() );

    auth = new javax.mail.Authenticator() {
      /**
       * @see javax.mail.Authenticator#getPasswordAuthentication()
       */
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication( getSmtpUsername(), getSmtpPassword() );
      }
    };
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
    return (NetworkSender<C>) new SmtpSender( (Connector<SmtpConfiguration>) connector );
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
   * @see #smtpUsername
   */
  public final String getSmtpUsername() {
    return smtpUsername;
  }


  /**
   * @see #smtpUsername
   */
  public final void setSmtpUsername(String smtpUsername) {
    if ( smtpUsername != null && !smtpUsername.isEmpty() ) {
      this.smtpUsername = smtpUsername;
    }
  }


  /**
   * @see #smtpPassword
   */
  public final String getSmtpPassword() {
    return smtpPassword;
  }


  /**
   * @see #smtpPassword
   */
  public final void setSmtpPassword(String smtpPassword) {
    if ( smtpPassword != null && !smtpPassword.isEmpty() ) {
      this.smtpPassword = smtpPassword;
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