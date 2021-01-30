package node;

import lexer.Environment;
import lexer.LexicalType;
import lexer.Symbol;
import lexer.Value;
import lexer.ValueImpl;
import lexer.ValueType;

public class ForNode extends Node {

	private Node initialValue;
	private Node finalValue;
	private Node process;
	private Value valName;
	private boolean forAllFlag;
	private Node fromConstant;
	private Node toConstant;
	private Node declaredVariable;
	/* 	| <FOR> <subst> <TO> <INTVAL> <NL> <stmt_list> <NEXT> <NAME> */

	public ForNode(Environment env) {
		super(env);
	}

	//For 変数名 = 初期値 to 最終値  処理 Next 変数名
	//たとえば、 For i = 0 TO 3 処理 Next i みたいに書く
	// Forall a IN 1..10 PRINT (a) みたいに書く 
	//	ググった感じではNext aは必要なさそう

	public void parse() throws Exception {
		if(peek().getType() == LexicalType.FORALL) {
			get(); //FORALLをとる
			forAllFlag = true;

			declaredVariable = handle(Symbol.var);
			get(); //変数をとる

			if(peek().getType() == LexicalType.IN) {
				get();	//INをとる
			}

			fromConstant = handle(Symbol.constant);
			get(); //レンジの開始値をとる

			//レンジの..をとる
			if(peek().getType() == LexicalType.RANGE) {
				get();
			}

			toConstant = handle(Symbol.constant);
			get(); //レンジの終了値をとる

			while(peek().getType() == LexicalType.NL) {
				//改行を読みとばす
				get();
			}

			process = handle(Symbol.stmt_list); //処理

		}else {
			get(); //FORをとる

			initialValue = handle(Symbol.subst); //初期値

			if(peek().getType() == LexicalType.TO) {
				get(); //TOをとる
			}else {
				error("TOがありません ");
			}

			finalValue = handle(Symbol.constant);  //最終値
			get(); 

			while(peek().getType() == LexicalType.NL) //改行を読みとばす
				get();

			process = handle(Symbol.stmt_list); //処理

			if(peek().getType() == LexicalType.NEXT) {
				get();
			}else{
				error("処理部のあとはNEXTがきます");
			}

			if(peek().getType() == LexicalType.NAME) { //変数名
				valName = get().getValue();
			}else {
				error("Nextのあとは変数名がきます");
			}
		}
	}

	@Override
	public String toString() {
		if(forAllFlag) return "FORALL " + declaredVariable.toString() + " IN " + fromConstant.toString() + ".." + toConstant.toString() + process.toString() ;
		return "FOR " + initialValue.toString() + " TO "
		+ finalValue.toString() + " " + process.toString() 
		+ "NEXT " + valName.getSValue() ;
	}

	@Override
	public Value getValue() throws Exception {
		if(forAllFlag) {
			env.getVariable(declaredVariable.toString()).setValue(fromConstant.getValue()); //宣言した変数を初期値で初期化
			while(true) {
				if(env.getVariable(declaredVariable.toString()).getValue().getIValue() > toConstant.getValue().getIValue()) {
					break;
				}
				process.getValue();
				int counter = env.getVariable(declaredVariable.toString()).getValue().getIValue() + 1; //カウンタを用意
				env.getVariable(declaredVariable.toString()).setValue(new ValueImpl(String.valueOf(counter), ValueType.INTEGER)); //値を更新する			}
			}
			return null;
		}	else {
			initialValue.getValue();

			while(true) {
				//FOR a = 1 TO n , a > n でbreak
				if(env.getVariable(valName.getSValue()).getValue().getIValue() > finalValue.getValue().getIValue()) {
					break;
				}

				process.getValue(); //実行したい

				int counter = env.getVariable(valName.getSValue()).getValue().getIValue() + 1; //カウンタを用意
				env.getVariable(valName.getSValue()).setValue(new ValueImpl(String.valueOf(counter), ValueType.INTEGER)); //値を更新する
			}
			return null;
		}
	}
}
