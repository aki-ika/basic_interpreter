package node;

public enum ExprType {
	//ConstantNode
	STRING,
	INT,
	DOUBLE,
	BOOL,
	//VariableNode
	VARIABLE,
	//BinaryOperatorNode
	ADD,
	MUL,
	SUB,
	DIV, 
	MOD,
	LP,
	RP, 
	//ExpressionNode
	FUNCTION_CALL,
}
