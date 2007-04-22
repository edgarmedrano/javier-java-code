package org.javier.jacob.SAPI;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;


@OleInterface(name="Sapi.SpObjectToken")
public interface SpObjectToken extends Dispatchable {
	@OleProperty SpObjectTokenCategory getCategory();
	
	@OleProperty void setCategory(SpObjectTokenCategory category);
	
	@OleProperty Dispatch getDataKey();
	
	@OleProperty void getDataKey(Dispatch dataKey);
	
	@OleProperty String getId();
	
	@OleProperty void setId(String id);

	@OleMethod Dispatch CreateInstance();
	
	@OleMethod Dispatch CreateInstance(Dispatch pUnkOuter);
	
	@OleMethod Dispatch CreateInstance(Dispatch pUnkOuter
			,int ClsContext);
	
	@OleMethod void DisplayUI(
			int hWnd,
		    String Title,
		    String TypeOfUI);
	
	@OleMethod void DisplayUI(
			int hWnd,
		    String Title,
		    String TypeOfUI,
		    Variant ExtraData);
	
	@OleMethod void DisplayUI(
			int hWnd,
		    String Title,
		    String TypeOfUI,
		    Variant ExtraData,
		    Dispatch Object);
	
	@OleMethod String GetAttribute(String AttributeName);
	
	@OleMethod String GetDescription();
	
	@OleMethod String GetDescription(int Locale);
	
	@OleMethod String GetStorageFileName(
			String ObjectStorageCLSID,
			String KeyName,
			String FileName,
			SpeechTokenShellFolder Folder);
	
	@OleMethod boolean IsUISupported(
			String TypeOfUI,
		    Variant ExtraData,
		    Dispatch Object);
	
	@OleMethod boolean MatchesAttributes(String Attributes);
	
	@OleMethod void Remove(String ObjectStorageCLSID);
	
	@OleMethod void RemoveStorageFileName(
			String ObjectStorageCLSID,
			String KeyName,
		    boolean DeleteFile);
	
	@OleMethod void SetId(String Id);
	
	@OleMethod void SetId(String Id
			, String CategoryID);
	
	@OleMethod void SetId(String Id
			, String CategoryID
			, boolean CreateIfNotExist);

}
