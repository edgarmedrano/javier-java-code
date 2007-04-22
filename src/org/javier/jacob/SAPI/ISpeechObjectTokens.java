package org.javier.jacob.SAPI;

import org.javier.jacob.Dispatchable;
import org.javier.jacob.OleInterface;
import org.javier.jacob.OleMethod;
import org.javier.jacob.OleProperty;


@OleInterface(name="Sapi.ISpeechObjectTokens")
public interface ISpeechObjectTokens extends Dispatchable {
	@OleProperty long Count();
	@OleMethod SpObjectToken Item(long index);
}
