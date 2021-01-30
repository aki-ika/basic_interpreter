package node;

import lexer.Environment;
import lexer.LexicalType;
import lexer.Symbol;
import lexer.Value;

public class StatementNode extends Node {
	private Node body;

	public StatementNode(Environment env) {
		super(env);
	}

	/* 
	 * 	 <stmt>  ::=
	 * 		<subst>
	 * 		| <call_sub>
	 * 		| <FOR> <subst> <TO> <INTVAL> <NL> <stmt_list> <NEXT> <NAME>
	 * 		| <FORALL>	<NAME> <IN> <INTVAL> <DOT> <DOT> <INTVAL> <NL> <stmt_list>
	 * 		| <END>
	 */

	@Override
	public void parse() throws Exception {
		if(peek2().getType() == LexicalType.EQ || peek().getType() == (LexicalType.DIM)) { 		//二字句先がイコールなら<subst>
			body = handle(Symbol.subst);
		}else if(peek2().getType() == LexicalType.LP) { //そうじゃないなら<call_sub>
			body = handle(Symbol.call_func);
		}else if(peek().getType() == LexicalType.FOR || peek().getType() == LexicalType.FORALL) { //<subst>でも<call_sub>でもないなら<FOR>か <FORALL>
			body = handle(Symbol.forloop);
		}else  { //上のどれでもないなら<END>
			body = handle(Symbol.end);
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
