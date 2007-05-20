/**
 * File:        AGIConnection.java
 * Description: FastAGI connection to Asterisk
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.05.19
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:       The classes included in this package implement AGI 
 *              communication and are based in agi.py implementation
 *              from http://sourceforge.net/projects/pyst/ 
 */
package org.javier.agi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class encapsulates communication between Asterisk and Javascript. It
 * handles encoding commands to Asterisk and parsing responses from Asterisk.
 */
public class AGIConnection {
	
	/**
	 * The Asterisk command's result.
	 */
	protected class Result extends Hashtable<String, String[]> {
		
		private static final long serialVersionUID = -3272685660490686408L;

		public Result() {
			put("result", new String[2]);
		}
		
		public Result(int i) {
			this();
			get("result")[0] = String.valueOf(i);
		}

		public Result(int i, String string) {
			this(i);
			get("result")[1] = string;			
		}
	}
	
	/** Regular expresion to extract the command's resulting code. */
	static protected final Pattern re_code = Pattern.compile("(^\\d*)\\s*(.*)");
	
	/** Regular expresion to extract the detailed command's resulting code. */
	static protected final Pattern re_kv = Pattern.compile("(\\w+)=([^\\s]+)\\s*(?:\\((.*)\\))*");
	
	/** The default timeout. */
	static protected final long DEFAULT_TIMEOUT = 6000;
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the args
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
	static public void main(String args[]) throws AGIException {
		AGIConnection agi = new AGIConnection(
			System.in,
			System.out,
			System.err
			);
		
		agi.stream_file("auth-thankyou");
		agi.hangup();
	}
	
	/** The err. */
	protected PrintWriter err;
	
	/** The in. */
	protected BufferedReader in;
	
	/** The out. */
	protected OutputStream out;
	
	/** The properties. */
	protected Properties properties;
	
	/**
	 * @param err the error output stream
	 * @param in  the input stream
	 * @param out the output stream
	 * 
	 * @throws AGIException if a communication error take place
	 */
	public AGIConnection(InputStream in, PrintStream out, PrintStream err) throws AGIException {
		this(in,(OutputStream)out,(OutputStream)err);
	}
	
	/**
	 * @param err the error output stream
	 * @param in  the input stream
	 * @param out the output stream
	 * 
	 * @throws AGIException if a communication error take place
	 */
	public AGIConnection(InputStream in, OutputStream out, OutputStream err) throws AGIException {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = out;
		this.err = new PrintWriter(err);
		
		//_got_sighup = false;
		//signal.signal(signal.SIGHUP, _handle_sighup)  // handle SIGHUP
		properties = _get_agi_env();
	}
	
	/**
	 * Get the AGI environment variables.
	 * 
	 * @return the environment variables
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
	protected Properties _get_agi_env() throws AGIException {
		Properties result = new Properties();
		for(;;) {
			String line = readLine().trim();
			err.printf("ENV LINE: %s\n", line);
			if(line.equals("")) {
				// blank line signals end
				break;
			}
			String parts[] = line.split(":");
			String key = parts[0].trim();
			String data = parts.length > 1 ? parts[1].trim() : "";
			if(!key.equals("")) {
				result.put(key, data);
			}
		}
		err.printf("class AGI: env = %s\n", result.toString());
		
		return result;
	}

	/**
	 * _quote.
	 * 
	 * @param text
	 *            the text
	 * 
	 * @return the string
	 */
	protected String _quote(Object text) {
		return "\"" + text + "\"";
	}

