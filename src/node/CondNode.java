package node;

import java.util.EnumSet;
import java.util.Set;

import lexer.Environment;
import lexer.LexicalType;
import lexer.LexicalUnit;
import lexer.Symbol;
import lexer.Value;
import lexer.ValueImpl;
import lexer.ValueType;

public class CondNode extends Node {

	private Node com1;
	private Node com2;
	private LexicalUnit compare;

	//比較演算子をSetでもっておく,変更不可能
	private static final Set<LexicalType> CONDSET = 
			EnumSet.of(
					LexicalType.EQ,
					LexicalType.GT,
					LexicalType.LT,
					LexicalType.GE,
					LexicalType.LE,
					LexicalType.NE);

	//コンストラクタ
	public CondNode(Environment env) {
		super(env);
	}

	/*
	 * <cond>	 ::=
	 * 　<expr> <EQ> <expr>
	 * 	| <expr> <GT> <expr>
	 * 	| <expr> <LT> <expr>
	 * 	| <expr> <GE> <expr>
	 * 	| <expr> <LE> <expr>
	 * 	| <expr> <NE> <expr>
	 */

	@Override
	public void parse() throws Exception {
		com1 = handle(Symbol.expr); 

		if(CONDSET.contains(peek().getType())) { //比較演算子なら字句をとる
			compare = get();
		} else {
			error("CondNode 比較演算子がみつかりません  peek() = " + peek());
		}

		com2 = handle(Symbol.expr);
	}

	@Override
	public String toString() {
		switch(compare.getType()) {
		case EQ:
			return com1 + " = " + com2;
		case GT:
			return com1 + " > " + com2;
		case LT:
			return com1 + " < " + com2;
		case GE:
			return com1 + " >= " + com2;
		case LE:
			return com1 + " <= " + com2;
		case NE:
			return com1 + " != " + com2;
		default:
			break;
		}
		return null;
	}

	//真偽値をValueで返す
	@Override
	public Value getValue() throws Exception{
		Value val1 = com1.getValue();
		Value val2 = com2.getValue();

		//比較演算子のLexicalTypeを見て、比較演算をおこなう
		if (compare.getType() == LexicalType.EQ){
			return new ValueImpl(String.valueOf(val1.getIValue() == val2.getIValue()),ValueType.BOOL);
		}else if (compare.getType() == LexicalType.GT){
			return new ValueImpl(String.valueOf(val1.getIValue() > val2.getIValue()),ValueType.BOOL);
		}else if (compare.getType() == LexicalType.LT){
			return new ValueImpl(String.valueOf(val1.getIValue() < val2.getIValue()),ValueType.BOOL);
		}else if (compare.getType() == LexicalType.GE){
			return new ValueImpl(String.valueOf(val1.getIValue() <= val2.getIValue()),ValueType.BOOL);
		}else if (compare.getType() == LexicalType.LE){
			return new ValueImpl(String.valueOf(val1.getIValue() <= val2.getIValue()),ValueType.BOOL);
		}else if (compare.getType() == LexicalType.NE){ // NEの<> は != 
			return new ValueImpl(String.valueOf(val1.getIValue() != val2.getIValue()),ValueType.BOOL);
		}else {
			error ("条件判断できません");
		}
		return null;
	}
}

