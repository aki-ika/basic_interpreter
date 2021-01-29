package intepreter;

public class Variable {
	String var_name;
	Value value;

	/** Creates a new instance of variable */
	public Variable(String name) {
		var_name = name;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Value getValue() {
		return value;
	}

	public String getName() {
		return var_name;
	}

}
