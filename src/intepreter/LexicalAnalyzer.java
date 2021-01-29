package interpreter;

public interface LexicalAnalyzer {
	public LexicalUnit get() throws Exception;
	public LexicalUnit peek() throws Exception;
	public LexicalUnit peek2() throws Exception;
}
