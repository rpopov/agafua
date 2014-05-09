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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Implementation of java.util.logging.Handler for syslog protocol RFC 3164 and
 * RFC 5424
 */
public class SyslogConnector implements Connector {

	private static final int LOG_QUEUE_SIZE = 1024;

	private final Configuration config;
	private final BlockingQueue<Message> blockingQueue = new ArrayBlockingQueue<Message>(LOG_QUEUE_SIZE);
	
	private boolean closed;
	private Thread worker;

	public SyslogConnector(Configuration config) {
		super();
		this.config = config;

  	worker = config.constructWorkerThread( blockingQueue );  	
		worker.start();
	}

  /*
	 * (non-Javadoc)
	 * 
	 * @see com.agafua.syslog.SyslogConnect#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(Message message) {
		if (!closed) {
  		try {
  			switch (config.getSyslogRfc()) {
    			case RFC3164:
    				publishRFC3164(message);
    				break;
    			case RFC5424:
    				publishRFC5424(message);
    				break;
  			}
  		} catch (Throwable t) {
  			// Not nice! TODO: REMOVE OR CHECK ALTERNATIVES!
  			System.err.println("Error publishing log message.");
  			t.printStackTrace();
  		}
		}
	}

	private void publishRFC3164(Message message) {

	}

	private void publishRFC5424(Message message) {
		String pri = calculatePriority(message);

		message.print(pri); // ABNF RFC5424: PRI
		message.print("2"); // TODO ABNF RFC5424: VERSION
		message.print(" "); // ABNF RFC5424: SP
		
		message.print(message.getTimestamp()); // ABNF RFC5424: TIMESTAMP
		
		message.print(" "); // ABNF RFC5424: SP
		
		message.print(config.getLocalHostName()); // ABNF RFC5424: HOSTNAME
		
		message.print(" "); // ABNF RFC5424: SP
		
		message.print(config.getApplicationId()); // ABNF RFC5424: APP-NAME
		
		message.print(" "); // ABNF RFC5424: SP
		
		message.print(config.getProcessId()); // ABNF RFC5424: PROCID
		
		message.print(" "); // ABNF RFC5424: SP
		
		message.print(message.getMessageId()); // ABNF RFC5424: MSGID
		
		message.print(" "); // ABNF RFC5424: SP
		
		message.print(message.getMessage());

		System.out.println(message);// TODO: REMOVE

		blockingQueue.offer(message);
	}

	public String calculatePriority(Message message) {
		int code = (config.getFacility().getId() << 3)
				       + message.getSeverity().getLevel();
		return String.format("<%d>", code);

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
}