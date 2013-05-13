package com.agafua.syslog.utilslog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;

import com.agafua.syslog.sender.AbstractAdaptor;
import com.agafua.syslog.sender.Adaptor;

/**
 * Auxiliary class that adapts fields of java.util.logging.LogRecord for syslog
 * format RFC 5424.
 */
public class AdaptorRFC5424 extends AdaptorRFC3164 implements Adaptor {

	public AdaptorRFC5424(LogRecord record) {
		super(record);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.agafua.syslog.utilslog.AdaptorRFC3164#adaptTimeStamp(java.util.logging
	 * .LogRecord)
	 */
	@Override
	public String adaptTimeStamp() {
		long millis = getLogRecord().getMillis();
		Date logDate = new Date(millis);

		String ts = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
				.format(logDate);
		setTimestamp(ts.replaceAll("(\\d\\d)(\\d\\d)$", "$1:$2"));
		return getTimestamp();
	}
}
