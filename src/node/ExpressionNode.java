package node;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import lexer.Environment;
import lexer.LexicalType;
import lexer.LexicalUnit;
import lexer.Symbol;
import lexer.Value;
import lexer.ValueImpl;
import lexer.ValueType;
import lexer.Variable;

public class ExpressionNode extends Node {
	private Queue<Node> queue = new ArrayDeque<>();// 逆ポーランド記法にした結果を格納していくキュー
	private Deque<Node> stack = new ArrayDeque<>(); //演算子の一時スタック

	//配列用
	private String arrays;
	private boolean arrayFlag;

	//演算子と優先順位をHashMapで持つ,変更不可能
	private  static final Map<ExprType,Integer> PRIORITYOFOPERATOR = new HashMap<>();	{
		PRIORITYOFOPERATOR.put(ExprType.ADD,1);
		PRIORITYOFOPERATOR.put(ExprType.SUB,1);
		PRIORITYOFOPERATOR.put(ExprType.MUL,3);
		PRIORITYOFOPERATOR.put(ExprType.DIV,3);
		PRIORITYOFOPERATOR.put(ExprType.MOD,5);
	}

	//コンストラクタ
	public ExpressionNode(Environment env) {
		super(env);
	}

	@Override
	public void parse() throws Exception {
		if(peek().getType() == LexicalType.NAME 
				&& peek2().getType() == LexicalType.LP) {
			getArray();
		}else {
			while (true) {
				//負数の処理
				if(peek().getType() == LexicalType.SUB) {
					if(peek2().getType() == LexicalType.INTVAL
							|| peek2().getType() == LexicalType.DOUBLEVAL
							|| peek2().getType() == LexicalType.NAME) {
						getNegativeNumber();
					}
				}else {
					//負数と演算子以外
					switch (peek().getType()) {
					case NAME:
						if(peek2().getType() == LexicalType.LP) {
							getFunction();
						}else {
							getVar();
						}
						break;
					case LP:
						getParenthesis();
						break;
					case INTVAL:
					case DOUBLEVAL:
					case LITERAL:
						getConstant();
						break;
					default:
						error();
					}
				}

				if (Symbol.binary.isFirst(peek().getType())) {
					sortOperators();
				} else {
					//スタックが空になるまでキューへ流しこむ
					moveOperatorsFromStackToQueue();
					break;
				}
			}
		}
	}

	private void getArray() throws Exception {
		Node nv = handle(Symbol.var);
		arrayFlag = true;
		arrays = nv.toString();
		queue.add(nv);
	}

	public void moveOperatorsFromStackToQueue() throws Exception {
		while(!stack.isEmpty()){
			queue.add(stack.poll());
		}
	}

	private void getFunction() throws Exception {
		Node cf = handle(Symbol.call_func);
		queue.add(cf);;
	}
	private void getParenthesis() throws Exception {
		Node lp = handle(Symbol.parenthesis);
		stack.add(lp);
		get();

		while(true) {
			if(peek().getType() == LexicalType.SUB) {
				if(peek2().getType() == LexicalType.INTVAL
						|| peek2().getType() == LexicalType.DOUBLEVAL
						|| peek2().getType() == LexicalType.NAME) {
					getNegativeNumber();
				}
			}
			getConstant();
			getVar();

			if(peek().getType() == LexicalType.LP) {
				getParenthesis() ;
			}

			Node operator = peek_handle(Symbol.binary);
			if(operator == null) break;
			if(operator != null) {
				stack.add(operator);
				get();
			}
		}

		moveOperatorsBetweenParenthesesFromStackToQueue();
	}

	public void getNegativeNumber() throws Exception {
		//負数になりうるのは、 -2とか-3.0とか-x ->  -1 と非負整数(の変数)の積へ変換することで負数を実現する
		Node tmp;
		get(); //SUBをとる
		LexicalUnit negative = new LexicalUnit(LexicalType.INTVAL, new ValueImpl(String.valueOf(-1), ValueType.INTEGER));
		queue.add(new ConstantNode(negative)); // -1をキューにいれる
		tmp = peek_handle(Symbol.constant);
		if(tmp == null) tmp = handle(Symbol.var);
		queue.add(tmp); //constantかvarなのでこれをキューにいれる
		queue.add(new BinaryOperatorNode(LexicalType.MUL));  // *をキューにいれる
		get();  //数をとる
	}

