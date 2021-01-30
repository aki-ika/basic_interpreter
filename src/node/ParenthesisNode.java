package node;

import lexer.Environment;

public class ParenthesisNode extends Node {
	
	public ParenthesisNode(Environment env) {
		super(env);
	}

	@Override
	public void parse() throws Exception {
		this.type = ExprType.LP;
	}

}
