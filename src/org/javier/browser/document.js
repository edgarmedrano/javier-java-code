function __grammar_length(grammars, weight, mode) {
	var length;
	var result = weight > 0 ? 0 : 32767;
	
	for(var i = 0; i < grammars.length; i++) {
	    var rules = grammars[i].rules;
		for(var j = 0; j < rules.length; j++) {
			if(rules[j].type.indexOf(mode) >= 0
					|| rules[j].type == "any" 
					|| mode == "any"
					|| rules[j].type == "" 
					|| mode == "")  {
				var ruleString = rules[j].regexp.toString();
				ruleString = ruleString.substring(1,ruleString.lastIndexOf("/"));
				length =  __parse__regexp(ruleString,"",weight);
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
	}
	
	return result;
}

function __parse__regexp(regexp,parent,weight) {
	var length = 0;
	var last = 0;
	
	if(parent == "") {
	  __parse__regex_i = 0;
	}
	
	for(; __parse__regex_i < regexp.length; __parse__regex_i++) {
		switch(regexp.charAt(__parse__regex_i)) {
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
					var aux = length + last;
					last = 0;
					length = __parse__regexp(regexp, '|', weight);
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
				last = __parse__regexp(regexp, '[', weight);
			case '{':
				var index = regexp.indexOf("}", __parse__regex_i);
				if(index >= 0) {
					var range = regexp.substring(__parse__regex_i, index);
					__parse__regex_i = index;
					index = range.indexOf(',');
					if(index >= 0) {
						if(weight == 0) {
							last *= parseInt(range.split(",")[0]);
						} else {
							last *= parseInt(range.split(",")[1]);								
						}
					} else {
						last *= parseInt(range);
					}
				}
				break;
			case '(':
				last = __parse__regexp(regexp, '(', weight);
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
	var result = new Object();
	result.match = false;
	
	for(var i = 0; i < grammars.length; i++) {
	    var rules = grammars[i].rules;
		for(var j = 0; j < rules.length; j++) {
			if(rules[j].type.indexOf(mode) >= 0
					|| rules[j].type == "any" 
					|| mode == "any"
					|| rules[j].type == "" 
					|| mode == "")  {
				match = input.match(rules[j].regexp);
				if(match) {
					if(match[0].length > length || grammars[i].weight > weight) {
						weight = grammars[i].weight;
						length = match[0].length;
						result.next = grammars[i].next;
						result.event = grammars[i].event;
						result.expr = grammars[i].expr;
						result.eventexpr = grammars[i].eventexpr;
						result.match = true;
						if(rules[j].value) {
							result.value = rules[j].value;
						} else {
							result.value = match[0];
						}
					}
				}	
			}
		}
	}
	
	return result;
}
