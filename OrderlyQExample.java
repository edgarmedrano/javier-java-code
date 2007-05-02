/**
 * 
 * OrderlyQ - Advanced, Caller-Friendly queue control.
 * 
 * Copyright (C) 2004-2005, Matt King, M.A. Oxon. <m@orderlysoftware.com>
 * 
 * All rights reserved.
 *  
 * This file is NOT released under the LGPL.
 * 
 * You MAY NOT modify it, or use compiled classes from this file for
 * anything other than demonstration purposes.  It is freely redistributable, 
 * however, and you are welcome to use code excerpts in your own code where
 * appropriate. 
 * 
 * This file is included for instructional purposes ONLY.
 * 
 * The full version of OrderlyQ is available from http://www.orderlyq.com 
 *  
 */

package com.orderlysoftware.orderlycalls.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.orderlysoftware.orderlycalls.asterisk.agi.AGIConnection;
import com.orderlysoftware.orderlycalls.asterisk.agi.AGIReusableProcessor;
import com.orderlysoftware.orderlycalls.asterisk.agi.AGIServer;
import com.orderlysoftware.orderlycalls.asterisk.agi.AGISettings;

/**
 * OrderlyQ - Advanced, Caller Friendly Queue Control
 * <p>
 * 50% of all callers hang up after just 45 seconds of waiting on hold. (Source:
 * Avaya)
 * <p>
 * OrderlyQ is a revolutionary call queuing system that lets your callers hang
 * up and call back without losing their place in the queue.
 * <p>
 * This example application of OrderlyQ makes all callers wait for five
 * mintues. The full version is available from Orderly Software
 * (http://www.orderlyq.com), and does not have this limitation.
 * <p>
 * This class is an Example, for Demonstration purposes ONLY, and IS NOT distributed under the terms of the GNU Lesser General Public
 * License.
 * <p>
 * You MAY NOT use this file or compiled classes derived from this file for
 * anything other than your own personal demonstration purposes.   It is freely 
 * redistributable, however, and you are welcome to use code excerpts in your own code where
 * appropriate. 
 * <p>
 * Please see the accompanying documentation for usage restrictions with
 * OrderlyCalls and queuing.
 *  
 */
public class OrderlyQExample implements AGIReusableProcessor {

	//Inner classes.
	class Caller {
		//Represents a caller.
		
		//The first number we gather for the caller.
		String id;

		//Any other numbers we gather.
		ArrayList alternates=new ArrayList();
		
		//When the caller joined the queue.
		java.util.Date joined;

		//When the caller will reach the front of the queue.
		java.util.Date ready;

		//When the caller will be kicked out of the queue.
		java.util.Date kick;
		
		//Do we have a number for this caller?
		boolean noNumber = false;
		
		//Has this caller requested a text?
		boolean hasText = false;
		
		//Is this caller using a mobile phone?
		boolean isMobile=false;
		
		//Remove this caller from the queue.
		void remove() {
			Iterator i=alternates.iterator();
			while(i.hasNext()) { 
				Object key=i.next();
				log.info("Removing "+key);
				callers.remove(key);
			}
			log.info("Removing id "+id);
			callers.remove(id);
		}
	}
	
	//Represents a please-press menu of actions.
	class Meanings {
		String meanings[]=new String[12];
	
		void put(int i, String meaning) {
			meanings[i]=meaning;
		}
		
		String get(int i) {
			return meanings[i];
		}
		
		void insert(int i, String meaning) {
			int lim=i+1;
			for(int j=meanings.length; j>lim; j--) {
				meanings[j]=meanings[j-1];			
			}
			meanings[i]=meaning;
		}
		
		void append(String meaning) {
			append(1, meaning);
		}
		
		String getDigits() {
			StringBuffer digits=new StringBuffer();
			for(int i=0; i<10; i++) {
				if(meanings[i]!=null)
					digits.append(i);
			}
			if(meanings[10]!=null)
				digits.append('*');
			if(meanings[11]!=null)
				digits.append('#');
			return digits.toString();
		}
		
		void append(int begin, String meaning) {
			int i=begin;
			int lim=meanings.length;
			while(i<lim && meanings[i]!=null) {
				i++;
			}
			if(i==lim)
				return;
			put(i, meaning);
		}
		
