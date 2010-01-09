//
//		Compiler for CS 480
//	class Lexer
//
//		Written by Tim Budd, Winter term 2006
//
//		modified by: 
//

import java.io.*;

//
//--------Lexer----------------
//

public class Lexer {
	private PushbackReader input;
	private String token;
	private int tokenType;

	public Lexer(Reader in) {
		input = new PushbackReader(in);
	}

	private void skipWhiteSpace() throws ParseException, IOException {
		// your code here
	}
	
	public void nextLex() throws ParseException {
		// your code here
	}

	static final int identifierToken = 1;
	static final int keywordToken = 2;
	static final int intToken = 3;
	static final int realToken = 4;
	static final int stringToken = 5;
	static final int otherToken = 6;
	static final int endOfInput = 7;

	public String tokenText() {
		return token;
	}

	public int tokenCategory() {
		return tokenType;
	}

	public boolean isIdentifier() {
		return tokenType == identifierToken;
	}

	public boolean match (String test) {
		return test.equals(token);
		}
}
