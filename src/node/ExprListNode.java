package node;

import java.util.ArrayList;
import java.util.List;

import lexer.Environment;
import lexer.LexicalType;
import lexer.Symbol;
import lexer.Value;

public class ExprListNode extends Node {
 private	List<Node> body = new ArrayList<>(); 

	public ExprListNode(Environment env) {
		super(env);
	}


	/*
	 * <expr_list>  ::=
	 * 	<expr>
	 * 	| <expr_list> <COMMA> <expr>
	 */
	@Override
	public void parse() throws Exception {
		Node expr = null;
		while(true) {
			expr = peek_handle(Symbol.expr);
			body.add(expr);
			if(peek().getType() == LexicalType.COMMA) {
				get();
			}else {
				break;
			}
		}
	}

	@Override
	public String toString() {
		return body.toString() ;
	}
	
	//bodyがListなんでi番目でgetValueしたい
	public Value getValue(int i) throws Exception {
			return body.get(i).getValue();
	}

	//List<Node> bodyの長さ(PrintFunctionでつかう)
	public int size() {
		return body.size();
	}
}
