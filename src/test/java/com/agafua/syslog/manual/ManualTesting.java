package com.agafua.syslog.manual;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.print.attribute.standard.Severity;

import com.agafua.syslog.SyslogHandler;
import com.agafua.syslog.sender.Configuration;

public class ManualTesting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LogManager lM = LogManager.getLogManager();

		InputStream is;
		try {
			is = new FileInputStream(
					"src/test/resources/commons-logging.properties");
			lM.readConfiguration(is);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		System.out.println("Sending to : "
				+ lM.getProperty("com.agafua.syslog.SyslogHandler.remoteHostname"));

		Logger l = Logger.getLogger(ManualTesting.class.getName());

		Logger l2 = Logger.getLogger("ANOTHER_LOGGER");

		// l.addHandler(sH);

		l.info("######### \n # Linebreaktest \n#########");
		l.info("Hello World!");

		final int maxNumbers = 100;
		StringBuilder smiley = new StringBuilder(maxNumbers + " numbers: ");
		for (int i = 0; i < maxNumbers; i++) {
			smiley.append(i + ",");
		}
		l.warning(smiley.toString());

		l.info("Sonderzeichencheck: ä");

		ManualTesting mt = new ManualTesting();
		try {
			mt.iteration(0);
		} catch (Exception e) {
			l.log(Level.WARNING, "A planned exeption occurred.", e);
		}
	}

	private int iteration(int iteration) throws Exception {
		final int maxIters = 10;
		if (iteration > maxIters) {
			throw new Exception("This is a dummy exception!");
		}
		return iteration2(++iteration);

	}

	private int iteration2(int iteration) throws Exception {
		return iteration(++iteration);
	}

}
