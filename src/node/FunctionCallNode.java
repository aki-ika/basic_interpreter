package node;

import lexer.Environment;
import lexer.LexicalType;
import lexer.LexicalUnit;
import lexer.Symbol;
import lexer.Value;

public class FunctionCallNode extends Node {
	private String func; //関数
	private ExprListNode argument; //引数
	
	public FunctionCallNode(Environment env) {
		super(env);
	}

	/*
	 * <call_func>	::=　<NAME> <LP> <expr_list> <RP>
	 */
	
	@Override
	public void parse() throws Exception {
		LexicalUnit lu = peek();
		// 関数名
		if(lu.getType() == LexicalType.NAME) {
			func = get().getValue().getSValue();
		}else {
			error("関数名がない");
		}

		// LP ( <- これのこと
		if(peek().getType() == LexicalType.LP){
			get();
		}else{
			error("FunctionCallNodeに ( がありません peek() = " + peek());
		}

		//引数
		if(peek().getType() == LexicalType.RP) { 
			//引数がないなら何もしない
		}else {
			// 引数があるなら引数を取りにいく
			argument = (ExprListNode) peek_handle(Symbol.expr_list);
			if(argument == null) {
				error("FunctionCallNode 引数がありません");
			}
		}

		// RP ) <- これのこと
		if(peek().getType() == LexicalType.RP) {
			get(); //ここで字句をとる
		}else {
			error("FunctionCallNode RP ) がみつかりません peek =  " + peek());
		}
	}

	@Override
	public String toString() {
		if(argument == null) {
			return func + "]";
		}
		return func + "(" + argument + ")]";
	}

	//関数呼び出しを実行する
	@Override
	public Value getValue() throws Exception {
		return env.getFunction(func).invoke(argument);
	}
}
