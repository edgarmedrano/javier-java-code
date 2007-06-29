function __DUMP__(ref, stack) {
	var result = [];
	var hasFields = false;
	var snst = "\n";
	
	if(!ref) {
		return "undefined";
	}
	
	if(stack) {
		/*
		if(stack.length == 3) {
			return "too deep!";
		}
		*/
		for(var i = 0; i < stack.length; i++) {
		    snst += "\t";
			if(stack[i] == ref) {
				return "/* circular reference */";
			}
		}
		stack.push(ref);
	} else {
		stack = new Array(ref);
	}
	
	for(var field in ref) {
		if(hasFields) {
			result.push(snst);
			result.push(", ");
		} else {
			hasFields = true;
		}
		result.push(field);
		result.push(": ");
		if(typeof(ref[field]) == "object" && ref[field]) {
			var constructor = ("" + ref[field].constructor).match(/(function\s+)?\[?(\w+)\]?.*/)[2];
			result.push(constructor);
			if(constructor != "undefined" 
				&& constructor != "JavaPackage") {
				result.push("{");
				result.push(snst);
				result.push("\t");
				result.push(__DUMP__(ref[field], stack));
				result.push(snst);
				result.push("}");
			}
		} else if(typeof(ref[field]) == "string") {
			result.push("\"");					
			result.push(ref[field]);					
			result.push("\"");					
		} else if(typeof(ref[field]) == "function") {
			result.push("function");					
		} else {
			var value = "" + ref[field];
			
			result.push(("" + ref[field]).substring(0,30).replace(/\s+/g," "));			
		}
	}
	
	stack.pop();
	
	return result.join("");
}