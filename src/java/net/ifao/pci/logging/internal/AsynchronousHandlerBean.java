/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 09.05.2014
 */
package net.ifao.pci.logging.internal;

import java.util.logging.Formatter;

import com.agafua.syslog.sender.Configuration;

public class AsynchronousHandlerBean<C extends Configuration> extends AsynchronousHandler<C> {

  public AsynchronousHandlerBean(C configuration) {
    super( configuration );
  }

  /**
   * Use bean-style initialization 
   * @param formatterClassName
   */
  public final void setFormatter(String formatterClassName) throws IllegalArgumentException {
    Formatter formatter;
    Class<? extends Formatter> formatterClass;
    
    if ( formatterClassName != null && !formatterClassName.isEmpty()) {
      try {
        formatterClass = Class.forName( formatterClassName ).asSubclass( Formatter.class );        
        formatter = formatterClass.newInstance();
        
        getConfiguration().setFormatter( formatter );
      } catch (Exception ex) {
        throw new IllegalArgumentException("Could not initialize java.util.logging Formatter class: "+formatterClassName, ex);
      }
    }
  }

  /**
   * Use bean-style initialization
   */
  public final void setApplicationId(String  applicationId) {
    if ( applicationId != null && !applicationId.trim().isEmpty() ) {
      getConfiguration().setApplicationId( applicationId.trim() );	    
    }
  }

  public final void setPort(String port) {
    if ( port != null && !port.isEmpty()) {
      try {
        getConfiguration().setPort( Integer.parseInt( port ) );
      } catch (Exception ex) {
        throw new IllegalArgumentException("Setting port to: "+port+" caused:", ex);
      }
    }
  }

  public final void setHost(String hostName) {
    if ( hostName != null && !hostName.isEmpty() ) {
      getConfiguration().setHost( hostName );
    }
  }
}
