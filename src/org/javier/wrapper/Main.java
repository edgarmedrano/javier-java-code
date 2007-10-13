/**
 * File:        Main.java
 * Description: Javier AGI Service Wrapper
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.10.02
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.wrapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.javier.agi.AGIScript;
import org.javier.agi.AGIService;
import org.tanukisoftware.wrapper.WrapperManager;
import org.tanukisoftware.wrapper.WrapperListener;
                    
public class Main
    implements WrapperListener
{
    private AGIService m_app;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    private Main()
    {
    }

    /*---------------------------------------------------------------
     * WrapperListener Methods
     *-------------------------------------------------------------*/
    /**
     * The start method is called when the WrapperManager is signaled by the 
     *	native wrapper code that it can start its application.  This
     *	method call is expected to return, so a new thread should be launched
     *	if necessary.
     *
     * @param args List of arguments used to initialize the application.
     *
     * @return Any error code if the application should exit on completion
     *         of the start method.  If there were no problems then this
     *         method should return null.
     */
    public Integer start( String[] args )
    {
		String handlerClass = AGIScript.class.getName();
		String bindAddress = "";
		int port = 4573;
		int poolSize = 24;
		
	    try {
		    Properties properties = new Properties();
		    
	        properties.load(new FileInputStream("AGIService.properties"));
		    handlerClass = properties.getProperty("handler_class", handlerClass);
	    	bindAddress = properties.getProperty("bind_address", bindAddress);
		    
		    if(properties.containsKey("port")) {
				port = Integer.parseInt(properties.getProperty("port"));
		    }
		    
		    if(properties.containsKey("pool_size")) {
				poolSize = Integer.parseInt(properties.getProperty("poolSize", String.valueOf(poolSize)));
		    }
	    } catch (IOException e) {
	    	
	    }
		
		try {
			m_app = new AGIService(handlerClass,port,poolSize,bindAddress);
			m_app.run();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
    	
        return null;
    }

    /**
	 * Called when the application is shutting down. The Wrapper assumes that
	 * this method will return fairly quickly. If the shutdown code code could
	 * potentially take a long time, then WrapperManager.signalStopping() should
	 * be called to extend the timeout period. If for some reason, the stop
	 * method can not return, then it must call WrapperManager.stopped() to
	 * avoid warning messages from the Wrapper.
	 * 
	 * @param exitCode
	 *            The suggested exit code that will be returned to the OS when
	 *            the JVM exits.
	 * 
	 * @return The exit code to actually return to the OS. In most cases, this
	 *         should just be the value of exitCode, however the user code has
	 *         the option of changing the exit code if there are any problems
	 *         during shutdown.
	 */
    public int stop( int exitCode )
    {
        m_app.shutdown();
        
        return exitCode;
    }
    
    /**
	 * Called whenever the native wrapper code traps a system control signal
	 * against the Java process. It is up to the callback to take any actions
	 * necessary. Possible values are: WrapperManager.WRAPPER_CTRL_C_EVENT,
	 * WRAPPER_CTRL_CLOSE_EVENT, WRAPPER_CTRL_LOGOFF_EVENT, or
	 * WRAPPER_CTRL_SHUTDOWN_EVENT
	 * 
	 * @param event
	 *            The system control signal.
	 */
    public void controlEvent( int event )
    {
        if (WrapperManager.isControlledByNativeWrapper()) {
            // The Wrapper will take care of this event
        } else {
            // We are not being controlled by the Wrapper, so
            //  handle the event ourselves.
            if ((event == WrapperManager.WRAPPER_CTRL_C_EVENT) ||
                (event == WrapperManager.WRAPPER_CTRL_CLOSE_EVENT) ||
                (event == WrapperManager.WRAPPER_CTRL_SHUTDOWN_EVENT)){
                WrapperManager.stop(0);
            }
        }
    }
    
    /**
	 * The main method.
	 * 
	 * @param args
	 *            the args
	 */
    public static void main( String[] args )
    {
        // Start the application.  If the JVM was launched from the native
        //  Wrapper then the application will wait for the native Wrapper to
        //  call the application's start method.  Otherwise the start method
        //  will be called immediately.
        WrapperManager.start( new Main(), args );
    }
}
