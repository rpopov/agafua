/*
Copyright (c) 2012 Vitaly Russinkovsky

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.agafua.syslog;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Implementation of java.util.logging.Handler for syslog protocol RFC 3164.
 */
public class SyslogHandler extends Handler {

	private static final int LOG_QUEUE_SIZE = 1024;

	private static final String VERSION = "02";

	private static final String LOCALHOST = "localhost";
	private static final String MY_HOST_NAME = "0.0.0.0";
	private static final int DEFAULT_PORT = 514;
	private static final int MIN_PORT = 0;
	private static final int MAX_PORT = 65535;

	private static final int DEFAULT_MAX_MESSAGE_SIZE = 65535;

	private static final String TRANSPORT_PROPERTY = "transport";
	private static final String LOCAL_HOSTNAME_PROPERTY = "localHostname";
	private static final String REMOTE_HOSTNAME_PROPERTY = "remoteHostname";
	private static final String PORT_PROPERTY = "port";
	private static final String FACILITY_PROPERTY = "facility";
	private static final String APPLICATION_ID = "applicationId";
	private static final String MAX_MESSAGE_SIZE_PROPERTY = "maxMsgSize";
	private static final String DAEMON_MODE_PROPERTY = "daemon";

	private String processId;
	private final String applicationId;
	private final String localHostName;
	private final String remoteHostName;
	private final int maxMessageSize;

	private final int port;
	private final Facility facility;
	private final Transport transport;

	private BlockingQueue<Message> blockingQueue = new ArrayBlockingQueue<Message>(
			LOG_QUEUE_SIZE);
	private boolean closed = false;
	private Thread worker;
	private volatile String myHostName;

	private Adaptor adaptor = new AdaptorRFC5424();

	public SyslogHandler() {
		super();
		transport = parseTransport();
		localHostName = parseLocalHostName();
		remoteHostName = parseRemoteHostName();
		applicationId = parseApplicationId();
		port = parsePort();
		facility = parseFacility();
		maxMessageSize = parseMaxMessageSize();
		setFormatter(new SimpleFormatter());
		if (Transport.TCP.equals(transport)) {
			worker = new Thread(new TcpSender(remoteHostName, port,
					blockingQueue));
		} else {
			worker = new Thread(new UdpSender(remoteHostName, port,
					blockingQueue));
		}
		worker.start();
	}

	@Override
	public void publish(LogRecord record) {
		if (closed) {
			return;
		}
		try {
			Message message = new MessageRFC5424(getMaxMessageSize());
			String pri = adaptor.adaptPriority(record, facility);
			message.print(pri); // ABNF RFC5424: PRI
			message.print(VERSION); // ABNF RFC5424: VERSION
			message.print(" "); // ABNF RFC5424: SP
			String timestamp = adaptor.adaptTimeStamp(record);
			message.print(timestamp); // ABNF RFC5424: TIMESTAMP
			message.print(" "); // ABNF RFC5424: SP
			String host = getLocalHostName();
			message.print(host); // ABNF RFC5424: HOSTNAME
			message.print(" "); // ABNF RFC5424: SP
			String app = getApplicationName();
			message.print(app); // ABNF RFC5424: APP-NAME
			message.print(" "); // ABNF RFC5424: SP
			String procId = getProcessId();
			message.print(procId); // ABNF RFC5424: PROCID
			message.print(" "); // ABNF RFC5424: SP
			String msgId = getMessageId();
			message.print(msgId); // ABNF RFC5424: MSGID
			message.print(" "); // ABNF RFC5424: SP
			String msg = getFormatter().format(record);
			message.print(msg);
			System.out.println(message);
			blockingQueue.offer(message);
		} catch (Throwable t) {
			// Not nice! TODO: REMOVE OR CHECK ALTERNATIVES!
			t.printStackTrace();
		}
	}

	private String getMessageId() {

		return "-";
	}

	private String getProcessId() {
		if (this.processId == null) {
			// lazy loading - do this only once:

			try {
				// This might not work on any operating system.
				// We return "-" of not successful.
				RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();

				String jvmName = bean.getName();
				String pid = jvmName.split("@")[0];

				if (pid != null && pid.length() > 0) {
					this.processId = pid;
				} else {
					this.processId = "-";
				}

			} catch (Exception e) {
				// we're very carefully here...
				this.processId = "-";
			}
			processId = replaceNonUsAsciiAndTrim(processId, 128);
		}

		return this.processId;
	}

	private String getApplicationName() {
		return this.applicationId;
	}

	public String getLocalHostName() {
		return localHostName;
	}

	@Override
	public void flush() {
		// Does nothing because sending is asynchronous
	}