		void remove(String meaning) {
			int i=indexOf(meaning);
			if(i!=-1)
				meanings[i]=null;
		}
		
		int indexOf(String meaning) {
			if(meaning==null)
				return -1;
			boolean found=false;
			int lim=meanings.length;
			for(int i=0; i<lim; i++) {
				if(meaning.equals(meanings[i])) {
					return i;
				}					
			}
			return -1;
		}
	}

	//Static member fields.
	//The list of callers in the queue.
	static HashMap callers = new HashMap();

	//Set to whatever we're passed when callerId is unavailable.
	static final String UNKNOWN = "unknown";

	//The logger
	static Logger log = Logger
			.getLogger("com.orderlysoftware.orderlycalls.examples.OrderlyQExample");

	//Where to find the sounds
	static String prefix = "OrderlyCalls/";

	//The names of the numbers.
	static String numbers[] = new String[] { "zero", "one", "two", "three",
			"four", "five", "six", "seven", "eight", "nine", "ten", "eleven",
			"twelve", "thirteen", "fourteen", "fifteen", "sixteen",
			"seventeen", "eighteen", "nineteen" };

	//Store of settings.
	static HashMap settingsByServer = new HashMap();

	//Instance member fields - must be reset in clean()
	//Caller menu.
	Meanings meanings = new Meanings();

	//We store this so we don't have to keep passing it to functions.
	AGIConnection agi=null;

	//The current caller.
	Caller caller=null;
	
	//Estimated wait time.
	int secondsLeft=0;

	//The settings object to use.
	OrderlyQExampleSettings settings=null;
	
	boolean extra=false;
	boolean text=false;
	
	/**
	 * Gets settings associated with a particular AGIServer.
	 * Settings are extracted from XML if none can be found.
	 * 
	 * @param server The server in which to look for the settings.
	 * @return The settings.  Default values are used if none can be found.
	 */
	public static OrderlyQExampleSettings getSettings(AGIServer server) {
		OrderlyQExampleSettings settings = (OrderlyQExampleSettings) settingsByServer
				.get(server);
		if (settings != null)
			return settings;
		settings = new OrderlyQExampleSettings();
		setSettings(server, settings);
		AGISettings agiSettings = (AGISettings) server.getSettings();
		NodeList oqList = agiSettings.getXMLSettings().getElementsByTagName(
				"OrderlyQ");
		if (oqList.getLength() > 1) {
			log
					.warning("OrderlyQExample only supports one set of settings.  Using defaults.");
			return settings;
		}
		Element oqElement = (Element) oqList.item(0);
		settings.setXMLSettings(oqElement);
		return settings;
	}

	/**
	 * Store a settings object for a particular AGIServer.
	 * 
	 * @param server The server corresponding to the settings
	 * @param settings the settings to store.
	 */
	public static void setSettings(AGIServer server,
			OrderlyQExampleSettings settings) {
		settingsByServer.put(server, settings);
	}

	//Instance member functions.
	private void clean() {
		meanings = new Meanings();
		agi=null;
		caller=null;
		secondsLeft=0;
		settings=null;
		extra=false;
		text=false;
	}

	private Caller getCaller(String callerId) {
		return (Caller) callers.get(callerId);
	}

	private void newCaller(Caller c) {
		log.info("Adding caller with id "+c.id);
		if (!c.id.equals(UNKNOWN))
			callers.put(c.id, c);
	}

	
	private int sayNumber(int number, String stopDigits) throws IOException {
		//We only have the sound files to say numbers up to n.i. 60
		if (number > 59)
			return -2;

		//We have sound files for all numbers up to 20.
		if (number < 20) {
			return agi.streamFile(prefix + numbers[number], stopDigits);
		}
		//Otherwise work out which 'decade'
		int tens = number / 10;

		//And which remainder
		int remainder = number % 10;

		int retval = -1;

		//Say the decade
		if (tens == 2) {
			retval = agi.streamFile(prefix + "twenty", stopDigits);
		} else if (tens == 3) {
			retval = agi.streamFile(prefix + "thirty", stopDigits);
		} else if (tens == 4) {
			retval = agi.streamFile(prefix + "forty", stopDigits);
		} else if (tens == 5) {
			retval = agi.streamFile(prefix + "fifty", stopDigits);
		}

		//Check for interrupted
		if (retval != 0)
			return retval;

		//Say the remainder
		if (remainder != 0) {
			return agi.streamFile(prefix + numbers[remainder], stopDigits);
		}

		//Shouldn't really be here...
		return -1;
	}

