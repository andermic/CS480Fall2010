//
//	parser skeleton, CS 480, Winter 2006
//	written by Tim Budd
//		modified by: Michael Anderson, Sam Heinith, Rob McGuire-Dale
//

public class Parser {
	private Lexer lex;
	private boolean debug;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse () throws ParseException {
		lex.nextLex();
		program();
		if (lex.tokenCategory() != lex.endOfInput)
			parseError(3); // expecting end of file
	}

	private final void start (String n) {
		if (debug) System.out.println("start " + n + " token: " + lex.tokenText());
	}

	private final void stop (String n) {
		if (debug) System.out.println("recognized " + n + " token: " + lex.tokenText());
	}

	private void parseError(int number) throws ParseException {
		throw new ParseException(number);
	}

	private void parseError(int number, String name) throws ParseException {
		throw new ParseException(number, name);
	}

	private void program () throws ParseException {
		start("program");

		while (lex.tokenCategory() != Lexer.endOfInput) {
			declaration();
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}        
		stop("program");
	}

	private void declaration () throws ParseException {
		start("declaration");
		
		if (lex.match("class")) {
			lex.nextLex();
			classDeclaration();
		}
		else {
			nonClassDeclaration();
		}
		
		stop("declaration");
	}

	private void nonClassDeclaration() throws ParseException {
		start("nonClassDeclaration");
		
		if (lex.match("function")) {
			lex.nextLex();
			functionDeclaration();
		}
		else {
			nonFunctionDeclaration();
		}
		
		stop("nonClassDeclaration");
	}
	
	private void nonFunctionDeclaration() throws ParseException {
		start("nonFunctionDeclaration");
		
		if (lex.match("var")) {
			lex.nextLex();
			variableDeclaration();
		}
		else if (lex.match("const")){
			lex.nextLex();
			constDeclaration();
		}
		else if (lex.match("type")){
			lex.nextLex();
			typeDeclaration();
		}
		else {
			parseError(26);
		}
		
		stop("nonFunctionDeclaration");
	}
	
	private void constantDeclaration() throws ParseException {
		start("constantDeclaration");
		
		if (lex.tokenCategory() == Lexer.identifierToken) {
			lex.nextLex();
			if (lex.match("=")) {
				lex.nextLex();
				if (lex.tokenCategory() == Lexer.intToken ||
				    lex.tokenCategory() == Lexer.realToken ||
				    lex.tokenCategory() == Lexer.stringToken) {
					lex.nextLex();
				}
				else {
					parseError(31);
				}
			}
			else {
				parseError(20);
			}
		}
		else {
			parseError(27);
		}
		
		stop("constantDeclaration");
	}

	private void typeDeclaration() throws ParseException {
		start("typeDeclaration");
		
		if (lex.tokenCategory() == Lexer.identifierToken) {
			lex.nextLex();
			nameDeclaration();
		}
		else {
			parseError(27);
		}
		
		stop("typeDeclaration");
	}

	private void variableDeclaration() throws ParseException {
		start("variableDeclaration");
		
		if (lex.tokenCategory() == Lexer.identifierToken) {
			lex.nextLex();
			nameDeclaration();
		}
		else {
			parseError(27);
		}
		
		stop("variableDeclaration");
	}
	
	private void nameDeclaration() throws ParseException {
		start("nameDeclaration");
		
		if (lex.match(":")) {
			lex.nextLex();
			type();
		}
		else {
			parseError(19);
		}
		
		stop("nameDeclaration");
	}

	private void classDeclaration () throws ParseException {
		start("classDeclaration");
		
		if (lex.tokenCategory() == Lexer.identifierToken) {
			lex.nextLex();
			classBody();
		}
		else {
			parseError(27);
		}
		
		stop("classDeclaration");
	}
	
	private void classBody () throws ParseException {
		start("classBody");
		
		if (lex.match("begin")) {
			lex.nextLex();
			while (!lex.match("end")) {
				nonClassDeclaration();
			}
			if (lex.match("end")) {
				lex.nextLex();
			}
			else {
				parseError(8);
			}
		}
		else {
			parseError(4);
		}
		
		stop("classBody");
	}
	
	private void functionDeclaration () throws ParseException {
		start("functionDeclaration");
		
		if (lex.tokenCategory() == Lexer.identifierToken) {
			lex.nextLex();
			arguments();
			returnType();
			functionBody();
		}
		else {
			parseError(27);
		}
		
		stop("functionDeclaration");
	}
	
