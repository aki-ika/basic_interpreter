package interpreter;

public enum LexicalType {
	LITERAL(""),		// 文字列定数　（例：　“文字列”）
	INTVAL(""),		// 整数定数	（例：　３）
	DOUBLEVAL(""),		// 小数点定数	（例：　１．２）
	NAME(""),		// 変数		（例：　i）
	IF("IF"),		// IF
	THEN("THEN"),		// THEN
	ELSE("ELSE"),		// ELSE
	ENDIF("ENDIF"),		// ENDIF
	FOR("FOR"),		// FOR
	FORALL("FORALL"),	// FORALL
	NEXT("NEXT")	,	// NEXT
	EQ("="),		// =
	LT("<"),		// <
	GT(">"),		// >
	LE("<="),		// <=, =<
	GE(">="),		// >=, =>
	NE("<>"),		// <>
	FUNC("FUNCTION"),	// SUB
	DIM("DIM"),		// DIM
	AS("AS"),		// AS
	END("END"),		// END
	NL("\n"),		// 改行
	DOT("."),		// .
	WHILE("WHILE"),		// WHILE
	DO("DO"),		// DO
	UNTIL("UNTIL"),		// UNTIL
	ADD("+"),		// +
	SUB("-"),		// -
	MUL("*"),		// *
	DIV("/"),		// /
	MOD("%"),		// MOD
	LP("("),		// (
	RP(")"),		// )
	COMMA(","),		// ,
	LOOP("LOOP"),		// LOOP
	TO("TO"),		// TO
	WEND("WEND"),		// WEND
	EOF(""),  		// end of file
	REM("REM"), 		// comment out
	IN("IN"),		// FORALL x IN p .. q  のIN
	RANGE("..")
	;
	
	String notation;
	
	public String getNotation() {
		return notation;
	}
	
	LexicalType(String notation) {
		this.notation = notation;
	}
}