	/**
	 * Answer channel if not already in answer state.
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
	public void answer() throws AGIException {
        execute("ANSWER");
	}
	
	/**
	 * Executes {@literal application} with given {@literal options}.
	 * 
	 * @param application
	 *            the application
	 * 
	 * @return whatever the application returns, or -2 on failure to find
	 *         application
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
	public String appexec(String application) throws AGIException {
    	return appexec(application, "");
    }
	
	/**
	 * Executes {@literal application} with given {@literal options}.
	 * 
	 * @param application
	 *            the application
	 * @param options
	 *            the options
	 * 
	 * @return whatever the application returns, or -2 on failure to find
	 *         application
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
	public String appexec(String application, String options) throws AGIException {
        String res = execute("EXEC", application, _quote(options)).get("result")[0];
        if(res.equals("-2")) {
            throw(new AGIAppError("Unable to find application: " + application));
		}
        return res;
	}
	
	/**
	 * Translates ASCII code to String.
	 * 
	 * @param code
	 *            the code to transalate
	 * 
	 * @return the string
	 */
	private String ascii(String code) {
		try {
			return new String(new byte[] { (byte)(int) Integer.valueOf(code) }, "UTF-8");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Returns the status of the specified channel. If no channel name is given
	 * the returns the status of the current channel.
	 * 
	 * @return the int
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 * 
	 * 0 when channel is down and available<br>
	 * 1 when channel is down, but reserved<br>
	 * 2 when channel is off hook<br>
	 * 3 when digits (or equivalent) have been dialed<br>
	 * 4 if line is ringing<br>
	 * 5 if remote end is ringing<br>
	 * 6 if line is up<br>
	 * 7 if line is busy<br>
	 */
	public int channel_status() throws AGIException {
    	return channel_status("");
    }

	/**
	 * Returns the status of the specified channel. If no channel name is given
	 * the returns the status of the current channel.
	 * 
	 * @param channel
	 *            the channel
	 * 
	 * @return the int
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 * 
	 * 0 when channel is down and available<br>
	 * 1 when channel is down, but reserved<br>
	 * 2 when channel is off hook<br>
	 * 3 when digits (or equivalent) have been dialed<br>
	 * 4 if line is ringing<br>
	 * 5 if remote end is ringing<br>
	 * 6 if line is up<br>
	 * 7 if line is busy<br>
	 */
	public int channel_status(String channel) throws AGIException {
		Result result;
		
        try {
           result = execute("CHANNEL STATUS", channel);
        } catch(AGIException e) {
			if(e instanceof AGIHangup) {
				throw(e);
			} else if(e instanceof AGIAppError) {
				result = new Result(-1);
			} else { 
				throw(e);
			}
		}

        return Integer.valueOf(result.get("result")[0]);
	}
	
	/**
	 * Send the given file.
	 * 
	 * @param filename
	 *            the filename
	 *            <em>(remember, the file extension must not be included in the filename.)</em>
	 * 
	 * @return ""
	 * 
	 * @throws AGIError
	 *             if the channel was disconnected.
	 * @throws AGIException
	 *             if a communication error take place
	 */
	public String control_stream_file(String filename) throws AGIException {
    	return control_stream_file(filename,"");
    }

	/**
	 * Send the given file, allowing playback to be interrupted by the given
	 * digits, if any.
	 * 
	 * @param filename
	 *            the filename
	 *            <em>(remember, the file extension must not be included in the filename.)</em>
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIError
	 *             if the channel was disconnected.
	 * @throws AGIException
	 *             if a communication error take place
	 */
	public String control_stream_file(String filename, 
    		String escape_digits) throws AGIException {
    	return control_stream_file(filename,escape_digits,3000);
    }	

    /**
	 * Send the given file, allowing playback to be interrupted by the given
	 * digits, if any.
	 * <p>
	 * If sample offset is provided then the audio will seek to sample offset
	 * before play starts.
	 * </p>
	 * 
	 * @param filename
	 *            the filename
	 *            <em>(remember, the file extension must not be included in the filename.)</em>
	 * @param skipms
	 *            the sample offset (in miliseconds) to skip before playing
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIError
	 *             if the channel was disconnected.
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String control_stream_file(String filename, 
    		String escape_digits, 
    		long skipms) throws AGIException {
    	return control_stream_file(filename,escape_digits,skipms,"");
    }

    /**
	 * Send the given file, allowing playback to be interrupted by the given
	 * digits, if any.
	 * <p>
	 * If sample offset is provided then the audio will seek to sample offset
	 * before play starts.
	 * </p>
	 * 
	 * @param filename
	 *            the filename
	 *            <em>(remember, the file extension must not be included in the filename.)</em>
	 * @param fwd
	 *            the forward digit
	 * @param skipms
	 *            the sample offset (in miliseconds) to skip before playing
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIError
	 *             if the channel was disconnected.
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String control_stream_file(String filename, 
    		String escape_digits, 
    		long skipms, 
    		String fwd) throws AGIException {
    	return control_stream_file(filename,escape_digits,skipms,fwd,"");
    }
    
    /**
	 * Send the given file, allowing playback to be interrupted by the given
	 * digits, if any.
	 * <p>
	 * If sample offset is provided then the audio will seek to sample offset
	 * before play starts.
	 * </p>
	 * 
	 * @param rew
	 *            the rewind digit
	 * @param filename
	 *            the filename
	 *            <em>(remember, the file extension must not be included in the filename.)</em>
	 * @param fwd
	 *            the forward digit
	 * @param skipms
	 *            the sample offset (in miliseconds) to skip before playing
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIError
	 *             if the channel was disconnected.
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String control_stream_file(String filename, 
    		String escape_digits, 
    		long skipms, 
    		String fwd, 
    		String rew) throws AGIException {
    	return control_stream_file(filename,escape_digits,skipms,fwd,rew,"");
    }
    
    /**
	 * Send the given file, allowing playback to be interrupted by the given
	 * digits, if any.
	 * <p>
	 * If sample offset is provided then the audio will seek to sample offset
	 * before play starts.
	 * </p>
	 * 
	 * @param pause
	 *            the pause digit
	 * @param rew
	 *            the rewind digit
	 * @param filename
	 *            the filename
	 *            <em>(remember, the file extension must not be included in the filename.)</em>
	 * @param fwd
	 *            the forward digit
	 * @param skipms
	 *            the sample offset (in miliseconds) to skip before playing
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIError
	 *             if the channel was disconnected.
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String control_stream_file(String filename, 
    		String escape_digits, 
    		long skipms, 
    		String fwd, 
    		String rew, 
    		String pause) throws AGIException, AGIError {
        escape_digits = _quote(escape_digits);
        String res = execute("CONTROL STREAM FILE", _quote(filename), escape_digits, _quote(skipms), _quote(fwd), _quote(rew), _quote(pause)).get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}

    /**
	 * Deletes an entry in the Asterisk database for a given family and key.
	 * 
	 * @param family
	 *            the family
	 * @param key
	 *            the key
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public void database_del(String family, String key) throws AGIException {
        Result result = execute("DATABASE DEL", _quote(family), _quote(key));
        String res = result.get("result")[0];
        if(res.equals("0")) {
            throw(new AGIDBError("Unable to delete from database: family=" + family + ", key=" + key));
		}
	}

    /**
	 * Deletes a family or specific keytree with in a family in the Asterisk
	 * database.
	 * 
	 * @param family
	 *            the family
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public void database_deltree(String family) throws AGIException {
    	database_deltree(family,"");
    }    
    
    /**
	 * Deletes a family or specific keytree with in a family in the Asterisk
	 * database.
	 * 
	 * @param family
	 *            the family
	 * @param key
	 *            the key
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public void database_deltree(String family, String key) throws AGIException {
        Result result = execute("DATABASE DELTREE", _quote(family), _quote(key));
        String res = result.get("result")[0];
        if(res.equals("0")) {
            throw(new AGIDBError("Unable to delete tree from database: family=" + family + ", key=" + key));
		}
	}			

    /**
	 * Retrieves an entry in the Asterisk database for a given family and key.
	 * Returns
	 * 
	 * @param family
	 *            the family
	 * @param key
	 *            the key
	 * 
	 * @return the entry value.
	 * 
	 * @throws AGIDBError
	 *             when entry is not set
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String database_get(String family, String key) throws AGIException {
        Result result = execute("DATABASE GET", _quote(family), _quote(key));
		String res = result.get("result")[0];
        String value = result.get("result")[1];
        if(res.equals("0")) {
            throw(new AGIDBError("Key not found in database: family=" + family + ", key=" + key));
        } else if(res.equals("1")) {
            return value;
        } else {
            throw(new AGIError("Unknown exception for : family=" + family + ", key=" + key + ", result=" + result));
		}
	}
    
    /**
	 * Adds or updates an entry in the Asterisk database for a given family,
	 * key, and value.
	 * 
	 * @param family
	 *            the family
	 * @param value
	 *            the value
	 * @param key
	 *            the key
	 * 
	 * @throws AGIDBError
	 *             when entry is not set
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public void database_put(String family, String key, Object value) throws AGIException {
        Result result = execute("DATABASE PUT", _quote(family), _quote(key), _quote(value));
        String res = result.get("result")[0];
        if(res.equals("0")) {
            throw(new AGIDBError("Unable to put value in database: family=" + family + ", key=" + key + ", value=" + value));
		}
	}
    
    /**
	 * Execute the specified AGI command using optional parameters.
	 * 
	 * @param args
	 *            the command's parameters
	 * @param command
	 *            the command
	 * 
	 * @return the command's result
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    protected synchronized Result execute(String command, Object ...args) throws AGIException {
		try {
			send_command(command,args);
			return get_result();
		} catch(AGIException e) {
			if(e.getNumber() == 32) {
				// Broken Pipe, let us go
				throw(new AGISIGPIPEHangup("Received SIGPIPE"));
			} else {
				throw(e);
			}
		}
	}
    
    /**
	 * Stream the given file and receive dialed digits.
	 * 
	 * @param filename
	 *            the filename <em>(remember, the file extension must
     * not be included in the filename.)</em>
	 * 
	 * @return a string containig the digits or "" if none is receipt
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String get_data(String filename) throws AGIException {
        return get_data(filename, DEFAULT_TIMEOUT);
    	
    }
            
    /**
	 * Stream the given file and receive dialed digits.
	 * 
	 * @param filename
	 *            the filename <em>(remember, the file extension must
     * not be included in the filename.)</em>
	 * @param timeout
	 *            the time to wait for digits
	 * 
	 * @return a string containig the digits or "" if none is receipt
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String get_data(String filename, long timeout) throws AGIException {
        return get_data(filename, timeout, 255);
    	
    }

    /**
	 * Stream the given file and receive dialed digits.
	 * 
	 * @param filename
	 *            the filename <em>(remember, the file extension must
     * not be included in the filename.)</em>
	 * @param max_digits
	 *            the maximum number of digits to get
	 * @param timeout
	 *            the time to wait for digits
	 * 
	 * @return a string containig the digits or "" if none is receipt
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String get_data(String filename, long timeout, int max_digits) throws AGIException {
        String res = execute("GET DATA", filename, timeout, max_digits).get("result")[0];
        return res;
	}

    /**
	 * Get a channel variable.
	 * 
	 * @param name
	 *            the variable's name
	 * 
	 * @return the value of the indicated channel variable or "" if the variable
	 *         is not set
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String get_full_variable(String name) throws AGIException {
    	return get_full_variable(name, "");
    }

    /**
	 * Get a channel variable.
	 * 
	 * @param name
	 *            the variable's name
	 * @param channel
	 *            the channel
	 * 
	 * @return the value of the indicated channel variable or "" if the variable
	 *         is not set
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String get_full_variable(String name, String channel) throws AGIException {
		Result result;
        try {
            if(!channel.equals("")) {
                result = execute("GET FULL VARIABLE", _quote(name), _quote(channel));
            } else {
	            result = execute("GET FULL VARIABLE", _quote(name));
			}
        } catch(AGIException e) {
			if(e instanceof AGIResultHangup) {
				result = new Result(1, "hangup");
			} else { 
				throw(e);
			}
		}

        String value = result.get("result")[1];
        return value;
	}

    /**
	 * Send the given file, allowing playback to be interrupted by the given
	 * digits, if any.
	 * 
	 * @param filename
	 *            the filename
	 *            <em>(remember, the file extension must not be included in the filename.)</em>
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIError
	 *             if the channel was disconnected.
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String get_option(String filename) throws AGIException {
    	return get_option(filename,"");
    }    

    /**
	 * Send the given file, allowing playback to be interrupted by the given
	 * digits, if any.
	 * 
	 * @param filename
	 *            the filename
	 *            <em>(remember, the file extension must not be included in the filename.)</em>
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIError
	 *             if the channel was disconnected.
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String get_option(String filename, String escape_digits) throws AGIException {
    	return get_option(filename,escape_digits, 0);
    }    
    
    /**
	 * Send the given file, allowing playback to be interrupted by the given
	 * digits, if any.
	 * 
	 * @param filename
	 *            the filename
	 *            <em>(remember, the file extension must not be included in the filename.)</em>
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * @param timeout
	 *            the time to wait for digits
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIError
	 *             if the channel was disconnected.
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String get_option(String filename, String escape_digits, long timeout) throws AGIException {
        escape_digits = _quote(escape_digits);
		Result response;
        if(timeout != 0) {
            response = execute("GET OPTION", filename, escape_digits, timeout);
        } else {
            response = execute("GET OPTION", filename, escape_digits);
		}

        String res = response.get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}

    /**
	 * Read the result of a command from Asterisk.
	 * 
	 * @return the result
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    protected Result get_result() throws AGIException {
		int code = 0;
		Result result = new Result();
		String line = readLine().trim();
		String response = "";
		err.printf("    RESULT_LINE: %s\n", line);
		Matcher matcher = re_code.matcher(line);
		if(matcher.find()) {
			code = matcher.groupCount() >= 1 ? Integer.valueOf(matcher.group(1)) : 0;
			response = matcher.groupCount() >= 2 ? matcher.group(2) : "";
		}
		
		if(code == 200) {
			matcher = re_kv.matcher(response);
			String key = null;
			String value = null;
			String data = null;
			
			if(matcher.find()) {
				key = matcher.groupCount() >= 1 ? matcher.group(1) : null;
				value = matcher.groupCount() >= 2 ? matcher.group(2) : null;
				data = matcher.groupCount() >= 3 ? matcher.group(3) : null;
			}
			
			key = key == null? "" : key;
			value = value == null? "" : value;
			data = data == null? "" : data;
			
			if(!key.equals("")) {
				result.put(key, new String[] {value, data});
			}
			
			// If user hangs up... we get "hangup" in the data
			if(data.equals("hangup")) {
				throw(new AGIResultHangup("User hungup during execution"));
			}

			if(key.equals("result") && value.equals("-1")) {
				throw(new AGIAppError("Error executing application, or hangup"));
			}

			err.printf("    RESULT_DICT: %s\n", result);
			return result;
		} else if(code == 510) {
			throw(new AGIInvalidCommand(response));
		} else if(code == 520) {
			StringBuilder usage = new StringBuilder(line);
			line = readLine().trim();
			while(!line.substring(0,3).equals("520")) {
				usage.append(line);
				usage.append("\n");
				line = readLine().trim();
			}
			usage.append(line);
			usage.append("\n");
			throw(new AGIUsageError(usage.toString()));
		} else {
			throw(new AGIUnknownError(code, "Unhandled code or undefined response"));
		}
	}
    
    /**
	 * Get a channel variable.
	 * 
	 * @param name
	 *            the variable's name
	 * 
	 * @return the value of the indicated channel variable or "" if the variable
	 *         is not set
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String get_variable(String name) throws AGIException {
		Result result;
        try {
           result = execute("GET VARIABLE", _quote(name));
        } catch(AGIException e) {
			if(e instanceof AGIResultHangup) {
				result = new Result(1, "hangup");
			} else { 
				throw(e);
			}
		}

        String value = result.get("result")[1];
        return value;
	}

    /**
	 * This is not an Asterisk command, is a shortand method to specify what to
	 * do after the AGI script is executed.
	 * 
	 * @param extension
	 *            the extension
	 * @param context
	 *            the context
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public void goto_on_exit(String context, int extension) throws AGIException {
    	goto_on_exit(context,extension,
    			Integer.valueOf(properties.getProperty("agi_priority")));
    }
    
    /**
	 * This is not an Asterisk command, is a shortand method to specify what to
	 * do after the AGI script is executed.
	 * 
	 * @param extension
	 *            the extension
	 * @param priority
	 *            the priority
	 * @param context
	 *            the context
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public void goto_on_exit(String context, int extension, int priority) throws AGIException {
        set_context(context);
        set_extension(extension);
        set_priority(priority);
	}

    /**
	 * Hangs up the current channel.
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public void hangup() throws AGIException {
    	hangup("");
    }
    
    /**
	 * Hangs up the specified channel.
	 * 
	 * @param channel
	 *            the channel
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public void hangup(String channel) throws AGIException {
        execute("HANGUP", channel);
	}

    /**
	 * Joins an array of objects into a string including the specified separator
	 * between them.
	 * 
	 * @param args
	 *            the objects to be joined
	 * @param separator
	 *            the separator
	 * 
	 * @return the string
	 */
    private String join(Object[] args, String separator) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < args.length; i++) {
			if(i > 0) {
				sb.append(separator);
			}
			sb.append(args[i]);
		}
		return sb.toString();
	}
    
