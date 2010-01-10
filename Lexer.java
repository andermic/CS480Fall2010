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
	private String[] keywords = {"not","new","and","or","while","if","return","begin","end","function","class","const","type","var","else"};
	                          

	public Lexer(Reader in) {
		input = new PushbackReader(in);
	}

	private void skipWhiteSpace() throws ParseException, IOException {
		// your code here
		int currentChar = -1;
		do{ 
			currentChar = input.read();
			
		} while(Character.isWhitespace((char) currentChar));
		input.unread(currentChar);
		return;		
		
	}
	
	public void nextLex() throws ParseException, IOException {
		try {
			skipWhiteSpace();
		} catch (IOException e) { throw new ParseException(0); }
		token = "";
		char currentChar;
		int currentCharAsInt;

		try {
			 currentCharAsInt = input.read();
		} catch (IOException e1) { throw new ParseException(0); }
		
		currentChar = (char) currentCharAsInt;
		//String one = Integer.toBinaryString(currentCharAsInt);
		//String two = Integer.toBinaryString(-1);
		if(currentCharAsInt == 65535){  //wtf
			tokenType = endOfInput;
		}		
		
		else if(Character.isDigit(currentChar)){
			tokenType = intToken; //by default
			token = token + currentChar;
			while(true){
				currentChar = (char) input.read();
				if(Character.isDigit(currentChar)){
					token = token + currentChar;
				}
				else if (currentChar == '.'){
					if(token.indexOf('.') == -1){
						tokenType = realToken;
						token = token + currentChar;
					} else {
						input.unread(currentChar);
						break;
					}
				}
				else {
					input.unread(currentChar);
					break;
				}				
			}			
		}		
		else if(Character.isLetter(currentChar)){ 		
			token = token + currentChar;
			while(true){
				currentChar = (char) input.read();
				if(Character.isLetterOrDigit(currentChar)){
					token = token + currentChar;
				}
				else {
					input.unread(currentChar);
					tokenType = identifierToken;
					for(String current: keywords){
						if(token.equals(current)){
							tokenType = keywordToken;
							break;
						}
					}					
					break;	
				}
			}
		}		
		else{
			switch (currentChar){  
				
			case '<':
				tokenType = stringToken;
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
				tokenType = stringToken;
				if(currentChar != '<'){
				token = token + currentChar;
				}
				secondChar = (char) input.read();
				if(secondChar == '='){
					token = token + secondChar;
					break;
				}
				else {
					input.unread(secondChar);
					}	
				break;
				
			default:
				token = Character.toString(currentChar);
				tokenType = stringToken;		
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
