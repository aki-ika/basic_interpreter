package intepreter;

import java.lang.reflect.Constructor;
import java.util.EnumSet;
import java.util.Set;

public enum Symbol {
    program(
	    EnumSet.of(
		       LexicalType.NAME,
		       LexicalType.FOR,
		       LexicalType.FORALL,
		       LexicalType.END,
		       LexicalType.IF,
		       LexicalType.DO,
		       LexicalType.DIM
		       ), 
	    ProgramNode.class),
    stmt_list(
	      EnumSet.of(
			 LexicalType.NAME,
			 LexicalType.FOR,
			 LexicalType.FORALL,
			 LexicalType.END,
			 LexicalType.IF,
			 LexicalType.DO,
			 LexicalType.DIM
			 ),
	      StatementListNode.class),
    block(
	  EnumSet.of(
		     LexicalType.IF,
		     LexicalType.DO,
		     LexicalType.WHILE
		     ),
	  BlockNode.class),
    stmt(
	 EnumSet.of(
		    LexicalType.NAME,
		    LexicalType.FOR,
		    LexicalType.FORALL,
		    LexicalType.DIM,
		    LexicalType.END
		    ),
	 StatementNode.class),
    expr_list(
	      EnumSet.of(
			 LexicalType.SUB,
			 LexicalType.LP,
			 LexicalType.NAME,
			 LexicalType.INTVAL,
			 LexicalType.DOUBLEVAL,
			 LexicalType.LITERAL
			 ),
	      ExprListNode.class),
    if_prefix(
	      EnumSet.of(
			 LexicalType.IF	
			 ),
	      IfNode.class),
    else_block(
	       EnumSet.of(
			  LexicalType.ELSE	
			  ),
	       null),
    else_if_prefix(
		   EnumSet.of(
			      LexicalType.ELSEIF	
			      ),
		   null),
    subst(
	  EnumSet.of(
		     LexicalType.NAME	,
		     LexicalType.DIM
		     ),
	  AssignmentNode.class),
    cond(
	 EnumSet.of(
		    LexicalType.SUB,
		    LexicalType.LP,
		    LexicalType.NAME,
		    LexicalType.INTVAL,
		    LexicalType.DOUBLEVAL,
		    LexicalType.LITERAL
		    ),
	 CondNode.class),
    expr(
	 EnumSet.of(
		    LexicalType.SUB,
		    LexicalType.LP,
		    LexicalType.NAME,
		    LexicalType.INTVAL,
		    LexicalType.DOUBLEVAL,
		    LexicalType.LITERAL
		    ),
	 ExpressionNode.class),
    var(
	EnumSet.of(
		   LexicalType.NAME	
		   ),
	VariableNode.class),
    leftvar(
	    EnumSet.of(
		       LexicalType.NAME	
		       ),
	    VariableNode.class),
    call_func(
	      EnumSet.of(
			 LexicalType.NAME	
			 ),
	      FunctionCallNode.class),
    loop(
	 EnumSet.of(
		    LexicalType.DO	,
		    LexicalType.WHILE
		    ),
	 LoopNode.class),
    constant(
	     EnumSet.of(
			LexicalType.INTVAL,
			LexicalType.DOUBLEVAL,
			LexicalType.LITERAL
			),
	     ConstantNode.class),
    //BinaryOperatorNode
    binary(
	   EnumSet.of(
		      LexicalType.ADD,
		      LexicalType.SUB,
		      LexicalType.MUL,
		      LexicalType.DIV,
		      LexicalType.MOD
		      ),
	   BinaryOperatorNode.class),
    end(
	EnumSet.of(
		   LexicalType.END
		   ),
	EndNode.class),
    forloop(
	    EnumSet.of(
		       LexicalType.FOR,
		       LexicalType.FORALL
		       ),
	    ForNode.class), 
    parenthesis(
		EnumSet.of(
			   LexicalType.LP
			   ),
		ParenthesisNode.class),
    ;
    Set<LexicalType> first;
    Class<? extends Node> handler;
    Constructor<? extends Node> constructor;

    Symbol(Set<LexicalType> first, Class<? extends Node> handler) {
	this.first = first;
	this.handler = handler;
	try {
	    constructor = null;
	    if (handler != null) {
		constructor = handler.getConstructor(Environment.class);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public boolean isFirst(LexicalType type) {
	return first.contains(type);
    }

    public Node handle(Environment env) throws Exception {
	Node instance = (Node) constructor.newInstance(env);
	instance.parse();
	return instance;
    }
}
