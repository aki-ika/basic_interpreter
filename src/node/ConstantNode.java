package node;

import lexer.Environment;
import lexer.LexicalUnit;
import lexer.Value;

public class ConstantNode extends Node {
	private LexicalUnit lu;

	public ConstantNode(Environment env) {
		super(env);
	}

	//-1用
	public ConstantNode(LexicalUnit lu) {
		this.lu = lu;
		this.type = ExprType.INT;
	}

	@Override
	public void parse() throws Exception {
		lu = peek();

		switch (peek().getValue().getType()){
		case INTEGER:
			type = ExprType.INT;
			break;
		case DOUBLE:
			type = ExprType.DOUBLE;
			break;
		case STRING:
			type = ExprType.STRING;
			break;
		case BOOL:
			type = ExprType.BOOL;
			break;
		case VOID:
			break;
		default:
			break;
		}
	}

	@Override
	public String toString() {
		return lu.getValue().getSValue() ;	
	}


	@Override
	public Value getValue() {
		return lu.getValue();
	}
}
