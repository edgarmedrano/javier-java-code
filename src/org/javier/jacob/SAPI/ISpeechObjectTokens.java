package org.javier.jacob.SAPI;

import org.javier.jacob.Dispatchable;

import annotations.OleInterface;
import annotations.OleMethod;
import annotations.OleProperty;

@OleInterface(name="Sapi.ISpeechObjectTokens")
public interface ISpeechObjectTokens extends Dispatchable {
	@OleProperty long Count();
	@OleMethod SpObjectToken Item(long index);
}
