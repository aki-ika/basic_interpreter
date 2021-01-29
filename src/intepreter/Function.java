package interpreter;

public class Function {
	Environment env;

	public Function() {
	}
	
	public Function(Environment env) {
		this.env = env;
	}

	public Value invoke(ExprListNode argument) throws Exception {
		return null;        
	}
	
}
