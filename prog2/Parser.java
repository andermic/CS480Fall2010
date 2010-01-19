//
//	parser skeleton, CS 480, Winter 2006
//	written by Tim Budd
//		modified by: Michael Anderson, Sam Heinith, Rob McGuire-Dale
//

import java.io.*;

public class Parser {
	private Lexer lex;
	private boolean debug;
	private int indent = 0;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse () throws ParseException, IOException {
		lex.nextLex();
		program();
		if (lex.tokenCategory() != Lexer.endOfInput)
			parseError(3); // expecting end of file
	}

	private final void start (String n) {
		if (debug) {
			System.out.print(indent);
			if(indent < 10) {
				System.out.print(' ');
			}
			System.out.print(" - ");
			System.out.println("start " + n + " token: " + lex.tokenText());
			indent++;
		}
	}

	private final void stop (String n) {
		if (debug) {
			indent--;
			System.out.print(indent);
			if(indent < 10) {
				System.out.print(' ');
			}
			System.out.print(" - ");
			System.out.println("recognized " + n + " token: " + lex.tokenText());
		}
	}

	private void parseError(int number) throws ParseException {
		throw new ParseException(number);
	}

	private void parseError(int number, String name) throws ParseException {
		throw new ParseException(number, name);
	}

	private void program () throws ParseException, IOException {
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

	private void declaration () throws ParseException, IOException {
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

	private void nonClassDeclaration() throws ParseException, IOException {
		start("nonClassDeclaration");
		
		if (lex.match("function")) {
			functionDeclaration();
		}
		else {
			nonFunctionDeclaration();
		}
		
		stop("nonClassDeclaration");
	}
	
	private void nonFunctionDeclaration() throws ParseException, IOException {
		start("nonFunctionDeclaration");
		
		if (lex.match("var")) {
			lex.nextLex();
			variableDeclaration();
		}
		else if (lex.match("const")){
			lex.nextLex();
			constantDeclaration();
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
	
	private void constantDeclaration() throws ParseException, IOException {
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

	private void typeDeclaration() throws ParseException, IOException {
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

	private void variableDeclaration() throws ParseException, IOException {
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
	
	private void nameDeclaration() throws ParseException, IOException {
		start("nameDeclaration");
		
		lex.nextLex();
		if (lex.match(":")) {
			lex.nextLex();
			type();
		}
		else {
			parseError(19);
		}
		
		stop("nameDeclaration");
	}

	private void classDeclaration () throws ParseException, IOException {
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
	
	private void classBody () throws ParseException, IOException {
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
	
	private void functionDeclaration () throws ParseException, IOException {
		start("functionDeclaration");
		
		lex.nextLex();
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
	
	private void arguments () throws ParseException, IOException {
		start("arguments");
		
		if (lex.match("(")) {
			lex.nextLex();
			argumentList();
			if (lex.match(")")) {
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
	
	private void argumentList () throws ParseException, IOException {
		start("argumentList");
		
		if(lex.tokenCategory() == Lexer.identifierToken) {
			nameDeclaration();
			while(!lex.match(")")) {
				if(lex.match(",")) {
					lex.nextLex();
					if(lex.tokenCategory() == Lexer.identifierToken) {
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
	
	private void returnType () throws ParseException, IOException {
		start("returnType");
		
		if(lex.match(":")) {
			lex.nextLex();
			type();
		}
		else {} //Successfully matched nothing
		
		stop("returnType");
	}
	
	private void type () throws ParseException, IOException {
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

	private void functionBody () throws ParseException, IOException {
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
	
	private void compoundStatement () throws ParseException, IOException {
		start("compoundStatement");
		
		while(!lex.match("end")) {
			lex.nextLex();
			statement();
			if(!lex.match(";")) {
				parseError(18);
			}
		}
		lex.nextLex();
		
		stop("compoundStatement");
	}
	
	private void statement () throws ParseException, IOException {
		start("statement");
		
		if(lex.match("return")) {
			returnStatement();
		}
		else if(lex.match("if")) {
			ifStatement();
		}
		else if(lex.match("while")) {
			whileStatement();
		}
		else if(lex.match("begin")) {
			compoundStatement();
		}
		else if(lex.tokenCategory() == Lexer.identifierToken) {
			assignOrFunction();
		}
		else {
			parseError(34);
		}

		stop("statement");
	}
		
	private void returnStatement () throws ParseException, IOException {
		start("returnStatement");
		
		lex.nextLex();
		if(lex.match("(")) {
			lex.nextLex();
			expression();
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

	private void ifStatement () throws ParseException, IOException {
		start("ifStatement");
		
		lex.nextLex();
		if(lex.match("(")) {
			lex.nextLex();
			expression();
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
	
	private void whileStatement () throws ParseException, IOException {
		start("whileStatement");

		lex.nextLex();
		if(lex.match("(")) {
			lex.nextLex();
			expression();
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
		
		stop("whileStatement");
	}
	
	private void assignOrFunction () throws ParseException, IOException {
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
	
	private void parameterList () throws ParseException, IOException {
		start("parameterList");
		
		if(lex.match(")")) {
			lex.nextLex();
		}
		else {
			while(true) {
				expression();
				if(!lex.match("'")) {
					break;
				}
			}
		}
		
		stop("parameterList");	
	}

	private void expression () throws ParseException, IOException {
		start("expression");
		
		relExpression();
		while(lex.match("and") || lex.match("or")) {
			lex.nextLex();
			relExpression();
		}
		
		stop("expression");
	}

	private void relExpression () throws ParseException, IOException {
		start("relExpression");
		
		plusExpression();
		if(lex.match("<") || lex.match("<=") || lex.match("!=") ||
		   lex.match("==") || lex.match(">=") || lex.match(">")) {
			lex.nextLex();
			plusExpression();
		}
	
		stop("relExpression");
	}

	private void plusExpression () throws ParseException, IOException {
		start("plusExpression");
		
		timesExpression();
		while(lex.match("+") || lex.match("-")) {
			lex.nextLex();
			timesExpression();
		}
		
		stop("plusExpression");
	}

	private void timesExpression () throws ParseException, IOException {
		start("timesExpression");
		
		term();
		while(lex.match("*") || lex.match("/") || lex.match("%")) {
			lex.nextLex();
			term();
		}
		
		stop("timesExpression");
	}

	private void term () throws ParseException, IOException {
		start("term");

		if(lex.match("(")) {
			lex.nextLex();
			expression();
			if(lex.match(")")) {
				lex.nextLex();
			}
			else {
				parseError(22);
			}
		}
		else if(lex.match("not") || lex.match("-")) {
			lex.nextLex();
			term();
		}
		else if(lex.match("new")) {
			lex.nextLex();
			type();
		}
		else if(lex.tokenCategory() == Lexer.identifierToken) {
			reference();
			if(lex.match("(")) {
				lex.nextLex();
				parameterList();
				if(lex.match(")")) {
					lex.nextLex();
				}
				else {
					parseError(22);
				}
			}
			else {} //Successfully match nothing
		}
		else if(lex.match("&")) {
			lex.nextLex();
			reference();
		}
		else if (lex.tokenCategory() == Lexer.intToken ||
			     lex.tokenCategory() == Lexer.realToken ||
			     lex.tokenCategory() == Lexer.stringToken) {
			lex.nextLex();
		}
		else {
			parseError(47, "expecting term");
		}
		
		stop("term");
	}

	private void reference () throws ParseException, IOException {
		start("reference");
		
		while(lex.tokenCategory() != Lexer.identifierToken) {
			if(lex.match("^")) {
				lex.nextLex();
			}
			else if(lex.match(".")) {
				lex.nextLex();
				if(lex.tokenCategory() == Lexer.identifierToken) {
					lex.nextLex();
				}
				else {
					parseError(27);
				}
			}
			else {
				expression();
			}
		}
		lex.nextLex();
		
		stop("reference");
	}
}