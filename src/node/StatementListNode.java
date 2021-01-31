package node;

import java.util.ArrayList;
import java.util.List;

import lexer.Environment;
import lexer.LexicalType;
import lexer.Symbol;
import lexer.Value;

public class StatementListNode extends Node {

	private List<Node> body = new ArrayList<>();
	
	public StatementListNode(Environment env) {
		super(env);
	}
	/*
	 * <stmt_list>　::=
	 *  	<stmt>
	 *   	| <stmt_list> <NL> <stmt>
	 *    | <block>
	 *    | <stmt_list> <block>
	 */
	@Override
	public void parse( ) throws Exception {
		while(true) {
			//プログラム中のコメントを読みとばす
			if(peek().getType() == LexicalType.REM) {
				while(peek().getType() != LexicalType.NL) {
					if(peek().getType() == LexicalType.EOF) {
						break;
					}
					get();
				}
			}
			
			LexicalType ft = peek().getType();
			if (ft == LexicalType.EOF) break; //終了
			Node elm = peek_handle(Symbol.stmt); // stmtかもしれない。違えばnullを返す
			// 確定でない場合は、handleの代わりにpeek_handleを用いる
			if (elm == null) elm = peek_handle(Symbol.block); // あるいは、blockかもしれない
			if (elm != null) {
				body.add(elm); // 処理結果は、Listに保存しておく
				continue;
			}
			if (see(LexicalType.NL)) {
				continue;// NLだったら読み飛ばす
			}
			break; // stmtでもblockでもなかったら終わり
		}
	}

	@Override
	public String toString() {
		return body.toString();
	}

	//プログラムを実行する
	public Value getValue() throws Exception {
		for(int i = 0; i < body.size(); i++) {
			body.get(i).getValue();
		}
		return null;
	}
}