	private int sayTime(int seconds, String stopDigits, int rounding) throws IOException {
		//Find the number of hours
		int numHours = seconds / 3600;

		//Find the number of minutes
		int numMinutes = (seconds / 60) % 60;

		int retval;

		if(rounding==OrderlyQExampleSettings.UP) {
		//Round up the minutes to avoid early calling.
			if (numMinutes < 59 && numMinutes > 0)
				numMinutes += 1;
		}
		//Say the number of hours
		if (numHours > 0) {
			retval = sayNumber(numHours, stopDigits);
			if (retval != 0)
				return retval;
			if (numHours > 1)
				retval = agi.streamFile(prefix + "hours", stopDigits);
			else
				retval = agi.streamFile(prefix + "hour", stopDigits);
			if (retval != 0)
				return retval;
		}

		//Say "and" if there are minutes too
		if (numHours > 0 && numMinutes > 0) {
			retval = agi.streamFile(prefix + "and", stopDigits);
			if (retval != 0)
				return retval;
		}

		//Say the minutes
		if (numHours > 0 && numMinutes == 0) {
			return 0;
		}
		if (numMinutes > 0) {
			retval = sayNumber(numMinutes, stopDigits);
		} else {
			retval = agi.streamFile(prefix + "less-than");
			retval = sayNumber(1, stopDigits);

		}
		if (retval != 0)
			return retval;
		if (numMinutes > 1)
			retval = agi.streamFile(prefix + "minutes", stopDigits);
		else
			retval = agi.streamFile(prefix + "minute", stopDigits);
		if (retval != 0)
			return retval;
		
		return 0;
	}

	/**
	 * Says a string of digits.
	 * 
	 * @param digits the digits to say.
	 * @param stopDigits stop saying if any of these are pressed.
	 * @param sayOh say "Oh" instead of "Zero".
	 * @return interrup digit (ASCII), or 0.
	 * @throws IOException
	 */
	private int sayDigits(String digits, String stopDigits, boolean sayOh)
			throws IOException {
		int retval = 0;

		//Iterate through the digits
		for (int i = 0; i < digits.length(); i++) {
			//One at a time...
			String digit = digits.substring(i, i + 1);

			int num = Integer.parseInt(digit);

			//Say the number
			if (sayOh && num == 0) {
				retval = agi.streamFile(prefix + "oh", stopDigits);
			} else {
				retval = agi.streamFile(prefix + numbers[num], stopDigits);
			}
			//Interrupted
			if (retval != 0)
				return retval;
		}
		return 0;
	}

	/**
	 * Passes the caller through to the agents.
	 * 
	 * @throws IOException
	 */
	private void passThrough() throws IOException {
		agi.streamFile(prefix + "you-are-now-at-the-front-of-the-queue");
		agi.streamFile(prefix + "please-hold-while-we-transfer");

		agi.exec("Ringing", "5");
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Date now=new Date();
		caller.kick=new Date(now.getTime()+(long)(settings.getPersistAfterCall()*60000));
		
		String priStr = agi.getAGIProperty("agi_priority");
		int pri = Integer.parseInt(priStr);
		pri += 2;
		agi.setPriority("" + pri);
		
		log.info("Closing Connection");
		 
		agi.clean();
		throw new IOException("PASSED THROUGH");
	}

