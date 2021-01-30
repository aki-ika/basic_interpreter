package node;

import lexer.Environment;
import lexer.LexicalType;
import lexer.Value;

public class VariableNode extends Node {
	private String name;

	public VariableNode(Environment env){
		super(env);
		type = ExprType.VARIABLE;
	}

	@Override
	public void parse() throws Exception {
		//配列で作った変数の場合
		if(peek().getType() == LexicalType.NAME 
				&& peek2().getType() == LexicalType.LP) {
			String arrayName = get().getValue().getSValue();
			arrayName += get().getValue().getSValue();
			arrayName += get().getValue().getSValue();
			arrayName += get().getValue().getSValue();
			this.name = arrayName;
		}else {
			this.name = peek().getValue().getSValue();
		}
	}

	@Override
	public String toString() {
		return  name;
	}

	@Override
	public Value getValue() throws Exception {
		return env.getVariable(name).getValue();
	}
}