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

package com.agafua.syslog.sender;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Implementation of java.util.logging.Handler for syslog protocol RFC 3164 and
 * RFC 5424
 */
public class SyslogConnector implements Connector {

	public static final int LOG_QUEUE_SIZE = 1024;

	private final Configuration config;

	private BlockingQueue<Message> blockingQueue = new ArrayBlockingQueue<Message>(
			LOG_QUEUE_SIZE);
	private boolean closed = false;
	private Thread worker;

	public SyslogConnector(Configuration config) {
		super();
		this.config = config;

		if (Transport.TCP.equals(config.getTransport())) {
			worker = new Thread(new TcpSender(getRemoteHostName(), getPort(),
					blockingQueue));
		} else {
			worker = new Thread(new UdpSender(getRemoteHostName(), getPort(),
					blockingQueue));
		}

		worker.start();
	}

	private String getRemoteHostName() {
		return config.getRemoteHostName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.agafua.syslog.SyslogConnect#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(Message message) {
		if (closed) {
			return;
		}

		try {

			if (config.getSyslogRfc() == null) {
				throw new IllegalArgumentException(
						"Syslog RFC undefined. Please select RFC3164 or RFC5424."
								+ config.getSyslogRfc());
			}
			switch (config.getSyslogRfc()) {
			case RFC3164:
				publishRFC3164(message);
				break;
			case RFC5424:
				publishRFC5424(message);
				break;

			default:
				throw new IllegalArgumentException("Unknown enumeration: "
						+ config.getSyslogRfc());
			}
		} catch (Throwable t) {
			// Not nice! TODO: REMOVE OR CHECK ALTERNATIVES!
			System.err.println("Error publishing log message.");
			t.printStackTrace();
		}

	}

	private void publishRFC3164(Message message) {

	}

	private void publishRFC5424(Message message) {
		String pri = calculatePriority(message);

		message.print(pri); // ABNF RFC5424: PRI
		message.print("2"); // TODO ABNF RFC5424: VERSION
		message.print(" "); // ABNF RFC5424: SP
		String timestamp = message.getTimestamp();
		message.print(timestamp); // ABNF RFC5424: TIMESTAMP
		message.print(" "); // ABNF RFC5424: SP
		String host = getApplicationName();
		message.print(host); // ABNF RFC5424: HOSTNAME
		message.print(" "); // ABNF RFC5424: SP
		String app = getApplicationName();
		message.print(app); // ABNF RFC5424: APP-NAME
		message.print(" "); // ABNF RFC5424: SP
		String procId = getProcessId();
		message.print(procId); // ABNF RFC5424: PROCID
		message.print(" "); // ABNF RFC5424: SP
		String msgId = message.getMessageId();
		message.print(msgId); // ABNF RFC5424: MSGID
		message.print(" "); // ABNF RFC5424: SP
		String msg = message.getMessage();
		message.print(msg);

		System.out.println(message);// TODO: REMOVE

		blockingQueue.offer(message);

	}

	public String calculatePriority(Message message) {
		int code = (getFacility().getId() << 3)
				+ message.getSeverity().getLevel();
		return String.format("<%d>", code);

	}

	private String getProcessId() {
		if (config.getProcessId() == null) {
			// lazy loading - do this only once:

			try {
				// This might not work on any operating system.
				// We return "-" of not successful.
				RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();

				String jvmName = bean.getName();
				String pid = jvmName.split("@")[0];

				if (pid != null && pid.length() > 0) {
					config.setProcessId(pid);
				} else {
					config.setProcessId("-");
				}

			} catch (Exception e) {
				// we're very carefully here...
				config.setProcessId("-");
			}
			config.setProcessId(replaceNonUsAsciiAndTrim(config.getProcessId(),
					128));
		}

		return config.getProcessId();
	}

	private String getApplicationName() {
		return config.getApplicationId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.agafua.syslog.SyslogConnect#close()
	 */
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
		return config.getTransport().name();
	}

	public int getPort() {
		return config.getPort();
	}

	public Facility getFacility() {
		return config.getFacility();
	}

	public int getMaxMessageSize() {
		return config.getMaxMessageSize();
	}

	private String replaceNonUsAsciiAndTrim(String s, int maxLength) {
		s = s.substring(0, Math.min(s.length(), maxLength));

		// Only ASCII-7 chars are allowed. So replace all others:
		return s.replaceAll("[\\x80-\\xFF]", ".");
	}

}