    /**
	 * Performs no operation.
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public void noop() throws AGIException {
        execute("NOOP");
	}

    /**
	 * Read line from {@link #in}.
	 * 
	 * @return the line read
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    private String readLine() throws AGIException {
		try {
			return in.readLine();
		} catch (IOException e) {
			throw(new AGIUnknownError());
		}
	}
    
    /**
	 * Receives a character of text on a channel.
	 * <p>
	 * <strong>WARNING:</strong><br>
	 * <em>Most channels do not support the reception of text.</em>
	 * </p>
	 * 
	 * @return the text
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String receive_char() throws AGIException {
    	return receive_char(DEFAULT_TIMEOUT);
    }

    /**
	 * Receives a character of text on a channel.
	 * <p>
	 * <strong>WARNING:</strong><br>
	 * <em>Most channels do not support the reception of text.</em>
	 * </p>
	 * 
	 * @param timeout
	 *            maximum time to wait for input in milliseconds, or 0 for
	 *            infinite
	 * 
	 * @return the text
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String receive_char(long timeout) throws AGIException {
        String res = execute("RECEIVE CHAR", timeout).get("result")[0];

        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}
    
    /**
	 * Record to a file until a given DTMF digit in the sequence is received.
	 * 
	 * @param filename
	 *            the filename
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String record_file(String filename) throws AGIException {
    	return record_file(filename,"gsm");    	    	    	
    }

    /**
	 * Record to a file until a given DTMF digit in the sequence is received.
	 * 
	 * @param filename
	 *            the filename
	 * @param format
	 *            specify what kind of file will be recorded.
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String record_file(String filename, String format) throws AGIException {
    	return record_file(filename,format,"");    	    	
    }
    
    /**
	 * Record to a file until a given DTMF digit in the sequence is received.
	 * 
	 * @param filename
	 *            the filename
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * @param format
	 *            specify what kind of file will be recorded.
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String record_file(String filename, String format, String escape_digits) throws AGIException {
    	return record_file(filename,format,escape_digits,DEFAULT_TIMEOUT);    	
    }
    
    /**
	 * Record to a file until a given DTMF digit in the sequence is received.
	 * 
	 * @param filename
	 *            the filename
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * @param format
	 *            specify what kind of file will be recorded.
	 * @param timeout
	 *            the maximum record time in milliseconds, or -1 for no timeout.
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String record_file(String filename, String format, String escape_digits, long timeout) throws AGIException {
    	return record_file(filename,format,escape_digits,timeout,0);    	
    }
    
    /**
	 * Record to a file until a given DTMF digit in the sequence is received.
	 * 
	 * @param filename
	 *            the filename
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * @param format
	 *            specify what kind of file will be recorded.
	 * @param offset
	 *            seek to the offset without exceeding the end of the file
	 * @param timeout
	 *            the maximum record time in milliseconds, or -1 for no timeout.
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String record_file(String filename, String format, String escape_digits, long timeout, long offset) throws AGIException {
    	return record_file(filename,format,escape_digits,timeout,offset,"beep");
    }
    
    /**
	 * Record to a file until a given DTMF digit in the sequence is received.
	 * 
	 * @param filename
	 *            the filename
	 * @param escape_digits
	 *            a string containing the digits used to stop playing
	 * @param format
	 *            specify what kind of file will be recorded.
	 * @param beep
	 *            the sound file played before recording
	 * @param offset
	 *            seek to the offset without exceeding the end of the file
	 * @param timeout
	 *            the maximum record time in milliseconds, or -1 for no timeout.
	 * 
	 * @return the digit if one was pressed
	 * 
	 * @throws AGIException
	 *             if a communication error take place
	 */
    public String record_file(String filename, String format, String escape_digits, long timeout, long offset, String beep) throws AGIException {
        escape_digits = _quote(escape_digits);
        String res = execute("RECORD FILE", _quote(filename), format, escape_digits, timeout, offset, beep).get("result")[0];
        try {
            return ascii(res);
        } catch(Exception e) {
            throw(new AGIError("Unable to convert result to digit: " + res));
		}
	}

