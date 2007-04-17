package org.javier.jacob;

import annotations.OleProperty;

import com.jacob.com.Dispatch;

public interface Dispatchable {
	@OleProperty Dispatch _GET_JACOB_DISPATCH_();
}
