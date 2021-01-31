package node;

import lexer.Environment;
import lexer.LexicalUnit;

public class EndNode extends Node {
	private LexicalUnit end;
	
	public EndNode(Environment env) {
		super(env);
	}

	@Override
	public void parse() throws Exception {
		end = get();
	}

	@Override
	public String toString() {
		return end.toString();
	}
}