/**
 * File:        SpObjectToken.java
 * Description: SpObjectToken from MS SAPI
 * Author:      Edgar Medrano Pérez
 *              edgarmedrano at gmail dot com
 * Created:     2007.04.21
 * Company:     JAVIER project
 *              http://javier.sourceforge.net
 * Notes:        
 */
package org.javier.jacob.SAPI;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * represents an available resource of a type used by SAPI. The default 
 * interface for this object is ISpeechObjectTokens.
 * 
 * <p>The Speech configuration database contains folders representing the 
 * resources on a computer that are used by SAPI 5.1 speech recognition (SR)
 * and text-to-speech (TTS). These folders are organized into resource 
 * categories, such as voices, lexicons, and audio input devices. 
 * The SpObjectTokenCategory object provides access to a category of 
 * resources, and the SpObjectToken object provides access to a single 
 * resource.</p>
 * 
 * <p>Several Speech Automation objects support methods that return 
 * collections of resources from a specific category of available resources. 
 * Examples are SpVoice.GetAudioOutputs, SpVoice.GetVoices and 
 * SpSharedRecognizer.GetProfiles, as well as the SpObjectToken object's 
 * MatchesAttributes method. Each of these operations returns an 
 * ISpeechObjectTokens object variable containing a collection of 
 * SpObjectToken objects.</p>
 * 
 * <p>The read-only Id property of an SpObjectToken object is the path to 
 * the folder of the resource with which it is associated. The read-only 
 * DataKey property is a data key object providing read and write access 
 * to this folder. An SpObjectToken created with the New keyword has an 
 * empty Id property, and is therefore not associated with a resource. 
 * Before it can be used, a new SpObjectToken must be associated with a 
 * resource by means of its SetId method.</p>
 * 
 * <p>The SpObjectToken object also provides the ability to create and access
 * storage files associated with a resource. The paths of data storage files 
 * created by an engine or by applications for a specific resource are stored
 * in its object token.</p>
 */
@OleInterface(name="Sapi.SpObjectToken")
public interface SpObjectToken extends Dispatchable {
	
	/**
	 * Gets the category of the object token as an SpObjectTokenCategory 
	 * object.
	 * 
	 * @return the category
	 */
	@OleProperty SpObjectTokenCategory getCategory();
	
	/**
	 * Sets the category of the object token as an SpObjectTokenCategory 
	 * object.
	 * 
	 * @param category the category
	 */
	@OleProperty void setCategory(SpObjectTokenCategory category);
	
	/**
	 * Gets the data key of the object token as an ISpeechDataKey object.
	 * 
	 * @return the data key
	 */
	@OleProperty Dispatch getDataKey();
	
	/**
	 * Gets the data key of the object token as an ISpeechDataKey object.
	 * 
	 * @param dataKey the data key
	 */
	@OleProperty void setDataKey(Dispatch dataKey);
	
	/**
	 * Gets the ID of the token.
	 * 
	 * @return the ID
	 */
	@OleProperty String getId();
	
	/**
	 * Sets the ID of the token.
	 * 
	 * @param id the ID
	 */
	@OleProperty void setId(String id);

	/**
	 * Creates an instance of the object represented by the token.
	 * 
	 * @return the object represented by the token.
	 */
	@OleMethod Dispatch CreateInstance();
	
	/**
	 * Creates an instance of the object represented by the token.
	 * 
	 * @param pUnkOuter the pUnkOuter. By default, the Nothing value is used. 
	 * 
	 * @return the object represented by the token.
	 */
	@OleMethod Dispatch CreateInstance(Dispatch pUnkOuter);
	
	/**
	 * Creates an instance of the object represented by the token.
	 * 
	 * @param pUnkOuter the pUnkOuter. By default, the Nothing value is used. 
	 * @param ClsContext the ClsContext. By default STCALL is used. 
	 * 
	 * @return the object represented by the token.
	 */
	@OleMethod Dispatch CreateInstance(Dispatch pUnkOuter
			,int ClsContext);
	
	/**
	 * displays the specified UI.
	 * 
	 * @param hWnd     the hWnd
	 * @param Title    the Title
	 * @param TypeOfUI the TypeOfUI
	 */
	@OleMethod void DisplayUI(
			int hWnd,
		    String Title,
		    String TypeOfUI);
	
	/**
	 * displays the specified UI.
	 * 
	 * @param hWnd      the hWnd
	 * @param Title     the Title
	 * @param TypeOfUI  the TypeOfUI
	 * @param ExtraData the ExtraData. By default, the Nothing value is used. 
	 */
	@OleMethod void DisplayUI(
			int hWnd,
		    String Title,
		    String TypeOfUI,
		    Variant ExtraData);
	
	/**
	 * displays the specified UI.
	 * 
	 * @param hWnd      the hWnd
	 * @param Title     the Title
	 * @param TypeOfUI  the TypeOfUI
	 * @param ExtraData the ExtraData. By default, the Nothing value is used. 
	 * @param Object    the Object. By default, the Nothing value is used.
	 */
	@OleMethod void DisplayUI(
			int hWnd,
		    String Title,
		    String TypeOfUI,
		    Variant ExtraData,
		    Dispatch Object);
	
