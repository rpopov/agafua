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

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import com.agafua.syslog.sender.Adaptor;
import com.agafua.syslog.sender.Configuration;
import com.agafua.syslog.sender.Facility;
import com.agafua.syslog.sender.MessageRFC5424;
import com.agafua.syslog.sender.Connector;
import com.agafua.syslog.sender.SyslogConnector;
import com.agafua.syslog.sender.SyslogRfc;
import com.agafua.syslog.sender.Transport;
import com.agafua.syslog.util.SysInfo;
import com.agafua.syslog.util.Validator;
import com.agafua.syslog.utilslog.AdaptorRFC5424;

/**
 * Implementation of java.util.logging.Handler for syslog protocol RFC 3164.
 */
public class SyslogHandler extends Handler {

	private final Connector connect;

	SysInfo facts = SysInfo.getInstance();
	//
	// private String processId;
	// private final String applicationId;
	// private final String localHostName;
	// private final String remoteHostName;
	// private final int maxMessageSize;
	//
	// private final int port;
	// private final Facility facility;
	// private final Transport transport;

	// private BlockingQueue<Message> blockingQueue = new
	// ArrayBlockingQueue<Message>(
	// Config.LOG_QUEUE_SIZE);
	private boolean closed = false;

	private final Formatter formatter;

	public SyslogHandler() {
		super();
		Configuration config = new Configuration();

		config.setApplicationId(parseApplicationId());
		config.setFacility(parseFacility());
		config.setLocalHostName(parseLocalHostName());
		config.setMaxMessageSize(parseMaxMessageSize());
		config.setPort(parsePort());
		config.setProcessId(SysInfo.getInstance().getProcessId());
		config.setRemoteHostName(parseRemoteHostName());
		config.setSyslogRfc(SyslogRfc.RFC5424);
		config.setTransport(parseTransport());

		formatter = parseFormatter();

		connect = new SyslogConnector(config);

	}

	@Override
	public void publish(LogRecord record) {
		if (closed) {
			return;
		}

		String msg = formatter.format(record);
		AdaptorRFC5424 a = new AdaptorRFC5424(record);
		a.setMessage(msg);
		a.adaptSeverity();
		a.adaptTimeStamp();
		a.setMessageId(getMessageId());
		MessageRFC5424 m = new MessageRFC5424(a);

		connect.publish(m);

	}

	private String getMessageId() {

		return "-";
	}

	@Override
	public void flush() {
		// Does nothing because sending is asynchronous
	}

	@Override
	public void close() throws SecurityException {
		connect.close();
	}

	private Formatter parseFormatter() {

		Formatter formatter = new SimpleFormatter();
		String formatterProperty = SyslogHandler.class.getName() + "."
				+ Configuration.FORMATTER;
		String formatterValue = LogManager.getLogManager().getProperty(
				formatterProperty);
		if (formatterValue != null) {
			try {
				Class<? extends Formatter> c2 = Class.forName(formatterValue)
						.asSubclass(Formatter.class);
				formatter = c2.newInstance();
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException e) {
				// TODO Log ERROR
				System.err
						.println("Could not initialize java.util.logging Formatter class.");
				e.printStackTrace();
			}
		}

		return formatter;
	}

	private Transport parseTransport() {
		String transportProperty = SyslogHandler.class.getName() + "."
				+ Configuration.TRANSPORT_PROPERTY;
		String transportValue = LogManager.getLogManager().getProperty(
				transportProperty);
		for (Transport t : Transport.values()) {
			if (t.name().equalsIgnoreCase(transportValue)) {
				return t;
			}
		}
		return Transport.UDP;
	}

	private String parseApplicationId() {
		String appIdProperty = SyslogHandler.class.getName() + "."
				+ Configuration.APPLICATION_ID;
		String appIdValue = LogManager.getLogManager().getProperty(
				appIdProperty);
		if (appIdValue != null && appIdValue.length() > 0) {
			// The RFC allows max. 48 chars for the app id, so cut it on demand
			appIdValue = Validator.replaceNonUsAsciiAndTrim(appIdValue, 48);
			return appIdValue;
		}

		return "-";
	}

	private String parseRemoteHostName() {
		String hostNameProperty = SyslogHandler.class.getName() + "."
				+ Configuration.REMOTE_HOSTNAME_PROPERTY;
		String hostNameValue = LogManager.getLogManager().getProperty(
				hostNameProperty);
		if (hostNameValue != null && hostNameValue.length() > 0) {
			hostNameValue = Validator.replaceNonUsAsciiAndTrim(hostNameValue,
					255);
			return hostNameValue;
		}
		return Configuration.LOCALHOST;
	}

	private int parseMaxMessageSize() {
		String maxMsgSizeProperty = SyslogHandler.class.getName() + "."
				+ Configuration.MAX_MESSAGE_SIZE_PROPERTY;
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
		return Configuration.DEFAULT_MAX_MESSAGE_SIZE;
	}

	private int parsePort() {
		String portProperty = SyslogHandler.class.getName() + "."
				+ Configuration.PORT_PROPERTY;
		String portValue = LogManager.getLogManager().getProperty(portProperty);
		if (portValue != null) {
			Integer p = null;
			try {
				p = Integer.parseInt(portValue);
			} catch (NumberFormatException e) {

			}
			if (p != null && p >= Configuration.MIN_PORT
					&& p < Configuration.MAX_PORT) {
				return p;
			}
		}
		return Configuration.DEFAULT_PORT;
	}

	private Facility parseFacility() {
		String facilityProperty = SyslogHandler.class.getName() + "."
				+ Configuration.FACILITY_PROPERTY;
		String facilityValue = LogManager.getLogManager().getProperty(
				facilityProperty);
		for (Facility f : Facility.values()) {
			if (f.name().equalsIgnoreCase(facilityValue)) {
				return f;
			}
		}
		return Facility.USER;
	}

	private String parseLocalHostName() {

		String localHostProperty = SyslogHandler.class.getName() + "."
				+ Configuration.FACILITY_PROPERTY;
		String localHostValue = LogManager.getLogManager().getProperty(
				localHostProperty);

		if (localHostValue != null && localHostValue.length() > 0) {
			localHostValue = Validator.replaceNonUsAsciiAndTrim(localHostValue,
					255);
			return localHostValue;
		} else {
			return facts.determineLocalHostName();
		}
	}

}
