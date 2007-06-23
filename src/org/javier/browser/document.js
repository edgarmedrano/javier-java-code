function __grammar_length(grammars, weight) {
	var length;
	var result = weight > 0 ? 32767 : 0;
	
	for(int i = 0; i < grammars.length; i++) {
	    var rules = grammars[i].rule;
		for(int j = 0; j < rules.length; j++) {
			length =  __parse__regex(rules[j].regex,parent,weight);
			if(weight > 0) {
				if(length > result) {
					result = length;
				}
			} else {
				if(length < result) {
					result = length;
				}			
			}
		}
	}
	
	return result;
}

function __parse__regex(regex,parent,weight) {
	var length = 0;
	var last = 0;
	
	if(parent == "") {
	  __parse__regex_i = 0;
	}
	
	for(; __parse__regex_i < regex.length(); __parse__regex_i++) {
		switch(regex.charAt(__parse__regex_i)) {
			case '\\':
				__parse__regex_i++;
				length += last;
				last = 1;
				break;
			case '|':
				__parse__regex_i++;
				if(parent == '|') {
					return length + last;
				} else {
					int aux = length + last;
					last = 0;
					length = parseregex(regex, '|', weight);
					if(weight == 0) {
						if(aux < length) {
							length = aux;
						} 								
					} else {
						if(aux > length) {
							length = aux;
						} 								
					}
				}
				break;
			case ')':
				__parse__regex_i++;
				return length + last;
			case ']':
				__parse__regex_i++;
				return 1;
			case '[':
				last = parseregex(regex, '[', weight);
			case '{':
				int index = regex.indexOf("}", __parse__regex_i);
				if(index >= 0) {
					String range = regex.substring(__parse__regex_i, index);
					__parse__regex_i = index;
					index = range.indexOf(',');
					if(index >= 0) {
						if(weight == 0) {
							last *= Integer.parseInt(range.split(",")[0]);
						} else {
							last *= Integer.parseInt(range.split(",")[1]);								
						}
					} else {
						last *= Integer.parseInt(range);
					}
				}
				break;
			case '(':
				last = parseregex(regex, '(', weight);
				break;
			case '*':
				last *= weight;
				break;
			case '+':
				last *= weight > 0 ? weight : 1;
				break;
			case '?':
				last *= weight > 0 ? 1 : 0;
				break;
			default:
				length += last;
				last = 1;
				break;
		}
	}
	length += last;
	
	return length;
}


function __parse_input(input, grammars, slot, mode) {
	var length = 0;
	var match;
	var weight = 0;
	var result = "";
	
	for(int i = 0; i < grammars.length; i++) {
	    var rules = grammars[i].rules;
		for(int j = 0; j < rules.length; j++) {
			if(rules[j].type.indexOf(mode) >= 0
				|| (rules[j].type == "dtmf voice" 
					&& mode == ""))  {
				match = input.match(rules[j].regex);
				if(match) {
					if(match[0].length > length || grammars[i].weight > weight) {
						weight = grammars[i].weight;
						length = match[0].length;
						if(rules[j].value) {
							result = rules[j].value;
						} else {
							result = match[0];
						}
					}
				}	
			}
		}
	}
	
	return result;
}