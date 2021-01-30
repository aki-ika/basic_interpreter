package func;

import lexer.Environment;
import lexer.Value;
import node.ExprListNode;

public class Function {
	Environment env;

	public Function() {
	}
	
	public Function(Environment env) {
		this.env = env;
	}

	public Value invoke(ExprListNode argument) throws Exception {
		return null;        
	}
	
}
