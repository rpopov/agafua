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

package com.agafua.syslog.log4j;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Level;
import org.apache.log4j.pattern.LogEvent;

import com.agafua.syslog.sender.AbstractAdaptor;
import com.agafua.syslog.sender.Adaptor;
import com.agafua.syslog.sender.Severity;

/**
 * Auxiliary class that adapts fields of java.util.logging.LogRecord for syslog
 * format RFC 3164.
 */
public class AdaptorRFC3164 extends AbstractAdaptor implements Adaptor {

	private final LogEvent logEvent;

	protected LogEvent getLogRecord() {
		return logEvent;
	}

	public AdaptorRFC3164(LogEvent record) {
		logEvent = record;
	}

	private static final String[] MONTH_NAMES = { "Jan", "Feb", "Mar", "Apr",
			"May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	public Severity adaptSeverity() {
		Level level = logEvent.getLevel();
		switch (level.toInt()) {
		case Level.TRACE_INT:
			setSeverity(Severity.DEBUG);
			break;
		case Level.DEBUG_INT:
			setSeverity(Severity.INFO);
			break;
		case Level.INFO_INT:
			setSeverity(Severity.NOTICE);
			break;
		case Level.WARN_INT:
			setSeverity(Severity.WARNING);
			break;
		case Level.ERROR_INT:
			setSeverity(Severity.ERROR);
			break;
		case Level.FATAL_INT:
			setSeverity(Severity.CRITICAL);

		default:
			// TODO
			System.err.println("Cant parse level: " + level);
			setSeverity(Severity.DEBUG);
			break;
		}

		return getSeverity();
	}

	public String adaptTimeStamp() {
		long millis = logEvent.getTimeStamp();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(millis);
		int month = limit(calendar.get(Calendar.MONTH), 0, 11);
		String mmm = MONTH_NAMES[month];
		int day = limit(calendar.get(Calendar.DAY_OF_MONTH), 1, 31);
		String dd = indent("" + day, 2, ' ');
		int hour = limit(calendar.get(Calendar.HOUR_OF_DAY), 0, 23);
		String hh = indent("" + hour, 2, '0');
		int minute = limit(calendar.get(Calendar.MINUTE), 0, 59);
		String mm = indent("" + minute, 2, '0');
		int second = limit(calendar.get(Calendar.SECOND), 0, 59);
		String ss = indent("" + second, 2, '0');
		return String.format("%s %s %s:%s:%s", mmm, dd, hh, mm, ss);
	}

	private static String indent(String s, int requiredLength, char identChar) {
		while (s.length() < requiredLength) {
			s = identChar + s;
		}
		return s;
	}

	private static int limit(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

}
