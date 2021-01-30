package node;

import lexer.Environment;
import lexer.LexicalType;
import lexer.LexicalUnit;
import lexer.Symbol;
import lexer.Value;

public class Node {
	Environment env;
	ExprType type;	

	/** Creates a new instance of Node */
	public Node() {
	}

	public Node(Environment env) {
		this.env = env;
	}

	public void parse() throws Exception {
	}

	public Value getValue() throws Exception {
		return null;
	}

	protected LexicalUnit get() throws Exception {
		return env.getInput().get();
	}

	protected LexicalUnit peek() throws Exception {
		return env.getInput().peek();
	}

	//peek2()を追加
	protected LexicalUnit peek2() throws Exception {
		return env.getInput().peek2();
	}

	protected Node handle(Symbol symbol) throws Exception {
		LexicalUnit f = peek();
		if (symbol.isFirst(f.getType())) {
			return symbol.handle(env);
		}
		error();
		return null;
	}

	protected Node peek_handle(Symbol symbol) throws Exception {
		LexicalUnit f = peek();
		if (symbol.isFirst(f.getType())) {
			return symbol.handle(env);
		}
		return null;
	}

	protected boolean see(LexicalType t) throws Exception {
		if (peek().getType() == t) {
			get();
			return true;
		}
		else return false;
	}

	protected void expect(LexicalType t) throws Exception {
		if (get().getType() != t) error();
	}

	protected void check(LexicalType t) throws Exception {
		if (peek().getType() != t) error();
	}

	static void error() throws Exception {
		throw new Exception("syntax error");
	}

	static void error(String mes) throws Exception {
		throw new Exception(mes);
	}

	public String toString() {
		return "Node";        
	}

	public ExprType getType() {
		return type;
	}

}