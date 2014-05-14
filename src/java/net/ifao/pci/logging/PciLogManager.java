/*
 * Copyright (c) i:FAO AG 2014. All Rights Reserved.
 *
 * This SOURCE CODE FILE is provided under Eclipse Public License 1.0
 * 
 * Created on 09.05.2014
 */
package net.ifao.pci.logging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Allow defining and referring handlers by name, instead of by class.
 * <pre>
 * Extended syntax of logging.properties:
 * &lt;logger class&gt;.handler.names = comma or space-separated names of handlers
 * 
 * &lt;handler name&gt;.class = qualified name of the handler's class
 *                              In the specific case of SYSLOG logger: net.ifao.syslog.logger.SyslogHandlerBean
 * &lt;handler name&gt;.level = log level
 * &lt;handler name&gt;.&lt;property name&gt; = any string value
 *   calls the &lt;handler class&gt;.set&lt;property name&gt;(String) with that value
 *   
 * The &lt;handler class&gt;.set&lt;property name&gt;(String) methods should accept <ul>
 * <li> null and empty values, in case no value is provided in the logging.properties
 * <li> string representation of the actual values to set (like numbers, enums, etc.)
 *      Recommended is the values' recognition to be case insensitive 
 * </ul> 
 * Usage:
 *   add in the java command line add the parameter
 *   <b>-Djava.util.logging.manager=net.ifao.pci.logging.PciLogManager</b>
 * </pre>  
 * @see net.ifao.pci.logging.syslog.SyslogHandlerBean
 * @author rpopov
 */
public class PciLogManager extends LogManager {

  /**
   * Custom suffix start
   */
  private static final String SUFFIX_START = ".";
  private static final String SUFFIX_NAMED_HANLERS = SUFFIX_START+"handler.names";
  private static final String SUFFIX_CLASS = SUFFIX_START+"class";
  
  /**
   * This is a standard suffix, it does not need custom suffix start
   */
  private static final String SUFFIX_LEVEL = ".level";
  
  private static final String REGEX_NAMED_HALDERS_SEPARATOR = "(, *)|( +)";
  
  /**
   * A copy of the configuration, just ot allow custom handler properties
   * @see #readConfiguration(InputStream)
   */
  private final Properties properties = new Properties();
  
  
  /**
   * @see java.util.logging.LogManager#addLogger(java.util.logging.Logger)
   */
  public boolean addLogger(Logger logger) {
    boolean result;
    result = super.addLogger( logger );
    
    if ( result ) { // this is a new logger - configure its handlers
      parseNamedHandlers( logger );
    }
    return result;
  }

  /**
   * Parse, instantiate and bind the named handlers for that logger
   * @param logger not null
   */
  private void parseNamedHandlers(Logger logger) {
    String namedHandlers;
    String[] handlerNames;
    Handler handler;
    
    namedHandlers = getProperty( logger.getName()+SUFFIX_NAMED_HANLERS );
    if ( namedHandlers != null ) {
      handlerNames = namedHandlers.split( REGEX_NAMED_HALDERS_SEPARATOR );
      
      for (String name : handlerNames) {
        if ( !name.isEmpty()  ) {
          try {
            handler = parseHandler( name );            
            logger.addHandler( handler );            
          } catch (Exception ex) { // report and suppress initialization failures
            ex.printStackTrace();
          }
        }
      }
    }    
  }

