package interpreter;

public abstract class Value {
	// 実装すべきコンストラクタ
	public Value(String s) {};
	public Value(int i) {};
	public Value(double d) {};
	public Value(boolean b) {};
	public Value(String s, ValueType t) {};
	public abstract String getSValue();
	// ストリング型で値を取り出す。必要があれば、型変換を行う。
	public abstract int getIValue();
	// 整数型で値を取り出す。必要があれば、型変換を行う。
	public abstract double getDValue();
	// 小数点型で値を取り出す。必要があれば、型変換を行う。
	public abstract boolean getBValue();
	// 論理型で値を取り出す。必要があれば、型変換を行う。
	public abstract ValueType getType();
}