    /**
	 * Say_alpha.
	 * 
	 * @param characters
	 *            the characters
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_alpha(String characters) throws AGIException {
    	return say_alpha(characters,"");
    }

    /**
	 * Say_alpha.
	 * 
	 * @param escape_digits
	 *            the escape_digits
	 * @param characters
	 *            the characters
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_alpha(String characters, String escape_digits) throws AGIException {
        /* agi.say_alpha(string, escape_digits="") --> digit
        Say a given character string, returning early if any of the given DTMF
        digits are received on the channel.  
        Throws AGIError on channel failure
        */
        characters = _quote(characters);
        escape_digits = _quote(escape_digits);
        String res = execute("SAY ALPHA", characters, escape_digits).get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}
    
    /**
	 * Say_date.
	 * 
	 * @param seconds
	 *            the seconds
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_date(long seconds) throws AGIException {
    	return say_date(seconds,"");
    }
    
    /**
	 * Say_date.
	 * 
	 * @param seconds
	 *            the seconds
	 * @param escape_digits
	 *            the escape_digits
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_date(long seconds, String escape_digits) throws AGIException {
        /* agi.say_date(seconds, escape_digits="") --> digit
        Say a given date, returning early if any of the given DTMF digits are
        pressed.  The date should be in seconds since the UNIX Epoch (Jan 1, 1970 00:00:00)
        */
        escape_digits = _quote(escape_digits);
        String res = execute("SAY DATE", seconds, escape_digits).get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}
    
    /**
	 * Say_datetime.
	 * 
	 * @param seconds
	 *            the seconds
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_datetime(long seconds) throws AGIException {
    	return say_datetime(seconds, "");
    }
    
    /**
	 * Say_datetime.
	 * 
	 * @param seconds
	 *            the seconds
	 * @param escape_digits
	 *            the escape_digits
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_datetime(long seconds, String escape_digits) throws AGIException {
    	return say_datetime(seconds, escape_digits, "");
    }

    /**
	 * Say_datetime.
	 * 
	 * @param seconds
	 *            the seconds
	 * @param escape_digits
	 *            the escape_digits
	 * @param format
	 *            the format
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_datetime(long seconds, String escape_digits, String format) throws AGIException {
    	return say_datetime(seconds, escape_digits, format, "");
    }

    /**
	 * Say_datetime.
	 * 
	 * @param seconds
	 *            the seconds
	 * @param escape_digits
	 *            the escape_digits
	 * @param format
	 *            the format
	 * @param zone
	 *            the zone
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_datetime(long seconds, String escape_digits, String format, String zone) throws AGIException {
        /* agi.say_datetime(seconds, escape_digits="", format="", zone="") --> digit
        Say a given date in the format specfied (see voicemail.conf), returning
        early if any of the given DTMF digits are pressed.  The date should be
        in seconds since the UNIX Epoch (Jan 1, 1970 00:00:00).
        */
        escape_digits = _quote(escape_digits);
        if(!format.equals("")) { format = _quote(format); }
        String res = execute("SAY DATETIME", seconds, escape_digits, format, zone).get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}

    /**
	 * Say_digits.
	 * 
	 * @param digits
	 *            the digits
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_digits(String digits) throws AGIException {
    	return say_digits(digits,"");    	
    }

    /**
	 * Say_digits.
	 * 
	 * @param digits
	 *            the digits
	 * @param escape_digits
	 *            the escape_digits
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_digits(String digits, String escape_digits) throws AGIException {
        /* agi.say_digits(digits, escape_digits="") --> digit
        Say a given digit string, returning early if any of the given DTMF digits
        are received on the channel.  
        Throws AGIError on channel failure
        */
        digits = _quote(digits);
        escape_digits = _quote(escape_digits);
        String res = execute("SAY DIGITS", digits, escape_digits).get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}

    /**
	 * Say_number.
	 * 
	 * @param number
	 *            the number
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_number(String number) throws AGIException {
    	return say_number(number,"");
    }
    
    /**
	 * Say_number.
	 * 
	 * @param number
	 *            the number
	 * @param escape_digits
	 *            the escape_digits
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_number(String number, String escape_digits) throws AGIException {
        /* agi.say_number(number, escape_digits="") --> digit
        Say a given digit string, returning early if any of the given DTMF digits
        are received on the channel.  
        Throws AGIError on channel failure
        */
        number = _quote(number);
        escape_digits = _quote(escape_digits);
        String res = execute("SAY NUMBER", number, escape_digits).get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}
    
    /**
	 * Say_phonetic.
	 * 
	 * @param characters
	 *            the characters
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_phonetic(String characters) throws AGIException {
    	return say_phonetic(characters,"");
    }
    
    /**
	 * Say_phonetic.
	 * 
	 * @param escape_digits
	 *            the escape_digits
	 * @param characters
	 *            the characters
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_phonetic(String characters, String escape_digits) throws AGIException {
        /* agi.say_phonetic(string, escape_digits="") --> digit
        Phonetically say a given character string, returning early if any of
        the given DTMF digits are received on the channel.  
        Throws AGIError on channel failure
        */
        characters = _quote(characters);
        escape_digits = _quote(escape_digits);
        String res = execute("SAY PHONETIC", characters, escape_digits).get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}
    
    /**
	 * Say_time.
	 * 
	 * @param seconds
	 *            the seconds
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_time(long seconds) throws AGIException {
    	return say_time(seconds,"");
    }
    
    /**
	 * Say_time.
	 * 
	 * @param seconds
	 *            the seconds
	 * @param escape_digits
	 *            the escape_digits
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String say_time(long seconds, String escape_digits) throws AGIException {
        /* agi.say_time(seconds, escape_digits="") --> digit
        Say a given time, returning early if any of the given DTMF digits are
        pressed.  The time should be in seconds since the UNIX Epoch (Jan 1, 1970 00:00:00)
        */
        escape_digits = _quote(escape_digits);
        String res = execute("SAY TIME", seconds, escape_digits).get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}

    /**
	 * Send_command.
	 * 
	 * @param args
	 *            the args
	 * @param command
	 *            the command
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    protected void send_command(String command, Object args[]) throws AGIException {
		/* Send a command to Asterisk */
		command = command.trim();
		command += " " + join(args, " ");
		command = command.trim();
		if(command.charAt(command.length() - 1) != '\n') {
			command += "\n";
		}
		
		err.printf("    COMMAND: %s\n", command);
		write(command);
	}

    /**
	 * Send_image.
	 * 
	 * @param filename
	 *            the filename
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void send_image(String filename) throws AGIException {
        /* agi.send_image(filename) --> None
        Sends the given image on a channel.  Most channels do not support the
        transmission of images.   Image names should not include extensions.
        Throws AGIError on channel failure
        */
        String res = execute("SEND IMAGE", filename).get("result")[0];
        if(!res.equals("0")) {
            throw(new AGIAppError("Channel falure on channel " + properties.getProperty("agi_channel","UNKNOWN")));
		}
	}
    
    /**
	 * Send_text.
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void send_text() throws AGIException {
    	send_text("");
    }

    /**
	 * Send_text.
	 * 
	 * @param text
	 *            the text
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void send_text(String text) throws AGIException {
        /* agi.send_text(text="") --> None
        Sends the given text on a channel.  Most channels do not support the
        transmission of text.
        Throws AGIError on error/hangup
        */
        execute("SEND TEXT", _quote(text));
	}

    /**
	 * Set_autohangup.
	 * 
	 * @param secs
	 *            the _autohangup
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void set_autohangup(long secs) throws AGIException {
        /* agi.set_autohangup(secs) --> None
        Cause the channel to automatically hangup at <time> seconds in the
        future.  Of course it can be hungup before then as well.   Setting to
        0 will cause the autohangup feature to be disabled on this channel.
        */
        execute("SET AUTOHANGUP", secs);
	}

    /**
	 * Set_callerid.
	 * 
	 * @param number
	 *            the _callerid
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void set_callerid(String number) throws AGIException {
        /* agi.set_callerid(number) --> None
        Changes the callerid of the current channel.
        */
        execute("SET CALLERID", number);
	}

    /**
	 * Set_context.
	 * 
	 * @param context
	 *            the _context
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void set_context(String context) throws AGIException {
        /* agi.set_context(context)
        Sets the context for continuation upon exiting the application.
        No error appears to be produced.  Does not set exten or priority
        Use at your own risk.  Ensure that you specify a valid context.
        */
        execute("SET CONTEXT", context);
	}

    /**
	 * Set_extension.
	 * 
	 * @param extension
	 *            the _extension
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void set_extension(int extension) throws AGIException {
        /* agi.set_extension(extension)
        Sets the extension for continuation upon exiting the application.
        No error appears to be produced.  Does not set context or priority
        Use at your own risk.  Ensure that you specify a valid extension.
        */
        execute("SET EXTENSION", extension);
	}

    /**
	 * Set_priority.
	 * 
	 * @param priority
	 *            the _priority
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void set_priority(int priority) throws AGIException {
        /* agi.set_priority(priority)
        Sets the priority for continuation upon exiting the application.
        No error appears to be produced.  Does not set exten or context
        Use at your own risk.  Ensure that you specify a valid priority.
        */
        execute("set priority", priority);
	}

    /**
	 * Set_variable.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void set_variable(String name, String value) throws AGIException {
        /* Set a channel variable.
        */
        execute("SET VARIABLE", _quote(name), _quote(value));
	}

    /**
	 * Stream_file.
	 * 
	 * @param filename
	 *            the filename
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String stream_file(String filename) throws AGIException {
    	return stream_file(filename,"",0);
    }

    /**
	 * Stream_file.
	 * 
	 * @param filename
	 *            the filename
	 * @param escape_digits
	 *            the escape_digits
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String stream_file(String filename,
    		String escape_digits) throws AGIException {
    	return stream_file(filename,escape_digits,0);
    }

    /**
	 * Stream_file.
	 * 
	 * @param sample_offset
	 *            the sample_offset
	 * @param filename
	 *            the filename
	 * @param escape_digits
	 *            the escape_digits
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String stream_file(String filename,
    		String escape_digits, 
    		long sample_offset) throws AGIException {
        /* agi.stream_file(filename, escape_digits="", sample_offset=0) --> digit
        Send the given file, allowing playback to be interrupted by the given
        digits, if any.  escape_digits is a string "12345" or a list  of 
        ints [1,2,3,4,5] or strings ["1","2","3"] or mixed [1,"2",3,"4"]
        If sample offset is provided then the audio will seek to sample
        offset before play starts.  Returns  digit if one was pressed.
        Throws AGIError if the channel was disconnected.  Remember, the file
        extension must not be included in the filename.
        */
        escape_digits = _quote(escape_digits);
        String res = execute("STREAM FILE", filename, escape_digits, sample_offset).get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to char: " + res));
			}
		}
	}

    /**
	 * Tdd_mode.
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void tdd_mode() throws AGIException {
    	tdd_mode("off");
    }

    /**
	 * Tdd_mode.
	 * 
	 * @param mode
	 *            the mode
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void tdd_mode(String mode) throws AGIException {
        /* agi.tdd_mode(mode="on"|"off") --> None
        Enable/Disable TDD transmission/reception on a channel. 
        Throws AGIAppError if channel is not TDD-capable.
        */
        String res = execute("TDD MODE", mode).get("result")[0];
        if(res.equals("0")) {
            throw(new AGIAppError("Channel is not TDD-capable"));
		}
	}

    /**
	 * Verbose.
	 * 
	 * @param message
	 *            the message
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void verbose(String message) throws AGIException {
    	verbose(message,1);
    }
            
    /**
	 * Verbose.
	 * 
	 * @param message
	 *            the message
	 * @param level
	 *            the level
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public void verbose(String message, int level) throws AGIException {
        /* agi.verbose(message="", level=1) --> None
        Sends <message> to the console via verbose message 
        <level> is the the verbose level (1-4)
        */
        execute("VERBOSE", _quote(message), level);
	}

    /**
	 * Wait_for_digit.
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String wait_for_digit() throws AGIException {
    	return wait_for_digit(DEFAULT_TIMEOUT);
    }

    /**
	 * Wait_for_digit.
	 * 
	 * @param timeout
	 *            the timeout
	 * 
	 * @return the string
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    public String wait_for_digit(long timeout) throws AGIException {
        /* agi.wait_for_digit(timeout=DEFAULT_TIMEOUT) --> digit
        Waits for up to "timeout" milliseconds for a channel to receive a DTMF
        digit.  Returns digit dialed
        Throws AGIError on channel falure
        */
        String res = execute("WAIT FOR DIGIT", timeout).get("result")[0];
        if(res.equals("0")) {
            return "";
        } else {
            try {
                return ascii(res);
            } catch(Exception e) {
                throw(new AGIError("Unable to convert result to digit: " + res));
			}
		}
	}

    /**
	 * Write.
	 * 
	 * @param command
	 *            the command
	 * 
	 * @throws AGIException
	 *             the AGI exception
	 */
    private void write(String command) throws AGIException {
		try {
			out.write(command.getBytes("UTF-8"));
			out.flush();
		} catch (UnsupportedEncodingException e) {
			throw(new AGIUnknownError());
		} catch (IOException e) {
			throw(new AGIUnknownError());
		}
	}
}