	/**
	 * Ask caller to enter a number.
	 * 
	 * @param message  The request message to play
	 * @return the number entered.
	 * 
	 * @throws IOException
	 */
	public String enterNumber(String message) throws IOException {
		boolean complete=false;
		StringBuffer numberSB=new StringBuffer();
		int digit;
		String stopDigits="1234567890";
		String answerDigits="12";
		while(numberSB.toString().equals("")) {
			//Play the message, listening for interrup.
			digit=agi.streamFile(prefix+message, stopDigits);
			if(digit>0) {
				//Append the interrupt digit, if any.
				numberSB.append(AGIConnection.asciiToChar(digit));
			} else {
				digit=agi.streamFile(prefix+"including-the-area-code", stopDigits);
				if(digit>0) {
					//Append the interrupt digit, if any.
					numberSB.append(AGIConnection.asciiToChar(digit));
				} else {
					//Tell the caller to terminate the number with #.
					digit=agi.streamFile(prefix+"followed-by-the-pound-key",stopDigits);
					if(digit>0) {
						//Append the interrupt digit, if any.
						numberSB.append(AGIConnection.asciiToChar(digit));
					}
				}
			}
			
			//Get the number, one digit at a time.
			while(!complete) {	
				log.fine("Waiting");
				digit=agi.waitForDigit(10000);
				log.fine("Result "+digit);
				if(digit>0) {
					char digitChar=AGIConnection.asciiToChar(digit);
					
					//See if the number is complete.
					if(digitChar=='#' || digitChar=='*') {
						break;
					}
					//Otherwised append the digit.
					numberSB.append(digitChar);
				} else {
					//If timed out, we clear and ask again.
					numberSB=new StringBuffer();
				
					break;						
				}
			}
			log.fine("Entered "+numberSB.toString());
			if(numberSB.toString().equals("")) {
				//If we don't have the number, ask again.
				continue;
			}
			
			//Now find out whether the caller is happy with the number entered.
			boolean answered=false;
			while(!answered) {
				//Read back the number, listening for interrupt.
				digit=agi.streamFile(prefix+"the-number-you-entered-is",answerDigits);
				if(digit==0) {
					digit=sayDigits(numberSB.toString(),answerDigits,false);
				}
				if(digit==0) {
					//Ask if correct.
					digit=agi.streamFile(prefix+"please-press-one-if-correct-or-two", answerDigits);
					if(digit==0)
						digit=agi.waitForDigit(10000);
				}
				if(digit>0) {
					char digitChar=AGIConnection.asciiToChar(digit);
					if(digitChar=='1') {
						//The caller is happy.
						log.fine("Accepted "+numberSB);
						answered=true;
						complete=true;
						break;
					} else if(digitChar=='2') {
						//Ask again.
						log.fine("Rejected "+numberSB);
						numberSB=new StringBuffer();
						break;
					}				
				}
			}
		}
		//Acknowledge.
		agi.streamFile(prefix+"thankyou");
		agi.waitForDigit(500);
		log.fine("Returning "+numberSB);
		return numberSB.toString();
	}
	
	/**
	 * Handles interrupt from caller to enter data.
	 * 
	 * @param result the number pressed (ASCII).
	 * 
	 * @return true if anything was played, false otherwise.
	 * 
	 * @throws IOException
	 */
	public boolean processPress(int result) throws IOException {
		log.fine("processPress "+result);
		//Was a number pressed?
		if(result<=0)
			return false;
		
		//Get the number.
		char c=AGIConnection.asciiToChar(result);
		result=AGIConnection.charToInt(c);
		
		//Get the meaning of the number.
		String meaning=meanings.get(result);
		
		if(meaning==null) {
			//Meaningless interrupt.
			return false;
		} if(meaning.equals("number")) {
			//Enter number (after witholding callerID).
			log.info("Requesting phone number");
			
			//Get the number.
			String number=enterNumber("please-enter-the-number-you-will-be-calling-from");
			log.info("Number is "+number);
			
			//Associate it with the caller.
			caller.id=number;
			
			//See if we already have one.
			Caller testCaller=(Caller)callers.get(number);
			
			if(testCaller!=null) {
				//Recognised.
				log.info("Recognised");
				//Use the stored caller object.
				caller=testCaller;
				//Tell the caller he/she's been recognised.
				agi.streamFile(prefix
					+ "your-phone-number-has-been-recognised");
				//Get the new time.
				setSecondsLeft(caller);
				
				if(secondsLeft<=0) {
					//Send them through.
					passThrough();
					return true;
				}
				//Give them update wait time.
				agi.streamFile(prefix
						+ "and-your-estimated-wait-time-is-now");
				sayTime(secondsLeft, null, settings.getRounding());
				
			} else {
				//It's a new caller - add him/her to the queue.
				newCaller(caller);
			}
			//Flag that we've got the number
			caller.noNumber=false;
			
			//Don't ask again.
			meanings.remove("number");
			return true;
		}
		
		if(meaning.equals("extra")) {
			log.info("Requesting extra number");
			//Enter additional number			
			String number=enterNumber("please-enter-the-additional-number-you-will-be");
			
			//Store the number in the caller
			caller.alternates.add(number);
			
			//Add the caller under this number.
			callers.put(number, caller);
			
			//Stop this from being pressed again.
			meanings.remove("extra");
			extra=false;
			return true;
		}
		
		if(meaning.equals("text")) {
			log.info("Sign up for text requested");
			
			if(caller.isMobile) {
				//Then we don't ask for a number, we just send the text.
				log.info("Will send text to "+caller.id);
				
				//Acknowlege.
				agi.streamFile(prefix+"thankyou");
				
			} else {
				//Enter number to text.
				String number=enterNumber("please-enter-the-number-you-would-like-us-to-text");
				log.info("Will send text to "+number);
			}
			//Flag that caller has already requested a text.
			caller.hasText=true;
			
			//Don't ask for text again.
			meanings.remove("text");	
			text=false;
			return true;
		}		
		return false;
	}
	
