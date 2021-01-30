package func;

import lexer.Value;
import node.ExprListNode;

public class PrintFunction extends Function {
	public PrintFunction() {
	}

	@Override
	public Value invoke(ExprListNode arg) throws Exception {
		if(arg.size() <= 0) {
			System.out.println("PRINTの引数が空です");
		}else {
			String str = "";
			for(int i = 0; i < arg.size(); i++ ) {
				if(i > 0) str += " ";
				str += arg.getValue(i).getSValue();
			}

			System.out.println(str);
		}
		return null;
	}
}