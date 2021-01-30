
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import lexer.*;
import node.Node;

public class Main {

	public static void main(String[] args) throws Exception {
		String source = "fizzbuzz.bas";
		File file = new File(source);
		if(!file.exists()) {
			System.out.println(source + " は存在しません");
		}else if(file.length() == 0) {
			System.out.println(source + " は空ファイルです");
		}else {
			InputStream in = new FileInputStream(source);
			LexicalAnalyzer lex = new LexicalAnalyzerImpl(in);
			Environment env = new Environment(lex);
			Node p = Symbol.program.handle(env);
			System.out.println("---- ----- 構文解析 ---- ---- ");
			System.out.println(p);
			System.out.println("---- ----- -- 実行 -- ---- ---- ");
			p.getValue();
			//System.out.println(p.getValue().getSValue());
		}
	}
}