	private void arguments () throws ParseException {
		start("arguments");
		
		if (lex.match("(")) {
			lex.nextLex();
			argumentList();
			if (lex.match(")") {
				lex.nextLex();
			}
			else {
				parseError(22);
			}
		}
		else {
			parseError(21);
		}
		
		stop("arguments");
	}
	
	private void argumentList () throws ParseException {
		start("argumentList");
		
		if(lex.tokenCategory() == Lexer.identifierToken) {
			lex.nextLex();
			nameDeclaration();
			while(!lex.match(")") {
				if(lex.match(",")) {
					lex.nextLex();
					if(lex.tokenCategory() == Lexer.identifierToken) {
						lex.nextLex();
						nameDeclaration();
					}
					else {
						parseError(27);
					}					
				}
				else {
					parseError(47, "expecting comma");
				}
			}
		}
		else {} //Successfully match nothing
		
		stop("argumentList");
	}
	
	private void returnType () throws ParseException {
		start("returnType");
		
		if(lex.match(":")) {
			lex.nextLex();
			type();
		}
		else {} //Successfully matched nothing
		
		stop("returnType");
	}
	
	private void type () throws ParseException {
		start("type");
		
		if( lex.tokenCategory() == Lexer.identifierToken) {
			lex.nextLex();
		}
		else if( lex.match("^")) {
			lex.nextLex();
			type();
		}
		else if( lex.match("[")) {
			lex.nextLex();
			if( lex.tokenCategory() == Lexer.intToken) {
				lex.nextLex();
				if( lex.match(":")) {
					lex.nextLex();
					if( lex.tokenCategory() == Lexer.intToken) {
						lex.nextLex();
						if( lex.match("]")) {
							lex.nextLex();
							type();
						}
						else {
							parseError(24);
						}
					}
					else {
						parseError(32);
					}
				}
				else {
					parseError(19);
				}
			}
			else {
				parseError(32);
			}
		}
		else {
			parseError(30);
		}
		
		stop("type");
	}

	private void functionBody () throws ParseException {
		start("functionBody");
		
		while(!lex.match("begin")) {
			nonClassDeclaration();
			lex.nextLex();
			if( lex.match(";")) {
				lex.nextLex();
			}
			else {
				parseError(18);
			}
		}
		
		compoundStatement();
		
		stop("functionBody");
	}
	
	private void compoundStatement () throws ParseException {
		start("compoundStatement");
		
		while(!lex.match("end")) {
			statement();
			lex.nextLex();
			if( lex.match(";")) {
				lex.nextLex();
			}
			else {
				parseError(18);
			}
		}
		lex.nextLex();
		
		stop("compoundStatement");
	}
	
	private void statement () throws ParseException {
		start("statement");
		
		if(lex.match("return")) {
			lex.nextLex();
			returnStatement();
		}
		else if(lex.match("if")) {
			lex.nextLex();
			ifStatement();
		}
		else if(lex.match("while")) {
			lex.nextLex();
			whileStatement();
		}
		else if(lex.match("begin")) {
			lex.nextLex();
			compoundStatement();
		}
		else if(lex.tokenCategory() == Lexer.identifierToken) {
			lex.nextLex();
			assignOrFunction();
		}
		else {
			parseError(34);
		}

		stop("statement");

	private void returnStatement () throws ParseException {
		start("returnStatement");
		
		if(lex.match("(")) {
			lex.nextLex();
			expression();
			lex.nextLex();
			if(lex.match(")")) {
				lex.nextLex();
			}
			else {
				parseError(22);
			}
		}
		else {} //Successfully matched nothing
		
		stop("returnStatement");
	}

	private void ifStatement () throws ParseException {
		start("ifStatement");
		
		if(lex.match("(")) {
			lex.nextLex();
			expression();
			lex.nextLex();
			if(lex.match(")")) {
				lex.nextLex();
				statement();
				if(lex.match("else")) {
					lex.nextLex();
					statement();
				}
				else {} //Successfully match nothing
			}
			else {
				parseError(22);
			}
		}
		else {
			parseError(21);
		}
		
		stop("ifStatement");
	}
	
	private void whileStatement () throws ParseException {
		start("whileStatement");

		if(lex.match("(")) {
			lex.nextLex();
			expression();
			lex.nextLex();
			if(lex.match(")")) {
				lex.nextLex();
				statement();
			}
			else {
				parseError(22);
			}
		}
		else {
			parseError(21);
		}
		
		start("whileStatement");
	}
	
	private void assignOrFunction () throws ParseException {
		start("assignOrFunction");
		
		reference();
		if(lex.match("=")) {
			lex.nextLex();
			expression();
		}
		else if(lex.match("(")) {
			lex.nextLex();
			parameterList();
			if(lex.match(")")) {
				lex.nextLex();
			}
			else {
				parseError(22);
			}
		}
		else {
			parseError(47, "expecting '=' or '('");
		}
		
		stop("assignOrFunction");
	}
	
	private void parameterList () throws ParseException {
		start("parameterList");
		
		while(!lex.match(")")) {
			
		}
		
		stop("parameterList");	
	}
}