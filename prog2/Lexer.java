//
//		Compiler for CS 480
//		Class Lexer
//
//		Written by Tim Budd, Winter term 2006
//
//		Modified by: Mike Anderson, Sam Heinith, and Rob Mcguire-Dale 
//

import java.io.*;

//
//--------Lexer----------------
//

public class Lexer {
	private PushbackReader input;
	private String token;
	private int tokenType;
	private String[] keywords = {"not","new","and","or","while","if","return","begin","end",
	 "function","class","const","type","var","else"};
	final int EOF_RETURN = 65535; // To fix a Java glitch, read() returns 65535 on eof if
								  //  ints are longer than 16 bits.
	                          

	public Lexer(Reader in) {
		input = new PushbackReader(in);
	}

	private void skipComments() throws ParseException, IOException {
		while(true) {
			int firstChar = 0, middleChar = 0;
			skipWhiteSpace();
			
			try { 
				firstChar = input.read();
			} catch (IOException e) { throw new ParseException(0); }

			if( (char)firstChar != '{' ){
				try { 
					input.unread(firstChar);
				} catch (IOException e) { throw new ParseException(0); }
				break;
			}
			
			do {
				try { 
					middleChar = input.read();
				} catch (IOException e) { throw new ParseException(0); }
				if( (char)middleChar == '{' || middleChar == -1 || middleChar == EOF_RETURN ) {
					throw new ParseException(1);
				}
			} while( (char)middleChar != '}' );
		}
	}
	
	private void skipWhiteSpace() throws ParseException, IOException {
		int currentChar = -1;
		do { 
			try { 
				currentChar = input.read();
			} catch (IOException e) { throw new ParseException(0); }
		} while(Character.isWhitespace((char) currentChar));
		try { 
			input.unread(currentChar);
		} catch (IOException e2) { throw new ParseException(0); }
		return;		
	}
	
	public void nextLex() throws ParseException, IOException {
		token = "";
		char currentChar;
		int currentCharAsInt;

		skipComments();
		skipWhiteSpace();

		try {
			 currentCharAsInt = input.read();
		} catch (IOException e1) { throw new ParseException(0); }
		
		currentChar = (char) currentCharAsInt;
		if( currentCharAsInt == -1 || currentCharAsInt == EOF_RETURN ){
			token = "<eof>";
			tokenType = endOfInput;
		}		
		
		else if(Character.isDigit(currentChar)) {
			tokenType = intToken; //by default
			token = token + currentChar;
			while(true){
				try {
					currentChar = (char) input.read();
				} catch (IOException e) { throw new ParseException(0); }
				if(Character.isDigit(currentChar)){
					token = token + currentChar;
				}
				else if (currentChar == '.'){
					if(token.indexOf('.') == -1){
						tokenType = realToken;
						token = token + currentChar;
					} else {
						try {
							input.unread(currentChar);
						} catch (IOException e) { throw new ParseException(0); }
						break;
					}
				}
				else {
					try {
						input.unread(currentChar);
					} catch (IOException e) { throw new ParseException(0); }
					break;
				}				
			}			
		}		

		else if(Character.isLetter(currentChar)) { 		
			token = token + currentChar;
			while(true){
				try {
					currentChar = (char) input.read();
				} catch (IOException e) { throw new ParseException(0); }
				if(Character.isLetterOrDigit(currentChar)){
					token = token + currentChar;
				}
				else {
					input.unread(currentChar);
					tokenType = identifierToken;
					for(String current: keywords){
						if(match(current)){
							tokenType = keywordToken;
							break;
						}
					}					
					break;	
				}
			}
		}		
		
		else {
			switch(currentChar) {
			case '"':
				tokenType = stringToken;
					while(true) {
						try {
							currentCharAsInt = input.read();
							currentChar = (char) currentCharAsInt;
						} catch (IOException e) { throw new ParseException(0); }
						if( currentCharAsInt == -1 || currentCharAsInt == EOF_RETURN ) {
							throw new ParseException(2);
						}
						else if( currentChar == '"') {
							break;
						}
						token = token + currentChar;
					} 
				break;
				
			case '<':
				tokenType = otherToken;
				token = token + currentChar;
				char secondChar = (char) input.read();
				if(secondChar == '<'){
					token = token + secondChar;
					break;
				}
				else {
					input.unread(secondChar);
				}					

			case '>':				
			case '=':			
			case '!':
				tokenType = otherToken;
				if(currentChar != '<') {
					token = token + currentChar;
				}
				secondChar = (char) input.read();
				if(secondChar == '=') {
					token = token + secondChar;
					break;
				}
				else {
					input.unread(secondChar);
					}
				break;
				
			default:
				token = Character.toString(currentChar);
				tokenType = otherToken;		
			}
		}	
		return;		
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