	public void getVar() throws Exception {
		if(peek().getType() == LexicalType.NAME) {
			Node vn = peek_handle(Symbol.var);
			if(vn != null) {
				queue.add(vn);
				get();
			}
		}
	}
	public void getConstant() throws Exception {
		if(peek().getType() == LexicalType.INTVAL
				|| peek().getType() == LexicalType.DOUBLEVAL
				|| peek().getType() == LexicalType.LITERAL) {
			Node cn = handle(Symbol.constant);
			if(cn != null) {
				queue.add(cn);
				get();
			}
		}
	}

	public void moveOperatorsBetweenParenthesesFromStackToQueue() throws Exception {
		if(peek().getType() == LexicalType.RP) {
			get();
			while(true) {
				if(stack.peekLast().getType() == ExprType.LP) {
					stack.removeLast();
					break;
				}else {
					queue.add(stack.pollLast());
				}
			}
		}
	}

	public void sortOperators() throws Exception {
		Node op = handle(Symbol.binary);
		get(); //演算子をとる
		while(true){
			if(stack.isEmpty() || stack.peekLast().getType() == ExprType.LP) {
				stack.push(op);
				break;
			}else // スタックのトップの演算子より読みこんだ演算子の優先度が高いならbreak

				if(PRIORITYOFOPERATOR.get(op.getType()) > PRIORITYOFOPERATOR.get(stack.peekFirst().getType())) {
					stack.push(op);
					break;
				}else {
					//キューにスタックのトップの演算子を追加
					queue.add(stack.poll());
				}
		}
	}
	@Override
	public String toString() {
		return queue.toString();
	}

