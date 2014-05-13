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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;

import net.ifao.pci.logging.syslog.SyslogConfiguration;

/**
 * Message for sending by worker implementation.
 */
public class MessageRFC5424 extends AbstractMessage {

	public MessageRFC5424(SyslogConfiguration configuration, LogRecord record, String messageId) {	  
		super(configuration, record, messageId);
		
    print( calculatePriority( configuration ) ); // ABNF RFC5424: PRI
    print("2"); // TODO ABNF RFC5424: VERSION
    print(" "); // ABNF RFC5424: SP
    
    print(getTimestamp()); // ABNF RFC5424: TIMESTAMP
    
    print(" "); // ABNF RFC5424: SP
    
    print(configuration.getLocalHostName()); // ABNF RFC5424: HOSTNAME
    
    print(" "); // ABNF RFC5424: SP
    
    print(configuration.getApplicationId()); // ABNF RFC5424: APP-NAME
    
    print(" "); // ABNF RFC5424: SP
    
    print(configuration.getProcessId()); // ABNF RFC5424: PROCID
    
    print(" "); // ABNF RFC5424: SP
    
    print(getMessageId()); // ABNF RFC5424: MSGID
    
    print(" "); // ABNF RFC5424: SP
    
    print(getMessage());    
		
	}
	
	/**
	 * TODO REVIEW THE LOGIC
	 * @see com.agafua.syslog.sender.AbstractMessage#getTimestamp()
	 */
  public String getTimestamp() {
    String result;
    long millis = getLogRecord().getMillis();
    Date logDate = new Date(millis);

    String ts = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(logDate);
    
    result = ts.replaceAll("(\\d\\d)(\\d\\d)$", "$1:$2");
    
    return result;
  }	
}