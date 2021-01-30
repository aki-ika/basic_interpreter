package lexer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {
	PushbackReader pbr;
	Map<String, LexicalType> reserved;
	List<LexicalUnit> peek_buffer;
	
	public LexicalAnalyzerImpl(InputStream in) {
		peek_buffer = new ArrayList<>();
		pbr = new PushbackReader(new InputStreamReader(in));
		reserved = new HashMap<String, LexicalType>();
		for(LexicalType lt : LexicalType.values()) {
			if(true) {
				reserved.put(lt.getNotation(), lt);
			}
		}
	}

	@Override
	public LexicalUnit get() throws Exception {
		//キューの先頭を取得して削除する
		if(!peek_buffer.isEmpty()) {
			return peek_buffer.remove(peek_buffer.size()-1);
		}

		//ここから従来のget()
		while(true) {
			int singleInt = pbr.read();
			char singleChar = (char)singleInt;
			String symbol = "[\\=\\<\\>\\\n\\.\\+\\-\\*\\/\\(\\)\\,\\%]";
			Matcher mSymbol = Pattern.compile(symbol).matcher(String.valueOf(singleChar));

			String s = "[a-zA-Z]";
			Matcher m = Pattern.compile(s).matcher(String.valueOf(singleChar));
			if(singleChar == '\s' && singleChar != '\n') {
				continue;
			}
			if(singleInt < 0) {
				return new LexicalUnit(LexicalType.EOF);
			}

			if(singleChar == '"') {
				return readLiteral(singleChar, pbr);
			}

			if(Character.isDigit(singleChar)) {
				return readNum(singleChar, pbr);
			}


			if(m.find()){
				return readName(singleChar, pbr);
			}
			if(mSymbol.find()) {
				return readSymbol(singleChar, pbr);
			}

		}
	}



	private LexicalUnit readLiteral(char c, PushbackReader r) throws Exception {
		String literal = "";
		for(;;) {
			char nextChar = (char) r.read(); //次の文字を読み込む
			if(nextChar == '"') {//"ならループ終了
				break;
			}else{
				literal += nextChar;//連結
			}
		}
		return new LexicalUnit(LexicalType.LITERAL,new ValueImpl(literal,ValueType.STRING));
	}

	private LexicalUnit readNum(char c, PushbackReader r) throws Exception {
		String num = "";
		num += c;
		boolean doubleFlag = false;
		for(;;) {
			//0-9なら追加、ちがうならdouble型か判定してピリオドを追加
			char nextChar = (char) r.read();
			if(Character.isDigit(nextChar)) { 
				num += nextChar;
			}else if(nextChar == '.' && !doubleFlag) {
				doubleFlag = true;
				num += nextChar;
			}else if(nextChar == '.' && doubleFlag) {
				r.unread(nextChar);
				break;
			}else {
				r.unread(nextChar);
				break;
			}
		}

		if(doubleFlag) {
			return new LexicalUnit(LexicalType.DOUBLEVAL, new ValueImpl(num,ValueType.DOUBLE));
		}else {
			return new LexicalUnit(LexicalType.INTVAL, new ValueImpl(num,ValueType.INTEGER));
		}
	}

	private LexicalUnit readName(char c, PushbackReader r) throws Exception {
		String str = "";
		str += c;
		for(;;) {
			char nextChar = (char) r.read(); //次の文字を読み込む
			String notSpace = "[a-zA-Z0-9]";
			Matcher mNotSpace = Pattern.compile(notSpace).matcher(String.valueOf(nextChar));
			if(mNotSpace.find()) {	
				str += nextChar;
			}else {
				r.unread(nextChar);
				break;
			}
		}

		Value value = new ValueImpl(str,ValueType.STRING);
		if(reserved.containsKey(str)) { //keyにあるかをチェック
			return new LexicalUnit(reserved.get(str), value);
		}else {
			return new LexicalUnit(LexicalType.NAME, value);
		}
	}

	private LexicalUnit readSymbol(char c, PushbackReader r) throws Exception {
		String symbol = "";
		symbol += c;
		Value value = new ValueImpl(symbol,ValueType.STRING);
		if(symbol.equals(">")) {
			char nextChar = (char)r.read();
			if(nextChar == '=') {
				symbol += nextChar;
				value = new ValueImpl(symbol,ValueType.STRING);
				return new LexicalUnit(reserved.get(symbol),value);		
			}else {
				return new LexicalUnit(reserved.get(symbol),value);		
			}
		}
		if(symbol.equals("<")){
			char nextChar = (char) r.read(); //次の文字を読み込む
			if(nextChar == '=') {
				symbol += nextChar;
				value = new ValueImpl(symbol,ValueType.STRING);
				return new LexicalUnit(reserved.get(symbol),value);		
			}else if(nextChar == '>'){
				symbol += nextChar;
				value = new ValueImpl(symbol,ValueType.STRING);
				return new LexicalUnit(reserved.get(symbol),value);		
			}else {
				return new LexicalUnit(reserved.get(symbol),value);		
			}
		}

		if(symbol.equals(".")) {
			char nextChar = (char) r.read();
			if(nextChar == '.') {
				symbol += nextChar;
				value = new ValueImpl(symbol, ValueType.STRING);
				return new LexicalUnit(reserved.get(symbol),value);
			}	else {
				return new LexicalUnit(reserved.get(symbol),value);
			}
		}


		if(reserved.containsKey(symbol)) { //keyにあるかをチェック
			return new LexicalUnit(reserved.get(symbol), value);
		}

		return new LexicalUnit(reserved.get(symbol),value);
	}



	@Override
	public LexicalUnit peek() throws Exception {
		LexicalUnit ret = get();
		peek_buffer.add(ret);
		return ret;
	}

	@Override
	public LexicalUnit peek2() throws Exception {
		List<LexicalUnit> tmpList = new ArrayList<>();
		tmpList.add(get());

		LexicalUnit ret = get();
		tmpList.add(ret);

		while(!tmpList.isEmpty()){
			peek_buffer.add(tmpList.get(tmpList.size()-1));
			tmpList.remove((tmpList.size()-1));
		}

		return ret;
	}
}