	@Override
	public Value getValue() throws Exception{
		if(arrayFlag) {
			int lp = arrays.indexOf("(");
			int rp = arrays.indexOf(")");
			String tmp = arrays.substring(0,lp+1);
			String tmparg = arrays.substring(lp+1, lp+2);
			int arg = env.getVariable(tmparg).getValue().getIValue();
			tmp += arg;
			tmp += arrays.substring(lp+2,rp+1);
			return new ValueImpl(String.valueOf(env.getVariable(tmp).getValue().getIValue()),ValueType.STRING);
		}else {
			Queue<Node> tmp = new ArrayDeque<>(queue); 

			//文字列は連結しかできないので、add以外はエラーを返す
			if(tmp.peek().getType() == ExprType.STRING){
				String output = "";
				while (tmp.peek() != null){
					switch (tmp.peek().getType()){
					case SUB:
					case MUL:
					case DIV:
					case MOD:
						error("文字列は連結以外できません");
					case ADD:
						tmp.remove();
						continue;
					case STRING:
						output += tmp.poll().getValue().getSValue();
						break;
					default:
						error("文字列演算の対象として不正です");
					}
				}
				return new ValueImpl(output,ValueType.STRING);
			}

			//INT,DOUBLEそれぞれ 足す引くかける割る余り
			else if (tmp.peek().getType() == ExprType.INT){
				Deque<Value> output = new ArrayDeque<>();
				while (tmp.peek() != null){
					switch (tmp.peek().getType()){
					case INT:
						output.push(tmp.poll().getValue());
						break;
					case VARIABLE:
						Variable vn = env.getVariable(tmp.poll().toString());
						output.push(vn.getValue());
						break;
					case ADD:
						tmp.remove();
						int a1 = output.poll().getIValue();
						int a2 = output.poll().getIValue();
						output.push(new ValueImpl(String.valueOf(a2 + a1),ValueType.INTEGER));
						break;
					case SUB:
						tmp.remove();
						int s1 = output.poll().getIValue();
						int s2 = output.poll().getIValue();
						output.push(new ValueImpl(String.valueOf(s2 - s1),ValueType.INTEGER));
						break;
					case MUL:
						tmp.remove();
						int m1 = output.poll().getIValue();
						int m2 = output.poll().getIValue();
						output.push(new ValueImpl(String.valueOf(m2 * m1),ValueType.INTEGER));
						break;
					case DIV:
						tmp.remove();
						int d1 = output.poll().getIValue();
						int d2 = output.poll().getIValue();
						if(d1 == 0) error("0除算はできません");
						output.push(new ValueImpl(String.valueOf(d2 / d1),ValueType.INTEGER));
						break;
					case MOD:
						tmp.remove();
						int mo1 = output.poll().getIValue();
						int mo2 = output.poll().getIValue();
						output.push(new ValueImpl(String.valueOf(mo2 % mo1),ValueType.INTEGER));
						break;
					default:
						error("整数演算の対象として不正です");
					}
				}
				return new ValueImpl(String.valueOf(output.poll().getIValue()),ValueType.INTEGER);
			}
			else if (tmp.peek().getType() == ExprType.DOUBLE){
				Deque<Value> output = new ArrayDeque<>();
				while (tmp.peek() != null){
					switch (tmp.peek().getType()){
					case ADD:
						tmp.remove();
						double a1 = output.poll().getDValue();
						double a2 = output.poll().getDValue();
						output.push(new ValueImpl(String.valueOf(a2 + a1),ValueType.DOUBLE));
						break;
					case SUB:
						tmp.remove();
						double s1 = output.poll().getDValue();
						double s2 = output.poll().getDValue();
						output.push(new ValueImpl(String.valueOf(s2 - s1),ValueType.DOUBLE));
						break;
					case MUL:
						tmp.remove();
						double m1 = output.poll().getDValue();
						double m2 = output.poll().getDValue();
						output.push(new ValueImpl(String.valueOf(m2 * m1),ValueType.DOUBLE));
						break;
					case DIV:
						tmp.remove();
						double d1 = output.poll().getDValue();
						double d2 = output.poll().getDValue();
						if(d1 == 0.0) error("0除算はできません");
						output.push(new ValueImpl(String.valueOf(d2 / d1),ValueType.DOUBLE));
						break;
					case MOD:
						tmp.remove();
						double mo1 = output.poll().getDValue();
						double mo2 = output.poll().getDValue();
						output.push(new ValueImpl(String.valueOf(mo2 % mo1),ValueType.DOUBLE));
						break;
					case DOUBLE:
						output.push(tmp.poll().getValue());
						break;
					case VARIABLE:
						Variable vn = env.getVariable(tmp.poll().toString());
						output.push(vn.getValue());
						break;
					default:
						error("小数演算の対象として不正です");
					}
				}
				return new ValueImpl(String.valueOf(output.poll().getDValue()),ValueType.DOUBLE);
			}

			else if (tmp.peek().getType() == ExprType.VARIABLE){
				Deque<Value> output = new ArrayDeque<>();
				while (tmp.peek() != null){
					switch (tmp.peek().getType()){
					case VARIABLE:
						Variable vn = env.getVariable(tmp.poll().toString());
						output.push(vn.getValue());
						break;
					case INT:
						output.push(tmp.poll().getValue());
						break;
					case DOUBLE:
						output.push(tmp.poll().getValue());
						break;
					case ADD:
						tmp.remove();
						int a1 = output.poll().getIValue();
						int a2 = output.poll().getIValue();
						output.push(new ValueImpl(String.valueOf(a2 + a1),ValueType.INTEGER));
						break;
					case SUB:
						tmp.remove();
						int  s1 = output.poll().getIValue();
						int  s2 = output.poll().getIValue();
						output.push(new ValueImpl(String.valueOf(s2 - s1),ValueType.INTEGER));
						break;
					case MUL:
						tmp.remove();
						int  m1 = output.poll().getIValue();
						int  m2 = output.poll().getIValue();
						output.push(new ValueImpl(String.valueOf(m2 * m1),ValueType.INTEGER));
						break;
					case DIV:
						tmp.remove();
						int d1 = output.poll().getIValue();
						int d2 = output.poll().getIValue();
						if(d1 == 0) error("0除算はできません");
						output.push(new ValueImpl(String.valueOf(d2 / d1),ValueType.INTEGER));
						break;
					case MOD:
						tmp.remove();
						int mo1 = output.poll().getIValue();
						int mo2 = output.poll().getIValue();
						output.push(new ValueImpl(String.valueOf(mo2 % mo1),ValueType.INTEGER));
						break;
					default:
						error("変数を用いた演算として不正です");
					}
				}

				//キュー先頭を返してあげる
				switch (output.peekFirst().getType()){
				case INTEGER:
					return new ValueImpl(String.valueOf(output.poll().getIValue()),ValueType.INTEGER);
				case DOUBLE:
					return new ValueImpl(String.valueOf(output.poll().getDValue()),ValueType.DOUBLE);
				case STRING:
					return new ValueImpl(String.valueOf(output.poll().getSValue()),ValueType.STRING);
				case BOOL:
				case VOID:
				default:
					break;
				}
				//関数の場合はただ関数実行をしてあげる
			}else if (tmp.peek().getType() == ExprType.FUNCTION_CALL){
				env.getFunction(tmp.poll().getValue().getSValue()).invoke((ExprListNode)(tmp.poll()));
				return null;
			}
			//このリターンが返ることはない(はず)
			return null;
		}
	}
}