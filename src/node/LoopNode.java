package node;

import lexer.Environment;
import lexer.LexicalType;
import lexer.Symbol;
import lexer.Value;

public class LoopNode extends Node {

	private Node cond;
	private Node stmt_list;
	private boolean whileStatementFlag;
	private boolean doWhile;
	private boolean doUntil;
	private boolean doStmtWhile;

	public LoopNode(Environment env) {
		super(env);
	}

	/*
	 *		| <WHILE> <cond> <NL> <stmt_list> <WEND> <NL>
	 * 	| <DO> <WHILE> <cond> <NL> <stmt_list> <LOOP> <NL>
	 * 	| <DO> <UNTIL> <cond> <NL> <stmt_list> <LOOP> <NL>
	 * 	| <DO> <NL> <stmt_list> <LOOP> <WHILE> <cond> <NL>
	 * 	| <DO> <NL> <stmt_list> <LOOP> <UNTIL> <cond> <NL>
	 */
	@Override
	public void parse() throws Exception {
		//DOかWHILEか？
		if(peek().getType() == LexicalType.WHILE) { //WHILEから作業する
			whileStatementFlag = true; //while文のフラグをたてておく
			get(); //WHILEをとる

			cond = handle(Symbol.cond); //条件文

			//NLの読みとばし
			if(peek().getType() == LexicalType.NL) {
				while(see(LexicalType.NL)) {
				}
			}

			stmt_list = handle(Symbol.stmt_list); //実行文


			//WHILE - WENDなので最後にWENDをとる必要がある
			if(peek().getType() == LexicalType.WEND) {
				get(); //WENDをとる
			}
		}

		if(peek().getType() == LexicalType.DO) { //一方のDO
			whileStatementFlag = false; //while文のフラグをfalseに
			get(); //DOをとる
			if(peek().getType() == LexicalType.WHILE) { //DO-WHILE
				//		| <DO> <WHILE> <cond> <NL> <stmt_list> <LOOP> <NL>
				doWhile = true;
				get(); //WHILEをとる
				cond = handle(Symbol.cond); //<cond>
				while(peek().getType() == LexicalType.NL) {
					get(); //改行の読みとばし
				}

				stmt_list = handle(Symbol.stmt_list); //<stmt_list>
				if(peek().getType() == LexicalType.UNTIL) {
					get();
				}
			}else if(peek().getType() == LexicalType.UNTIL) { //DO-UNTIL
				//		| <DO> <UNTIL> <cond> <NL> <stmt_list> <LOOP> <NL>

				doUntil = true;
				get();

				cond = handle(Symbol.cond); //<cond>
				if(peek().getType() == LexicalType.NL) {
					while(see(LexicalType.NL)) {
					}
				}

				stmt_list = handle(Symbol.stmt_list); //<stmt_list>
				if(see(LexicalType.LOOP)) {
					get();
				}
			}else {
				//		| <DO> <NL> <stmt_list> <LOOP> <WHILE> <cond> <NL>
				//		| <DO> <NL> <stmt_list> <LOOP> <UNTIL> <cond> <NL>
				if(peek().getType() == LexicalType.NL) {
					while(see(LexicalType.NL)) {
					}
				}

				stmt_list = handle(Symbol.stmt_list);
				if(peek().getType() == LexicalType.LOOP) {
					get();
				}
				if(peek().getType() == LexicalType.NL) {
					while(see(LexicalType.NL)) {
					}
				}
				if(peek().getType() == LexicalType.WHILE) {
					doStmtWhile = true;
					get(); //WHILEをとる
				}else {
					doStmtWhile = false;
					get(); //UNTILをとる
				}

				cond = handle(Symbol.cond);
			}
		}
	}


	@Override
	public String toString() {
		if(whileStatementFlag) { //WHILE文
			return "WHILE " + cond.toString() + " " + stmt_list.toString() +  " WEND";
		}else { //DO文
			if(doWhile) {
				return "DO WHILE " + cond.toString() + " " + stmt_list.toString() +" LOOP";
			}else if(doUntil) {
				return "DO UNTIL " + cond.toString() + " " + stmt_list.toString() + " LOOP";
			}else if(doStmtWhile) {
				return "DO " + stmt_list.toString() + " LOOP WHILE" + cond.toString() ;
			}else {
				return "DO " + stmt_list.toString() + " LOOP UNTIL" + cond.toString() ;
			}
		}
	}

	public Value getValue() throws Exception {
		if(whileStatementFlag) {
			while(cond.getValue().getBValue() == true) {
				stmt_list.getValue();
			}
		}else	if(doUntil) { //偽の間だけ実行する
			while (cond.getValue().getBValue() == false){
				stmt_list.getValue();
			}
		}else if(doWhile){ //真の間だけ実行する
			while(cond.getValue().getBValue() == true) {
				stmt_list.getValue();
			}
		}else if(doStmtWhile) { //一度は実行する(あとはWhile)
			stmt_list.getValue();
			while(cond.getValue().getBValue() == true) {
				stmt_list.getValue();
			}
		}else {
			stmt_list.getValue();//一度は実行する(あとはUntil)
			while(cond.getValue().getBValue() == false) {
				stmt_list.getValue();
			}
		}
		return null;
	}
}