	/**
	 * Gets the value of the specified attribute.
	 * 
	 * @param AttributeName the attribute name
	 * 
	 * @return the specified attribute.
	 */
	@OleMethod String GetAttribute(String AttributeName);
	
	/**
	 * Gets the name of the resource represented by the object token.
	 * 
	 * @return the name of the resource
	 */
	@OleMethod String GetDescription();
	
	/**
	 * Gets the name of the resource represented by the object token.
	 * 
	 * @param Locale the Locale. By default, zero is used. 
	 * 
	 * @return the name of the resource
	 */
	@OleMethod String GetDescription(int Locale);
	
	/**
	 * creates a storage file for data associated with the object token.
	 * 
	 * @param ObjectStorageCLSID
	 *            Globally unique identifier (GUID) of the calling object.
	 *            The method searches the registry for an entry key name of 
	 *            ObjectStorageCLSID, and then a corresponding Files subkey. 
	 *            If the registry entry is not present, one is created. 
	 * @param KeyName  The name of the attribute file for the registry entry 
	 *                 of clsidCaller. This attribute stores the location of 
	 *                 the resource file. 
	 * @param FileName A specifier that is either "" or a path/file name for 
	 *                 storage file.
	 *                 <ul>
	 *                 <li>If this starts with "X:\" or "\\" it is assumed 
	 *                 to be a full path; otherwise it is assumed to be 
	 *                 relative to special folders given in the nFolder 
	 *                 parameter.</li>
	 *                 <li>If it ends with a "\", or is NULL, a unique file 
	 *                 name will be created. The file name will be something 
	 *                 like: "SP_7454901D23334AAF87707147726EC235.dat". 
	 *                 "SP_" and ".dat" are the default prefix name and file 
	 *                 extension name. The numbers in between are generated 
	 *                 guid number to make sure the file name is unique.</li>
	 *                 <li>If the name contains a %d the %d is replaced by a 
	 *                 number to give a unique file name. The default file 
	 *                 extension is .dat, the user can specify anything else.
	 *                 Intermediate directories are created.</li>
	 *                 <li>If a relative file is used, the value stored in 
	 *                 the registry includes the nFolder value as %nFolder% 
	 *                 before the rest of the path.</li>
	 *                 </ul>
	 * @param Folder   One or more SpeechTokenShellFolder constants specifying 
	 *                 the Folder. 
	 * 
	 * @return A String variable containing the path of the storage file.
	 */
	@OleMethod String GetStorageFileName(
			String ObjectStorageCLSID,
			String KeyName,
			String FileName,
			SpeechTokenShellFolder Folder);
	
	/**
	 * determines if the specified UI is supported.
	 * 
	 * @param TypeOfUI  the TypeOfUI. 
	 * @param ExtraData the ExtraData. By default, the Nothing value is used.
	 * @param Object    the Object. By default, the Nothing value is used. 
	 * 
	 * @return <code>true</code>, if the specified UI is supported<br>
	 *         <code>false</code>, if it is not supported.
	 */
	@OleMethod boolean IsUISupported(
			String TypeOfUI,
		    Variant ExtraData,
		    Dispatch Object);
	
	/**
	 * Indicates whether the token matches specified attributes.
	 * 
	 * @param Attributes the attributes
	 * 
	 * @return <code>true</code>, if the token matches the specified 
	 *         attributes.<br>
	 *         <code>false</code>, if it does not match.
	 */
	@OleMethod boolean MatchesAttributes(String Attributes);
	
	/**
	 * Removes the token from the speech configuration database.
	 * 
	 * @param ObjectStorageCLSID
	 *            the CLSID associated with the object token to remove. 
	 *            If ObjectStorageCLSID is an empty string ("") or 
	 *            vbNullString, the entire token is removed; otherwise, 
	 *            only the specified section is removed. 
	 */
	@OleMethod void Remove(String ObjectStorageCLSID);
	
	/**
	 * Removes a storage file associated with the object token.
	 * 
	 * @param ObjectStorageCLSID
	 *            The globally unique identifier (GUID) of the calling object.
	 * @param KeyName    The KeyName.
	 * @param DeleteFile If <code>true</code>, the storage file will be 
	 *                   deleted after removal. 
	 */
	@OleMethod void RemoveStorageFileName(
			String ObjectStorageCLSID,
			String KeyName,
		    boolean DeleteFile);
	
	/**
	 * associates a new object token with a resource by setting its ID 
	 * property.
	 * 
	 * @param Id The ID of the token.
	 */
	@OleMethod void SetId(String Id);
	
	/**
	 * associates a new object token with a resource by setting its ID 
	 * property.
	 * 
	 * @param Id         The ID of the token.
	 * @param CategoryID The category ID of the token. By default the value 
	 *                   is the empty string value of "". 
	 */
	@OleMethod void SetId(String Id
			, String CategoryID);
	
	/**
	 * associates a new object token with a resource by setting its ID 
	 * property.
	 * 
	 * @param Id         The ID of the token.
	 * @param CategoryID The category ID of the token. By default the value 
	 *                   is the empty string value of "". 
	 * @param CreateIfNotExist
	 *            Specifies creating the token. If <code>true</code>, the 
	 *            folder is created if one does not already exist. By default 
	 *            the value is <code>false</code>, and no folder is created. 
	 */
	@OleMethod void SetId(String Id
			, String CategoryID
			, boolean CreateIfNotExist);
}
