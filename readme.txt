#
# 1. Configure logging.properties
#
#***********************************************************
# Extended syntax (see net.ifao.syslog.logger.PciLogManager) for Java SE logging.properties file:
#
# <logger class>.handler.names = comma or space-separated names of handlers
# 
# <handler name>.class = qualified name of the handler's class
# <handler name>.level = log level
# <handler name>.<property name> = any string value
#   calls the <handler class>.set<property name>(String) with that value
#   
# The <handler class>.set<property name>(String) methods should accept 
#  null and empty values, in case no value is provided in the logging.properties
#  string representation of the actual values to set (like numbers, enums, etc.)
#      Recommended is the values' recognition to be case insensitive 
#  
# Usage:
#   In order to use the new syntax of logging.properties add in the java command line add the parameter
#   -Djava.util.logging.manager=net.ifao.syslog.logger.PciLogManager
#
#   Include syslog.logger.jar in classpath.
#
#***********************************************************
# The following are example LOGGERS in logging.properties for PCI DSS events, configured to be transferred to SYSLOG

# Assumption: All events logged through those loggers will be transferred to SYSLOG

pci.logger.kernel.level = ALL
pci.logger.kernel.handler.names = syslog.kernel

pci.logger.user.level = ALL
pci.logger.user.handler.names = syslog.user

pci.logger.auth.level = ALL
pci.logger.auth.handler.names = syslog.auth

pci.logger.security.level = ALL
pci.logger.security.handler.names = syslog.auth, syslog.security

pci.logger.log.audit.level = ALL
pci.logger.log.audit.handler.names = syslog.log.audit

pci.logger.log.alert.level = ALL
pci.logger.log.alert.handler.names = syslog.log.alert


# Named PCI DSS handlers to communicate with SYSLOG service:
# NOTE: 
#  1. Each SYSLOG handler reports (to) a specific SYSLOG FACILITY
#  2. setup the application ID to the actual application pushing those messages

syslog.kernel.class = com.agafua.syslog.SyslogHandlerBean
syslog.log.alert.transport = UDP
syslog.log.audit.remoteHostName = localhost
syslog.log.audit.port = 514
syslog.user.applicationId = test-application
syslog.kernel.facility = KERN


syslog.user.class = com.agafua.syslog.SyslogHandlerBean
syslog.log.alert.transport = UDP
syslog.log.audit.remoteHostName = localhost
syslog.log.audit.port = 514
syslog.user.applicationId = test-application
syslog.user.facility = USER


syslog.auth.class = com.agafua.syslog.SyslogHandlerBean 
syslog.log.alert.transport = UDP
syslog.log.audit.remoteHostName = localhost
syslog.log.audit.port = 514
syslog.auth.applicationId = test-application
syslog.auth.facility = AUTH


syslog.security.class = com.agafua.syslog.SyslogHandlerBean
syslog.log.alert.transport = UDP
syslog.log.audit.remoteHostName = localhost
syslog.log.audit.port = 514
syslog.security.applicationId = test-application
syslog.security.facility = SECURITY


syslog.log.audit.class = com.agafua.syslog.SyslogHandlerBean
syslog.log.alert.transport = UDP
syslog.log.audit.remoteHostName = localhost
syslog.log.audit.port = 514
syslog.log.audit.applicationId = test-application
syslog.log.audit.facility = LOGAUDIT

syslog.log.alert.class = com.agafua.syslog.SyslogHandlerBean
syslog.log.alert.transport = UDP
syslog.log.audit.remoteHostName = localhost
syslog.log.audit.port = 514
syslog.user.applicationId = test-application
syslog.log.alert.facility = LOGALERT
# syslog.log.alert.rfc = RFC3164


#
# 2. Use the specific loggers in the Java application to report specific events to, by using their pure names:
#    Example:
#
#    Logger PCI_LOGGER_USER = Logger.getLogger("pci.logger.user");
#
#      and log to them  the standard way:
#
#    PCI_LOGGER_USER.log(...);
