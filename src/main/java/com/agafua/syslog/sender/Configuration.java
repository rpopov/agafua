package com.agafua.syslog.sender;

/**
 * All configuration parameter for the Syslog connectivity.
 * 
 */
public class Configuration {

	// Default recommendations:
	public static final String VERSION = "02";
	public static final String LOCALHOST = "localhost";
	public static final String MY_HOST_NAME = "0.0.0.0";
	public static final int DEFAULT_PORT = 514;
	public static final int MIN_PORT = 0;
	public static final int MAX_PORT = 65535;
	public static final int DEFAULT_MAX_MESSAGE_SIZE = 65535;
	public static final SyslogRfc SYSLOG_VERSION = SyslogRfc.RFC5424;
	public static final Facility FACILITY = Facility.LOCAL7;

	// Property names:
	public static final String TRANSPORT_PROPERTY = "transport";
	public static final String LOCAL_HOSTNAME_PROPERTY = "localHostname";
	public static final String REMOTE_HOSTNAME_PROPERTY = "remoteHostname";
	public static final String PORT_PROPERTY = "port";
	public static final String FACILITY_PROPERTY = "facility";
	public static final String APPLICATION_ID = "applicationId";
	public static final String MAX_MESSAGE_SIZE_PROPERTY = "maxMsgSize";
	public static final String FORMATTER = "formatter";

	// Configuration values:
	private Integer port = null;
	private SyslogRfc syslogRfc = null;
	private String processId = null;
	private String applicationId = null;
	private String localHostName = null;
	private String remoteHostName = null;
	private Integer maxMessageSize = null;
	private Transport transport = null;
	private Facility facility = null;

	// Lots of getters and setters:
	// ////////////////

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public void setMaxMessageSize(Integer maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public SyslogRfc getSyslogRfc() {
		return syslogRfc;
	}

	public void setSyslogRfc(SyslogRfc syslogVersion) {
		this.syslogRfc = syslogVersion;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getLocalHostName() {
		return localHostName;
	}

	public void setLocalHostName(String localHostName) {
		this.localHostName = localHostName;
	}

	public String getRemoteHostName() {
		return remoteHostName;
	}

	public void setRemoteHostName(String remoteHostName) {
		this.remoteHostName = remoteHostName;
	}

	public Integer getMaxMessageSize() {
		return maxMessageSize;
	}

	public void setMaxMessageSize(int maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

}
