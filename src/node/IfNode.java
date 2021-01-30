package node;

import java.util.ArrayList;
import java.util.List;

import lexer.Environment;
import lexer.LexicalType;
import lexer.Symbol;
import lexer.Value;

public class IfNode extends Node {
	private Node cond;
	private 	Node statement;
	private Node elsestmt;
	private 	boolean elseIfFlag;
	private Node elseifstmt;
	private 	Node condelseif;
	private 	List<Node> elseifstmtlist = new ArrayList<>();
	private 	List<Node> condelseiflist = new ArrayList<>();;

	public IfNode(Environment env) {
		super(env);
	}

	/*
	 * <if_prefix>  ::=
	 * 	<IF> <cond> <THEN>
	 * <else_block>  ::=
	 * 	<else_if_block>
	 * 	| <else_if_block> <ELSE> <NL> <stmt_list>
	 * 
	 * <else_if_block>   ::=
	 * 	φ
	 * 	｜<else_if_block> <ELSEIF> <cond> <THEN> <NL> <stmt_list>
	 */


	// IFのプログラムの例
	//	IF d > 0 THEN
	//	PRINT ("HELLO")
	//	ELSEIF d = 0 THEN
	//	PRINT ("WORLD")
	//	ELSEIF d < 0 THEN
	//	PRINT ("HELLO WORLD")
	//	ELSE
	//	PRINT ("dが条件を満たしません")
	//	ENDIF
	
	@Override
	public void parse() throws Exception {
		if(peek().getType() == LexicalType.IF) { //IFをとる
			get();
		}else {
			error("IfNode: IFがありません peek() =" + peek());
		}

		cond = handle(Symbol.cond); //<cond>をとる
		if(peek().getType() == LexicalType.THEN) {
			get();
		}else {
			error("IfNode: THENがありません peek()" + peek());
		}
		while(peek().getType() == LexicalType.NL) {
			//改行をとる
			get();
		}
		statement = handle(Symbol.stmt_list);

		//IF-ELSEIF
		while(true) {
			if(peek().getType() == LexicalType.ELSEIF) {
				elseIfFlag = true;
				get();
				condelseif = handle(Symbol.cond); //<cond>をとる
				condelseiflist.add(condelseif);
				if(peek().getType() == LexicalType.THEN) {
					get();
				}else {
					error("IfNode: THENがありません peek()" + peek());
				}
				while(peek().getType() == LexicalType.NL) {
					//改行をとる
					get();
				}
				elseifstmt = handle(Symbol.stmt_list);
				elseifstmtlist.add(elseifstmt);
			}
			if(peek().getType() == LexicalType.NL) {
				while(peek().getType() == LexicalType.NL) {
					//改行をとる
					get();
				}
			}
			if(peek().getType() != LexicalType.ELSEIF) {
				break;
			}
		}
		//IF-(ELSEIF-)ELSE
		if(peek().getType() == LexicalType.ELSE) {
			get();
			if(peek().getType() == LexicalType.NL) {
				while(peek().getType() == LexicalType.NL) {
					//改行をとる
					get();
				}
			}
			elsestmt = handle(Symbol.stmt_list);
		}else {
			
		}
		if(peek().getType() == LexicalType.ENDIF) {
			get();
		}else {
			error("ENDIFがありません peek() = " + peek());
		}
	}


	@Override
	public String toString() {
		String body = "IF " + cond + " THEN " + statement ; 
		if(elseIfFlag) {
			for(int i = 0; i < elseifstmtlist.size(); i++) {
				body += " ELSEIF " + condelseiflist.get(i) + " "+ elseifstmtlist.get(i);
			}
		}

		if(elsestmt != null) {
			body += " ELSE " + elsestmt;
		}

		return body + " ENDIF";
	}

	@Override
	public Value getValue() throws Exception {
		//IF
		if(cond.getValue().getBValue() == true) {
			statement.getValue();
		}
		
		//ELSEIF
		for(int i = 0; i < condelseiflist.size(); i++) {
			if(condelseiflist.get(i).getValue().getBValue() == true && cond.getValue().getBValue() == false) {
				elseifstmtlist.get(i).getValue();
				return null;
			}
		}
		
		//ELSE
		if(elsestmt != null && cond.getValue().getBValue() == false) {
			elsestmt.getValue();
		}
		return null;
	}
}