	public int peopleAheadOf(Caller caller) {
		Iterator i=callers.keySet().iterator();
		Date now=new Date();
		int count=0;
		while(i.hasNext()) {
			Object key=i.next();
			Caller compare=(Caller)callers.get(key);
			if(compare==null || caller==null)
				continue;
			if(compare==caller)
				continue;
			if(compare.kick.getTime()<now.getTime()) {
				log.info("Kicking "+key);
				i.remove();
				continue;
			}
			if(compare.ready.getTime()<caller.ready.getTime())
				count++;
		}
		return count;
	}

	private void kick() {
		Iterator i=callers.keySet().iterator();
		Date now=new Date();
		int count=0;
		while(i.hasNext()) {
			Object key=i.next();
			Caller compare=(Caller)callers.get(key);
			if(compare.kick.getTime()<now.getTime()) {
				log.info("Kicking "+key);
				i.remove();
				continue;
			}
		}
	}
	
	public void setSecondsLeft(Caller caller) {
		Date now=new Date();
		secondsLeft = (int) Math.ceil((caller.ready.getTime() - now
				.getTime()) / 1000);
	}
	public void processCall(AGIConnection agi) throws IOException {
		try {
			this.agi = agi;
			//Gets the settings associated with the calling server.
			settings = getSettings(agi.getServer());

			log.info("Using settings: " + settings);

			//Get the caller's number
			log.info("Properies: " + agi.getAGIProperties());
			log.info("Caller ID is: " + agi.getAGIProperty("agi_callerid"));

			String numberStr = agi.getAGIProperty("agi_callerid");
			
			kick();
			
			caller = getCaller(numberStr);

			//Is this the first time they've called the line?
			boolean firstCall = false;

			//Did we recognise their phone number
			boolean isRecognised = false;

			if (caller == null) {
				caller = new Caller();
				caller.joined = new Date();
				caller.ready = new Date(caller.joined.getTime()
						+ (long) (60000 * 4.5));
				caller.id = numberStr;
				caller.kick=new Date(caller.ready.getTime()+(long)(settings.getPersistNoCall()*60000));
				if (numberStr.indexOf(UNKNOWN)!=-1) {
					log.info("No Caller ID available.");
					caller.noNumber = true;
				} else {
					newCaller(caller);
				}
				firstCall = true;
				isRecognised = false;
			} else {
				firstCall = false;
				isRecognised = true;
			}


			//Are they valid?
			boolean isThrough = false;

			setSecondsLeft(caller);
			if (secondsLeft < 0)
				isThrough = true;

			int result;
			int peopleAhead=peopleAheadOf(caller);
			log.info("People ahead of caller: "+peopleAhead);
			
			String escapeDigits = null;
			caller.isMobile=Pattern.matches(settings.getMobilePattern(),numberStr);
				
			extra=false;
			text=false;
			
			if(caller.noNumber) {				
				meanings.append(1,"number");
			} else {
				//Callers who withold caller ID may not enter another number
				//Until they've entered the first one.
				int extraOffer=settings.getExtraOffer();
				if(extraOffer==OrderlyQExampleSettings.ALL 
						|| (extraOffer==OrderlyQExampleSettings.SIZE && peopleAhead>=settings.getExtraSize() )
						|| (extraOffer==OrderlyQExampleSettings.WAIT && secondsLeft>=settings.getExtraWait()*60.0)
					)
					extra=true;
				
				if(!firstCall && settings.getExtraPrompt()==OrderlyQExampleSettings.ENTRY)
					extra=false;
				
				int textOffer=settings.getTextOffer();
				
				
				if(textOffer==OrderlyQExampleSettings.ALL 
						|| (textOffer==OrderlyQExampleSettings.SIZE && peopleAhead>=settings.getTextSize() )
						|| (textOffer==OrderlyQExampleSettings.WAIT && secondsLeft>=settings.getTextWait()*60.0)
					)
					text=true;
				
				if(!firstCall && settings.getTextPrompt()==OrderlyQExampleSettings.ENTRY)
					text=false;
				if(!caller.isMobile && settings.getTextAvailable()==OrderlyQExampleSettings.MOBILES)
					text=false;
				if(caller.hasText)
					text=false;
			}
			if(extra) 
				meanings.append(1,"extra");
		
			if(text)
				meanings.append(1,"text");
			boolean activate=false;
			if(settings.getActivate()==OrderlyQExampleSettings.BUSY 
					|| (settings.getActivate()==OrderlyQExampleSettings.SIZE && peopleAhead>=settings.getActivateSize())
					|| (settings.getActivate()==OrderlyQExampleSettings.WAIT && secondsLeft>settings.getActivateWait()*60.0)
			) {
				activate=true;
			}
			//isThrough=true;
			//firstCall=false;
			//Welcome? Or welcome back?
			escapeDigits=meanings.getDigits();
			log.info("Escape digits are "+escapeDigits);
			if (firstCall) {
				processPress(agi.streamFile(prefix + "hello-orderlyq",
						escapeDigits));
			} else {
				processPress(agi.streamFile(prefix + "welcome-back",
						escapeDigits));
			}
			
			//If they're already at the front, let'em have it
			
			if (isThrough) {
				//caller.remove();
				passThrough();
				return;
			}

			if (!isRecognised) {
				processPress(agi.streamFile(prefix
						+ "due-to-overwhelming-demand", escapeDigits));
				processPress(agi.streamFile(prefix + "your-call-is-being-held",
						escapeDigits));
				
/*				if(activate && !caller.noNumber) {
					processPress(agi.streamFile(prefix
							+ "weve-saved-your-place-in-the-queue-for-you",
							escapeDigits));
					processPress(agi.streamFile(prefix
							+ "so-you-can-hang-up-and-call-back", escapeDigits));
					processPress(agi.streamFile(prefix
							+ "without-losing-your-place", escapeDigits));					
				}
				*/
				processPress(agi.streamFile(prefix
						+ "your-estimated-wait-time-is", escapeDigits));

			} else {
				processPress(agi
						.streamFile(prefix
								+ "your-phone-number-has-been-recognised",
								escapeDigits));
				processPress(agi.streamFile(prefix
						+ "and-your-estimated-wait-time-is-now", escapeDigits));
			}

			processPress(sayTime(secondsLeft, escapeDigits, settings.getRounding()));
			
			if(activate && caller.noNumber) {
				processPress(agi.streamFile(prefix
						+ "weve-saved-your-place-in-the-queue-for-you",
						escapeDigits));
				processPress(agi.streamFile(prefix
						+ "so-you-can-hang-up-and-call-back", escapeDigits));
				processPress(agi.streamFile(prefix
						+ "without-losing-your-place", escapeDigits));
				
			}
			
			if(activate && !caller.noNumber) {
				processPress(agi.waitForDigit(250));
				
				processPress(agi.streamFile(prefix
						+ "please-call-back-after-this-time", escapeDigits));
				if (firstCall && !isRecognised) {
					processPress(agi.streamFile(prefix
							+ "your-phone-number-will-be-recognised", escapeDigits));
				}
				processPress(agi.streamFile(prefix + "and-we-will-transfer",
						escapeDigits));
				processPress(agi.waitForDigit(500));
		    }
			if(activate && extra) {
				processPress(agi.streamFile(prefix + "if-you-think-you-might-be-calling-back",
						escapeDigits));
				processPress(agi.streamFile(prefix + "please-press"));
				processPress(agi.streamFile(prefix + numbers[meanings.indexOf("extra")]));
				processPress(agi.streamFile(prefix + "at-any-time"));
			}
			if(activate && text) {
				processPress(agi.streamFile(prefix + "if-you-would-like-to-receive-a-text",
						escapeDigits));
				processPress(agi.streamFile(prefix + "please-press"));
				processPress(agi.streamFile(prefix + numbers[meanings.indexOf("text")]));
				processPress(agi.streamFile(prefix + "at-any-time"));
			}
			if(!caller.noNumber)
				processPress(agi.waitForDigit(250));

			boolean hangup=false;
			
			if(settings.getHangUp()==OrderlyQExampleSettings.ALL
					|| (settings.getHangUp()==OrderlyQExampleSettings.SIZE && peopleAhead > settings.getHangUpSize()) 
					|| (settings.getHangUp()==OrderlyQExampleSettings.WAIT && settings.getHangUpWait()*60 < secondsLeft )
				) {
				hangup=true;
			}
			
			if(caller.noNumber)
				hangup=false;
			if(activate && hangup) {
				processPress(agi.streamFile(prefix + "this-message"));
				processPress(agi.streamFile(prefix + "thankyou-so-much"));
				agi.hangUp();
				return;
			}
					
			//Music
			if (activate && caller.noNumber) {
				processPress(agi.streamFile(prefix + "to-use-this-feature"));
				processPress(agi.streamFile(prefix + "please-press"));
				processPress(agi.streamFile(prefix + "one"));
				processPress(agi.streamFile(prefix + "at-any-time"));
			}
		
			if(activate)
				processPress(agi.streamFile(prefix + "or-you-can-continue-to-hold",
						escapeDigits));

			log.info("Playing music (if you haven't set up default music-on-hold, you'll hear silence)");
				

			boolean waiting = true;
			Date now=new Date();
			
			int minsLeft = (int) Math.ceil((caller.ready.getTime() - now
					.getTime()) / 60000.0f);
			
			if(settings.getMusic().equals("default")) {
				agi.setMusic(true);
			} else {
				agi.setMusic(true,settings.getMusic());
			}
			long wait = (int) Math.min(30000, secondsLeft * 1000);

			while (waiting) {

				if(processPress(agi.waitForDigit(wait))) {
					if(settings.getMusic().equals("default")) {
						agi.setMusic(true);
					} else {
						agi.setMusic(true,settings.getMusic());
					}	
				}
				
				//Thread.sleep(wait);
				now = new Date();

				int newSecondsLeft = (int) Math
						.ceil((caller.ready.getTime() - now.getTime()) / 1000);
				wait = (int) Math.min(10000, (newSecondsLeft + 2) * 1000);

				int newMinsLeft = (int) Math.ceil(newSecondsLeft / 60.0f);
				log.info("New: " + newMinsLeft + " Old: " + minsLeft);
				if (newSecondsLeft <= 0) {
					waiting = false;
					break;
				}
				if (newMinsLeft < minsLeft) {
					processPress(agi.streamFile(prefix
							+ "your-estimated-wait-time-is-now", escapeDigits));
					processPress(sayTime(newSecondsLeft, escapeDigits,settings.getRounding()));

					if(activate) {
						processPress(agi.streamFile(prefix
								+ "please-call-back-after-this-time", escapeDigits));
						processPress(agi.streamFile(
								prefix + "and-we-will-transfer", escapeDigits));
						processPress(agi.streamFile(prefix
								+ "or-you-can-continue-to-hold", escapeDigits));
					}
					
				}
				if(settings.getMusic().equals("default")) {
					agi.setMusic(true);
				} else {
					agi.setMusic(true,settings.getMusic());
				}
		
				minsLeft = newMinsLeft;
			}
			
			passThrough();
		} catch (Exception e) {
			log.log(Level.WARNING, "Example terminated early: "+e.getMessage());
			//e.printStackTrace();
		} finally {
			clean();
		}
	}
}