  /**
   * Parse and instantiate the named handler as of the new syntax
   * @param name not empty
   * @return not null handler
   */
  private Handler parseHandler(String name) throws IllegalArgumentException {
    Handler result;
    String className;   
    String levelName;   
    String classPropertyName;
    String levelPropertyName;
    String handlersPropertyName;
    String anyPropertyPrefix;
    Enumeration<String> keysEnumeration;
    String key;
    String value;
    
    Class<Handler> handlerClass;
    
    // parse the class and instantiate
    classPropertyName = name+SUFFIX_CLASS;
    className = getProperty( classPropertyName );
    if ( className == null || className.trim().isEmpty() ) {
      throw new IllegalArgumentException("Missing property "+classPropertyName);
    }
    try {
      handlerClass = (Class<Handler>) Class.forName( className.trim() );
      
      result = handlerClass.newInstance();
    } catch (Exception ex) {
      throw new IllegalArgumentException("Instantiating "+className+" caused:", ex);
    } // result != null
    
    // parse the log level for the handler
    levelPropertyName = name+SUFFIX_LEVEL;
    levelName = getProperty( levelPropertyName );
    if ( levelName != null  && !levelName.trim().isEmpty()) {
      try {
        result.setLevel( Level.parse( levelName.trim() ) );        
        
      } catch (Exception ex) {
        throw new IllegalArgumentException("Parsing "+levelPropertyName+" = "+ levelName+" caused:", ex);
      }
    }
    
    // parse any other property
    anyPropertyPrefix = name+SUFFIX_START;
    handlersPropertyName = name + SUFFIX_NAMED_HANLERS; // prevent using SUFFIX_NAMED_HANLERS as property name when the logger's and handler's names are equal
    
    keysEnumeration = (Enumeration<String>) properties.propertyNames();
    while ( keysEnumeration.hasMoreElements() ) {
      key = keysEnumeration.nextElement();
      
      if ( key.startsWith( anyPropertyPrefix )
           && key.length() > anyPropertyPrefix.length() // not empty property
           && !key.equals( classPropertyName )
           && !key.equals( levelPropertyName )
           && !key.equals( handlersPropertyName )) { // this is <handler name>.<property>
        
        value =  getProperty( key );
        if ( value != null && !value.trim().isEmpty() ) {
          setHandlerProperty( result, 
                              key.substring( anyPropertyPrefix.length() ),
                              value.trim());
        }
      }      
    }
    return result;
  }

  /**
   * Reflectively set the handler's property to the provided value
   * @param handler not null
   * @param propertyName not null, not empty
   * @param value could be null
   */
  private void setHandlerProperty(Handler handler, 
                                  String propertyName, 
                                  String value) throws IllegalArgumentException {
    Method setter;
    try {
      setter = handler.getClass().getMethod( "set"+Character.toUpperCase( propertyName.charAt( 0 ) )+propertyName.substring( 1 ), 
                                             String.class );
      setter.invoke( handler, value );
      
    } catch (NoSuchMethodException ex) {
      System.err.println( handler.getClass()+" does not have '"+propertyName+"' property");
      
    } catch (InvocationTargetException ex) {
      System.err.print("Setting '"+propertyName+"' property of "+handler+" to value: '"+value+"' caused:");
      ex.getCause().printStackTrace();
      
    } catch (Exception ex) {
      System.err.print("Setting '"+propertyName+"' property of "+handler+" to value: '"+value+"' caused:");
      ex.printStackTrace(); 
    }
  }

  /**
   * Because of the stupid overall design of java.util.logging.LogManager and specifically not allowing 
   * to access the property names, we capture the properties in a separate instance of the configuration.  
   * @param in
   * @throws IOException
   * @throws SecurityException
   * @see java.util.logging.LogManager#readConfiguration(java.io.InputStream)
   */
  public void readConfiguration(InputStream in) throws IOException, SecurityException {
    ByteArrayOutputStream buffer;
    ByteArrayInputStream readBuffer;
    int read;
    byte[] buff = new byte[16384];
    
    buffer = new ByteArrayOutputStream( 16384 );
    try {
      // clone the in into a buffer and read it twice
      while ((read = in.read( buff )) != -1) {
        buffer.write(buff,0, read);
      }
      readBuffer = new ByteArrayInputStream( buffer.toByteArray() );
      readBuffer.mark( 0 );
      
      // read the configuration twice
      super.readConfiguration( readBuffer );
      
      readBuffer.reset();
      properties.load( readBuffer );
    } finally {
      buffer.close();
    }
  }

  /**
   * @see #readConfiguration(InputStream)
   * @see java.util.logging.LogManager#reset()
   */
  public void reset() throws SecurityException {
    super.reset();
    
    properties.clear(); // it is synchronized
  }  
}