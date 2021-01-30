package node;

import lexer.Environment;
import lexer.Symbol;
import lexer.Value;

public class BlockNode extends Node {
	private Node body;

	public BlockNode(Environment env) {
		super(env);
	}

	/*
	 * 
	 * <block> ::=
	 * 	<if_prefix> <stmt> <NL>
	 * 	| <if_prefix> <stmt> <ELSE> <stmt> <NL>
	 * 	| <if_prefix> <NL> <stmt_list> <else_block> <ENDIF> <NL>
	 * 	| <WHILE> <cond> <NL> <stmt_list> <WEND> <NL>
	 * 	| <DO> <WHILE> <cond> <NL> <stmt_list> <LOOP> <NL>
	 * 	| <DO> <UNTIL> <cond> <NL> <stmt_list> <LOOP> <NL>
	 * 	| <DO> <NL> <stmt_list> <LOOP> <WHILE> <cond> <NL>
	 * 	| <DO> <NL> <stmt_list> <LOOP> <UNTIL> <cond> <NL>
	 */
	
	@Override
	public void parse() throws Exception {
		body = peek_handle(Symbol.loop);
		if(body == null) {
			body = peek_handle(Symbol.if_prefix);
		}
	}

	@Override
	public String toString() {
		return body.toString() ;
	}

	@Override
	public Value getValue() throws Exception {
		return body.getValue();
	}
}
