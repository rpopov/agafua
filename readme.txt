Sources:
  http://en.wikipedia.org/wiki/Syslog
  http://www.ietf.org/rfc/rfc3164.txt
  http://www.ietf.org/rfc/rfc5424.txt
  http://tools.ietf.org/html/rfc5424
  http://www.oracle.com/technetwork/java/javamail/index.html

RATIONALE: 
  For PCI DSS certification purposes it is needed:
  - all applications in scope to report their PCI-related events and alerts to the SYSLOG services
  - the applications in scope should send by email alerts
  The approach to apply is the applications that standard java logging API to be extended to use 
  specific loggers dedicated for PCI purposes in the cases when PCI events or alerts are needed to be logged.
  Behind the java standard logging API those loggers are configured.
  This way the changes of the applications will be minimal.
  
Configuring the PCI-related loggers

  Logging to SYSLOG:
    The RFC3164, RFC5424 state that each message sent to SYSLOG must have a facility. 
    The java standard loggers do not provide such facilities. 
    Thus, when logging messages to SYSLOG through the java standard java.utl.logging API, the facility must be 
    provided by the log's handler itself. 
    *In a single application many facilities may be in use.*
        
  Logging events by EMAIL:
    The different events and alerts might need separate email delivery parameters set in the handlers.
    
  The standard format and conventions for logging.properties, stated in java.util.logging.LogManager, define a 
  mechanism to *set up uniformly all handlers of the same class*, not allowing different handlers of the same class to 
  have different facility assigned. 
  
  As a result: We extend the syntax of logging.properties file by using net.ifao.pci.logger.PciLogManager as
               a LogManager. It becomes a general mechanism to use bean-style handlers, initialized through corresponding
               setter methods. 
                             
  syslog_logger.jar provides bean-style handlers:
    net.ifao.pci.logging.syslog.SyslogHandlerBean 
    net.ifao.pci.logging.email.EMailHandlerBean (requires lib/mail.jar in system classpath)
     
  syslog_logger.jar java logging standard handlers (with class-level configuration)
    com.agafua.syslog.SyslogHandler
    net.ifao.pci.logging.email.EMailHandler  (requires lib/mail.jar in system classpath)
               

1. Configure logging messages to syslog and email

#***********************************************************
# Extended syntax (see net.ifao.pci.logger.PciLogManager) for Java SE logging.properties file:
#
# <logger class>.handler.names = comma or space-separated names of handlers
# 
# <handler name>.class = qualified name of the handler's class
#                        for logging to SYSLOG this class is:  net.ifao.syslog.logger.SyslogHandlerBean
#  
# <handler name>.level = log level
# <handler name>.<property name> = any string value
#   calls the <handler class>.set<property name>(String) with that value
#   
# The <handler class>.set<property name>(String) methods should accept 
#  null and empty values, in case no value is provided in the logging.properties
#  string representation of the actual values to set (like numbers, enums, etc.)
#  
#***********************************************************

 Usage:
   Include syslog.logger.jar in classpath.
   If email logging would be used, include lib/mail.jar in classpath.
   In the java command line add the parameters:
   -Djava.util.logging.manager=net.ifao.pci.logger.PciLogManager
   -Djava.util.logging.config.file=<path>/logging.properties 

2. In logging.properties define loggers and handler, for example:

pci.logger.user.handler.names     = syslog.user
pci.logger.auth.handler.names     = syslog.auth, syslog.security
pci.logger.log.audit.handler.names= syslog.log.audit
pci.logger.log.alert.handler.names= syslog.log.alert, email.alert


# Named PCI DSS handlers to communicate with SYSLOG service:
# NOTE: 
#  1. Each SYSLOG handler reports (to) a specific SYSLOG FACILITY
#  2. setup the application ID to the actual application pushing those messages

syslog.user.class = net.ifao.syslog.logger.SyslogHandlerBean
# Allowed values of net.ifao.syslog.logger.SyslogHandlerBean.transport property are: TCP, UDP (default)
syslog.user.transport = TCP
syslog.user.host = localhost
syslog.user.port = 514
syslog.user.applicationId = test-application
# Allowed values of net.ifao.syslog.logger.SyslogHandlerBean.facility are: KERN, USER, MAIL, DAEMON, AUTH, SYSLOG, LPR, NEWS, UUCP, CRON, SECURITY, FTP, NTP, LOGAUDIT, LOGALERT, CLOCK, LOCAL0, LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5, LOCAL6, LOCAL7
syslog.user.facility = USER
# Allowed values of net.ifao.syslog.logger.SyslogHandlerBean.rfc are: RFC3164, RFC5424 (default)


syslog.auth.class = net.ifao.syslog.logger.SyslogHandlerBean 
syslog.auth.transport = TCP
syslog.auth.host = localhost
syslog.auth.port = 514
syslog.auth.applicationId = test-application
syslog.auth.facility = AUTH


syslog.security.class = net.ifao.syslog.logger.SyslogHandlerBean
syslog.security.transport = TCP
syslog.security.host = localhost
syslog.security.port = 514
syslog.security.applicationId = test-application
syslog.security.facility = SECURITY


syslog.log.audit.class = net.ifao.syslog.logger.SyslogHandlerBean
syslog.log.audit.transport = TCP
syslog.log.audit.host = localhost
syslog.log.audit.port = 514
syslog.log.audit.applicationId = test-application
syslog.log.audit.facility = LOGAUDIT


syslog.log.alert.class = net.ifao.syslog.logger.SyslogHandlerBean
syslog.log.alert.transport = TCP
syslog.log.alert.host = localhost
syslog.log.alert.port = 514
syslog.log.alert.applicationId = test-application
syslog.log.alert.facility = LOGALERT

email.alert.class = net.ifao.pci.logging.email.EMailHandlerBean
email.alert.applicationId = <unique application name to use in FROM:>
email.alert.host = <mail server>
email.alert.protocol = smtp
email.alert.port = 465
email.alert.subject = 
email.alert.to = <to whom to deliver the messages>
email.alert.user = <set the user to log in the email server>


3. Use the specific loggers in the Java application to report specific events to, by using their pure names:
    Example:

    Logger PCI_LOGGER_USER = Logger.getLogger("pci.logger.user");

      and log to them  the standard way:

    PCI_LOGGER_USER.log(...);
