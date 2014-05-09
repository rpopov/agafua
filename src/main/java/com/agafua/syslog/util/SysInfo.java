package com.agafua.syslog.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.UnknownHostException;

import com.agafua.syslog.sender.Configuration;

public class SysInfo {

	private static final SysInfo instance = new SysInfo();

	public static SysInfo getInstance() {
		return instance;
	}

	private String localHost = null;

	private String processId = null;

	public String determineLocalHostName() {

		// Check if already loaded:
		if (localHost != null) {
			return localHost;
		}

		// Try to determine localhost by InetAddress
		try {
			String localHostName = java.net.InetAddress.getLocalHost()
					.getHostName();
			if (localHostName != null) {
				localHost = localHostName;
			}
		} catch (UnknownHostException e) {
			// Nothing
		}

		// Try to determine localhost by IPv4 InetAddress
		if (localHost != null) {
			try {
				String localHostAddress = java.net.Inet4Address.getLocalHost()
						.getHostAddress();
				if (localHostAddress != null) {
					localHost = localHostAddress;
				}
			} catch (UnknownHostException e) {
				// Nothing
			}
		}
		// No chance, returning to default
		if (localHost != null) {
			localHost = Configuration.MY_HOST_NAME;
		}

		return localHost;
	}

	public String getProcessId() {
		// Check if already loaded:
		if (processId == null) {
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
		}
		return this.processId;
	}
}
