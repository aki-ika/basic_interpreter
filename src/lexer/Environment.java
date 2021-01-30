package lexer;


import java.util.HashMap;
import java.util.Map;

import func.Function;
import func.PrintFunction;

public class Environment {
	Map <String, Function> library;
	Map <String, Variable> var_table;
	LexicalAnalyzer input;

	public Environment(LexicalAnalyzer my_input) {
		input = my_input;
		library = new HashMap<>(); 
		library.put("PRINT", new PrintFunction() );
		//library.put("MAX" , new MaxFunction());
		var_table = new HashMap<>();
	}

	public LexicalAnalyzer getInput() {
		return input;
	}

	public Function getFunction(String fname) {
		return (Function) library.get(fname);
	}

	public Variable getVariable(String vname) throws Exception {
		Variable v;
		v = (Variable) var_table.get(vname);
		if (v == null) {
			v = new Variable(vname);
			var_table.put(vname, v);
		}
		return v;
	}		
}