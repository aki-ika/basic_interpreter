package node;

import lexer.Environment;
import lexer.LexicalType;
import lexer.Symbol;
import lexer.Value;
import lexer.ValueImpl;
import lexer.ValueType;

public class AssignmentNode extends Node {
	private Node leftvar;
	private Node expr;
	private boolean dimensionFlag;
	private String array;
	private int arraySize;
	private boolean doInitializeFlag;
	private ExprListNode exprList;

	public AssignmentNode(Environment env) {
		super(env);
	}

	// <subst> ::=	<leftvar> <EQ> <expr>

	@Override
	public void parse() throws Exception {	
		if(peek().getType() == LexicalType.DIM) {
			get();
			dimensionFlag = true;
			if(peek2().getType() == LexicalType.LP) {
				array = get().getValue().getSValue();
			}
			get(); // LP
			arraySize = get().getValue().getIValue();
			if(peek().getType() == LexicalType.RP) {
				get();
			}
			if(peek().getType() == LexicalType.EQ) {
				get();
				doInitializeFlag = true;
			}

			if(doInitializeFlag) {
				if(peek().getType() == LexicalType.LP) {
					get();
				}

				exprList = (ExprListNode) handle(Symbol.expr_list);
				if(peek().getType() == LexicalType.RP) {
					get();
				}
			}
		}else {
			leftvar =  handle(Symbol.leftvar);
			if(leftvar == null) { //変数じゃないなら何も入ってない
				error("左辺が変数ではありません");
			}else {
				get(); //字句をとる
			}

			if(peek().getType() == LexicalType.EQ) { //EQかどうかをみる
				get(); //字句をとる
			} else {
				error("AssignmentNode: イコール(=)ではなく " +  peek() + "です");
			}

			expr = peek_handle(Symbol.expr); //式かどうか見にいく
			if(expr == null) { //式じゃないなら何もはいってない
				error("右辺として不正です");
			}
		}
	}

	@Override
	public String toString() {
		if(dimensionFlag) {
			if(doInitializeFlag) {
				return "dimension: " + array + "[" + arraySize + "]" +  "\n =" + exprList.toString() ;
			}else {
				return "dimension: " + array + "(" + arraySize + ")" ;
			}
		}

		return leftvar.toString() + " = " + expr.toString();
	}

	//変数への値の代入
	public Value getValue() throws Exception {
		//配列
		if(dimensionFlag) {
			//初期化する場合
			if(doInitializeFlag) {
				for(int i = 0; i < arraySize; i++) {
					env.getVariable(array + "(" + i + ")").setValue(new ValueImpl(String.valueOf(exprList.getValue(i).getIValue()), ValueType.INTEGER));
				}
				return null;
			}else {
				for(int i = 0; i < arraySize; i++) {
					//0で初期化
					env.getVariable(array + "(" + i + ")").setValue(new ValueImpl(String.valueOf(0), ValueType.INTEGER));
				}
				return null;
			}
		}else {
			env.getVariable(leftvar.toString()).setValue(expr.getValue());
			return null;
		}
	}
}