package node;

import lexer.Environment;
import lexer.LexicalType;
import lexer.Symbol;
import lexer.Value;

public class ProgramNode extends Node {
	private Node body;

	public ProgramNode(Environment env) {
		super(env);
	}

	@Override
	public void parse( ) throws Exception {
		//プログラムまでのコメントを削除
		if(peek().getType() == LexicalType.REM) {
			while(peek().getType() != LexicalType.NL) {
				get();
			}
		}
		
		//プログラムまでの改行の削除
		if(peek().getType() == LexicalType.NL) {
			while(peek().getType() == LexicalType.NL) {
				get();
			}
		}
		
		body = handle(Symbol.stmt_list); //<program>  ::=	<stmt_list> なのでこれだけ
	}

	@Override
	public String toString() {
		return body.toString() ;
	}

	public Value getValue() throws Exception {
		return body.getValue();
	}
}
