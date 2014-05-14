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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.LogRecord;

import net.ifao.pci.logging.internal.NetworkSender;
import net.ifao.pci.logging.syslog.SyslogConfiguration;

/**
 * Worker for sending of syslog messages by UDP transport.
 */
class UdpSender extends NetworkSender<SyslogConfiguration> implements Runnable {

  private DatagramSocket socket;
  private InetAddress address;

  public UdpSender(Connector<SyslogConfiguration> connector) {
    super( connector );
  }

  /**
   * @see net.ifao.pci.logging.internal.NetworkSender#establishConnection()
   */
  protected void establishConnection() throws IOException {
    if ( socket == null ) {
      address = InetAddress.getByName( getConfiguration().getHost() );
      socket = new DatagramSocket();
    }
  }


  /**
   * @see net.ifao.pci.logging.internal.NetworkSender#sendMessage(LogRecord)
   */
  protected void sendMessage(LogRecord record) throws IOException {
    DatagramPacket packet;
    Message message;
    
    message = constructMessage( record );  
    
    packet = new DatagramPacket( message.getBytes(), 
                                 message.getLength(), 
                                 address, 
                                 getConfiguration().getPort() );
    socket.send( packet );
  }


  /**
   * @see net.ifao.pci.logging.internal.NetworkSender#releaseResources()
   */
  protected void releaseResources() {
    if ( socket != null ) {
      try {
        socket.close();
      } catch (Throwable ex) {
      } 
      socket = null;
    }
  }
  

  /**
   * @see net.ifao.pci.logging.internal.NetworkSender#describeConnection()
   */
  protected String describeConnection() {
    return "UDP connection to host:"+getConfiguration().getHost()+" and port:"+getConfiguration().getPort();
  }  

  /**
   * @param record
   * @return
   */
  private Message constructMessage(LogRecord record) {
    return getConfiguration().constructMessage( record, "-" );
  }


  /**
   * @see net.ifao.pci.logging.internal.NetworkSender#formatPrintable(java.util.logging.LogRecord)
   */
  protected String formatPrintable(LogRecord record) {
    return constructMessage( record ).toString();
  }
}