	@Override
	public void close() throws SecurityException {
		blockingQueue.clear();
		if (worker != null) {
			worker.interrupt();
		}
		worker = null;
		closed = true;
	}

	public String getTransport() {
		return transport.name();
	}

	public int getPort() {
		return port;
	}

	public String getFacility() {
		return facility.name();
	}

	public int getMaxMessageSize() {
		return maxMessageSize;
	}

	private Transport parseTransport() {
		String transportProperty = SyslogHandler.class.getName() + "."
				+ TRANSPORT_PROPERTY;
		String transportValue = LogManager.getLogManager().getProperty(
				transportProperty);
		for (Transport t : Transport.values()) {
			if (t.name().equalsIgnoreCase(transportValue)) {
				return t;
			}
		}
		return Transport.UDP;
	}

	private String parseLocalHostName() {
		String hostNameProperty = SyslogHandler.class.getName() + "."
				+ LOCAL_HOSTNAME_PROPERTY;
		String hostNameValue = LogManager.getLogManager().getProperty(
				hostNameProperty);
		if (hostNameValue != null && hostNameValue.length() > 0) {
			return hostNameValue;
		}

		return getMyHostName();
	}

	private String parseApplicationId() {
		String appIdProperty = SyslogHandler.class.getName() + "."
				+ APPLICATION_ID;
		String appIdValue = LogManager.getLogManager().getProperty(
				appIdProperty);
		if (appIdValue != null && appIdValue.length() > 0) {
			// The RFC allows max. 48 chars for the app id, so cut it on demand
			appIdValue = replaceNonUsAsciiAndTrim(appIdValue, 48);
			return appIdValue;
		}

		return "-";
	}

	private String replaceNonUsAsciiAndTrim(String s, int maxLength) {
		s = s.substring(0, Math.min(s.length(), maxLength));

		// Only ASCII-7 chars are allowed. So replace all others:
		return s.replaceAll("[\\x80-\\xFF]", ".");
	}

	private String parseRemoteHostName() {
		String hostNameProperty = SyslogHandler.class.getName() + "."
				+ REMOTE_HOSTNAME_PROPERTY;
		String hostNameValue = LogManager.getLogManager().getProperty(
				hostNameProperty);
		if (hostNameValue != null && hostNameValue.length() > 0) {
			hostNameValue = replaceNonUsAsciiAndTrim(hostNameValue, 255);
			return hostNameValue;
		}
		return LOCALHOST;
	}

	private int parseMaxMessageSize() {
		String maxMsgSizeProperty = SyslogHandler.class.getName() + "."
				+ MAX_MESSAGE_SIZE_PROPERTY;
		String maxMsgSize = LogManager.getLogManager().getProperty(
				maxMsgSizeProperty);
		if (maxMsgSize != null) {
			Integer p = null;
			try {
				p = Integer.parseInt(maxMsgSizeProperty);
			} catch (NumberFormatException e) {

			}
			if (p != null) {
				return p;
			}
		}
		return DEFAULT_MAX_MESSAGE_SIZE;
	}

	private int parsePort() {
		String portProperty = SyslogHandler.class.getName() + "."
				+ PORT_PROPERTY;
		String portValue = LogManager.getLogManager().getProperty(portProperty);
		if (portValue != null) {
			Integer p = null;
			try {
				p = Integer.parseInt(portValue);
			} catch (NumberFormatException e) {

			}
			if (p != null && p >= MIN_PORT && p < MAX_PORT) {
				return p;
			}
		}
		return DEFAULT_PORT;
	}

	private Facility parseFacility() {
		String facilityProperty = SyslogHandler.class.getName() + "."
				+ FACILITY_PROPERTY;
		String facilityValue = LogManager.getLogManager().getProperty(
				facilityProperty);
		for (Facility f : Facility.values()) {
			if (f.name().equalsIgnoreCase(facilityValue)) {
				return f;
			}
		}
		return Facility.USER;
	}

	private String getMyHostName() {
		if (myHostName != null) {
			return myHostName;
		}
		try {
			String localHostName = java.net.InetAddress.getLocalHost()
					.getHostName();
			if (localHostName != null) {
				myHostName = localHostName;
			}
		} catch (UnknownHostException e) {
			// Nothing
		}
		if (myHostName != null) {
			try {
				String localHostAddress = java.net.Inet4Address.getLocalHost()
						.getHostAddress();
				if (localHostAddress != null) {
					myHostName = localHostAddress;
				}
			} catch (UnknownHostException e) {
				// Nothing
			}
		}
		if (myHostName != null) {
			myHostName = MY_HOST_NAME;
		}
		myHostName = replaceNonUsAsciiAndTrim(myHostName, 255);
		return myHostName;
	}
}
