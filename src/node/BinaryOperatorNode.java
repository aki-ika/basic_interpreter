package node;

import lexer.Environment;
import lexer.LexicalType;

public class BinaryOperatorNode extends Node {
	//コンストラクタ
	public BinaryOperatorNode(Environment env) {
		super (env);
	}

	public BinaryOperatorNode(LexicalType mul) {
		//負数の処理用(- 1 * xの*)
		this.type = ExprType.MUL;
	}

	public void parse() throws Exception {
		LexicalType lt = peek().getType() ;
		switch(lt) {
		case ADD:
			this.type = ExprType.ADD;
			break;
		case SUB:
			this.type = ExprType.SUB;
			break;
		case MUL:
			this.type = ExprType.MUL;
			break;
		case DIV:
			this.type = ExprType.DIV;
			break;
		case MOD:
			this.type = ExprType.MOD;
			break;
		default:
			break;
		}
	}

	@Override
	public String toString() {
		String op;
		switch(this.type) {
		case ADD:
			op = "+";
			break;
		case SUB:
			op = "-";
			break;
		case MUL:
			op = "*";
			break;
		case DIV:
			op = "/";
			break;
		case MOD:
			op = "%";
			break;
		default:
			return null;
		}
		return op;
	}

}
