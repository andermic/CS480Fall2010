import java.util.*;

//
//	parser skeleton, CS 480/580, Winter 1998
//	written by Tim Budd
//		modified by:
//

public class Parser {
	private Lexer lex;
	private boolean debug;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse () throws ParseException {
		lex.nextLex();
		SymbolTable sym = new GlobalSymbolTable();
		sym.enterType("int", PrimitiveType.IntegerType);
		sym.enterType("real", PrimitiveType.RealType);
		sym.enterFunction("printInt", new FunctionType(PrimitiveType.VoidType));
		sym.enterFunction("printReal", new FunctionType(PrimitiveType.VoidType));
		sym.enterFunction("printStr", new FunctionType(PrimitiveType.VoidType));
		program(sym);
		if (lex.tokenCategory() != lex.endOfInput)
			parseError(3); // expecting end of file
		}

	private final void start (String n) {
		if (debug) System.out.println("start " + n + 
			" token: " + lex.tokenText());
		}

	private final void stop (String n) {
		if (debug) System.out.println("recognized " + n + 
			" token: " + lex.tokenText());
		}

	private void parseError(int number) throws ParseException {
		throw new ParseException(number);
		}

	private void program (SymbolTable sym) throws ParseException {
		start("program");

		while (lex.tokenCategory() != Lexer.endOfInput) {
			declaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		stop("program");
		}

	private void declaration (SymbolTable sym) throws ParseException {
		start("declaration");
		if (lex.match("class"))
			classDeclaration(sym);
		else if (lex.match("function") || lex.match("const") 
			|| lex.match("var") || lex.match("type"))
			nonClassDeclaration(sym);
		else 
			parseError(26);
		stop("declaration");
		}

	private void nonClassDeclaration (SymbolTable sym) throws ParseException {
		start("nonClassDeclaration");
		if (lex.match("function"))
			functionDeclaration(sym);
		else if (lex.match("const") || lex.match("var") 
				|| lex.match("type"))
			nonFunctionDeclaration(sym);
		else
			parseError(26);
		stop("nonClassDeclaration");
		}

	private void nonFunctionDeclaration (SymbolTable sym) throws ParseException {
		start("nonFunctionDeclaration");
		if (lex.match("var"))
			variableDeclaration(sym);
		else if (lex.match("const"))
			constantDeclaration(sym);
		else if (lex.match("type"))
			typeDeclaration(sym);
		else 
			parseError(26);
		stop("nonFunctionDeclaration");
		}

	private void constantDeclaration (SymbolTable sym) throws ParseException {
		start("constantDeclaration");
		if (lex.match("const")) {
			lex.nextLex();
			if (! lex.isIdentifier())
				parseError(27);
			String name = lex.tokenText();
			if (sym.nameDefined(name))
				throw new ParseException(35, name);
			lex.nextLex();
			if (! lex.match("="))
				parseError(20);
			lex.nextLex();
			Ast value = null;
			if (lex.tokenCategory() == lex.intToken)
				value = new IntegerNode(new Integer(lex.tokenText()));
			else if (lex.tokenCategory() == lex.realToken)
				value = new RealNode(new Double(lex.tokenText()));
			else if (lex.tokenCategory() == lex.stringToken)
				value = new StringNode(lex.tokenText());
			else
				parseError(31);
			sym.enterConstant(name, value);
			lex.nextLex();
			}
		else
			parseError(6);
		stop("constantDeclaration");
		}

	private void typeDeclaration (SymbolTable sym) throws ParseException {
		start("typeDeclaration");
		if (lex.match("type")) {
			lex.nextLex();
			if (! lex.isIdentifier())
				parseError(27);
			String name = lex.tokenText();
			if (sym.nameDefined(name))
				throw new ParseException(35, name);
			lex.nextLex();
			if (! lex.match(":"))
				parseError(19);
			lex.nextLex();
			sym.enterType(name, type(sym));
		} else
			parseError(14); 
		stop("typeDeclaration");
	}

	private void variableDeclaration (SymbolTable sym) throws ParseException {
		start("variableDeclaration");
		if (lex.match("var")) {
			lex.nextLex();
			nameDeclaration(sym);
			}
		else
			parseError(15);
		stop("variableDeclaration");
		}

	private void nameDeclaration (SymbolTable sym) throws ParseException {
		start("nameDeclaration");
		if (! lex.isIdentifier()) 
			parseError(27);
		String name = lex.tokenText();
		if (sym.nameDefined(name))
			throw new ParseException(35, name);
		lex.nextLex();
		if (! lex.match(":"))
			parseError(19);
		lex.nextLex();
		Type t = type(sym);
		sym.enterIdentifier(name, t);
		if (sym instanceof GlobalSymbolTable)
			CodeGen.genGlobal(name, t.size());
		
		stop("nameDeclaration");
		}

	private void classDeclaration(SymbolTable sym) throws ParseException {
		start("classDeclaration");
		if (! lex.match("class"))
			parseError(5);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		String name = lex.tokenText();
		if (sym.nameDefined(name))
			throw new ParseException(35, name);
		lex.nextLex();
		SymbolTable csym = new ClassSymbolTable(sym);
		sym.enterType(name, new ClassType(csym));
		classBody(csym);
		stop("classDeclaration");
		}

	private void classBody(SymbolTable sym) throws ParseException {
		start("classBody");
		if (! lex.match("begin"))
			parseError(4);
		lex.nextLex();
		while (! lex.match("end")) {
			nonFunctionDeclaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		lex.nextLex();
		stop("classBody");
		}

	private void functionDeclaration(SymbolTable sym) throws ParseException {
		start("functionDeclaration");
		if (! lex.match("function"))
			parseError(10);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		String name = lex.tokenText();
		if (sym.nameDefined(name))
			throw new ParseException(35, name);
		lex.nextLex();
		FunctionSymbolTable fsym = new FunctionSymbolTable(sym);
		arguments(fsym);
		fsym.doingArguments = false;
		Type rt = returnType(sym);
		FunctionType ft = new FunctionType(rt);
		ft.symbolTable = fsym;
		sym.enterFunction(name, ft);
		functionBody(fsym, name);
		stop("functionDeclaration");
		}
		
	private void arguments (SymbolTable sym) throws ParseException {
		start("arguments");
		if (! lex.match("("))
			parseError(21);
		lex.nextLex();
		argumentList(sym);
		if (! lex.match(")"))
			parseError(22);
		lex.nextLex();
		stop("arguments");
		}

	private void argumentList (SymbolTable sym) throws ParseException {
		start("argumentList");
		if (lex.isIdentifier()) {
			nameDeclaration(sym);
			while (lex.match(",")) {
				lex.nextLex();
				nameDeclaration(sym);
				}
			}
		stop("argumentList");
		}

	private Type returnType (SymbolTable sym) throws ParseException {
		start("returnType");
		Type result = PrimitiveType.VoidType;
		if (lex.match(":")) {
			lex.nextLex();
			result = type(sym);
			}
		stop("returnType");
		return result;
		}

	private Type type (SymbolTable sym) throws ParseException {
		start("type");
		Type result = null;
		if (lex.isIdentifier()) {
			result = sym.lookupType(lex.tokenText());
			lex.nextLex();
			}
		else if (lex.match("^")) {
			lex.nextLex();
			result = new PointerType(type(sym));
			}
		else if (lex.match("[")) {
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			int lower = (new Integer(lex.tokenText())).intValue();
			lex.nextLex();
			if (! lex.match(":"))
				parseError(19);
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			int upper = (new Integer(lex.tokenText())).intValue();
			lex.nextLex();
			if (! lex.match("]"))
				parseError(24);
			lex.nextLex();
			result = new ArrayType(lower, upper, type(sym));
			}
		else
			parseError(30);
		stop("type");
		return result;
		}

	private void functionBody (SymbolTable sym, String name) throws ParseException {
		start("functionBody");
		while (! lex.match("begin")) {
			nonFunctionDeclaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		CodeGen.genProlog(name, sym.size());
		compoundStatement(sym);
		CodeGen.genEpilog(name);
		stop("functionBody");
		}

	private void compoundStatement (SymbolTable sym) throws ParseException {
		start("compoundStatement");
		if (! lex.match("begin"))
			parseError(4);
		lex.nextLex();
		while (! lex.match("end")) {
			statement(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
			}
		lex.nextLex();
		stop("compoundStatement");
		}

	private void statement (SymbolTable sym) throws ParseException {
		start("statement");
		if (lex.match("return"))
			returnStatement(sym);
		else if (lex.match("if"))
			ifStatement(sym);
		else if (lex.match("while"))
			whileStatement(sym);
		else if (lex.match("begin"))
			compoundStatement(sym);
		else if (lex.isIdentifier())
			assignOrFunction(sym);
		else
			parseError(34);
		stop("statement");
		}

	private boolean firstExpression() {
		if (lex.match("(") || lex.match("not") || lex.match("-") || lex.match("&"))
			return true;
		if (lex.isIdentifier())
			return true;
		if ((lex.tokenCategory() == lex.intToken) ||
			(lex.tokenCategory() == lex.realToken) ||
			(lex.tokenCategory() == lex.stringToken))
			return true;
		return false;
		}

	private void returnStatement (SymbolTable sym) throws ParseException {
		start("returnStatement");
		Ast result = null;
		if (! lex.match("return"))
			parseError(12);
		lex.nextLex();
		if (lex.match("(")) {
			lex.nextLex();
			result = expression(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
			}
		CodeGen.genReturn(result);
		stop("returnStatement");
		}

	private void ifStatement (SymbolTable sym) throws ParseException {
		start("ifStatement");
		if (! lex.match("if"))
			parseError(11);
		lex.nextLex();
		if (! lex.match("("))
			throw new ParseException(21);
		else
			lex.nextLex();
		Ast result = expression(sym);
		if(! result.type.equals(PrimitiveType.BooleanType))
			parseError(43);
		Label label1 = new Label();
		result.branchIfFalse(label1);
				
		if (! lex.match(")"))
			throw new ParseException(22);
		else
			lex.nextLex();
		statement(sym);
		
		
		if (lex.match("else")) {
			Label label2 = new Label();
			label2.genBranch();
			label1.genCode();
			lex.nextLex();
			statement(sym);
			label2.genCode(); //generate the target?
			}
		else
			label1.genCode();		
		stop("ifStatement");
		}

	private void whileStatement (SymbolTable sym) throws ParseException {
		start("whileStatement");
		if (! lex.match("while"))
			parseError(16);
		lex.nextLex();
		if (! lex.match("("))
			throw new ParseException(21);
		else
			lex.nextLex();
		Ast result = expression(sym);
		if(! result.type.equals(PrimitiveType.BooleanType))
			parseError(43);
		if (! lex.match(")"))
			throw new ParseException(22);
		
		lex.nextLex();
		
		Label label1 = new Label();
		label1.genCode();
		
		Label label2 = new Label();
		result.branchIfFalse(label2);
		statement(sym);
		label1.genBranch();
		label2.genCode();
		
		stop("whileStatement");
		}

	private void assignOrFunction (SymbolTable sym) throws ParseException {
		start("assignOrFunction");
		Ast result = reference(sym);
		//val.genCode();
		if (lex.match("=")) {
			lex.nextLex();
			Ast right = expression(sym);
			Type leftBaseType = addressBaseType(result.type);
			if ( !(leftBaseType.equals(right.type)) )
				parseError(44);
			CodeGen.genAssign(result, right);
			}
		else if (lex.match("(")) {
			if ( ! (result.type instanceof FunctionType) )
				parseError(45);
			lex.nextLex();
			Vector params = parameterList(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
			FunctionCallNode fnode = new FunctionCallNode(result, params);
			fnode.genCode();
			}
		else
			parseError(20);
		stop("assignOrFunction");
		}

	private Vector parameterList (SymbolTable sym) throws ParseException {  //TODO
		start("parameterList");
		Stack<Type> paramTypes = ((FunctionSymbolTable)sym).getParams();
		Ast tree = null;
		Vector result = new Vector();
		//System.out.println(lex.tokenText());
		if (firstExpression()) {
			tree = expression(sym);
			//if(! tree.type.equals( paramTypes.pop() ))
				//parseError(44);
			result.addElement(tree);
			while (lex.match(",")) {
				lex.nextLex();
				tree = expression(sym);
				//if(! tree.type.equals( paramTypes.pop() ))
					//parseError(44);
				result.addElement(tree);
				}
			}
		stop("parameterList");
		return result;
		}

	private Ast expression (SymbolTable sym) throws ParseException {
		start("expression");
		Ast result = relExpression(sym);
		MustBeBoolean(result);
		while (lex.match("and") || lex.match("or")) {
			String text = lex.tokenText();
			lex.nextLex();
			Ast right = relExpression(sym);
			MustBeBoolean(right);
			if(text.equals("and")) {
				result = new BinaryNode(BinaryNode.and, PrimitiveType.BooleanType, result, right);
			} else {
				result = new BinaryNode(BinaryNode.or, PrimitiveType.BooleanType, result, right);
			  }
			}
		stop("expression");
		return result;
		}
	
	private void MustBeBoolean(Ast tree) throws ParseException { //Needs testing
		if (tree.type.equals(PrimitiveType.BooleanType))
			parseError(43);
		}
	

	private boolean relOp() {
		if (lex.match("<") || lex.match("<=") ||
			lex.match("==") || lex.match("!=") ||
				lex.match(">") || lex.match(">="))
				return true;
		return false;
		}

	private Ast relExpression (SymbolTable sym) throws ParseException {
		start("relExpression");
		Ast result = plusExpression(sym);
		if (relOp()) {
			String relation = lex.tokenText();
			lex.nextLex();
			Ast right = plusExpression(sym);
			if(! result.type.equals(right.type))
				parseError(44);
			if(relation.equals("<"))
				result = new BinaryNode(BinaryNode.less, 
						PrimitiveType.BooleanType, result, right);
			else if(relation.equals("<="))
				result = new BinaryNode(BinaryNode.lessEqual, 
						PrimitiveType.BooleanType, result, right);
			else if(relation.equals("=="))
				result = new BinaryNode(BinaryNode.equal, 
						PrimitiveType.BooleanType, result, right);
			else if(relation.equals("!="))
				result = new BinaryNode(BinaryNode.notEqual, 
						PrimitiveType.BooleanType, result, right);
			else if(relation.equals(">="))
				result = new BinaryNode(BinaryNode.greaterEqual, 
						PrimitiveType.BooleanType, result, right);
			else if(relation.equals(">"))
				result = new BinaryNode(BinaryNode.greater, 
						PrimitiveType.BooleanType, result, right);
			}
		stop("relExpression");
		return result;
		}

	private Ast plusExpression (SymbolTable sym) throws ParseException {
		start("plusExpression");
		Ast result = timesExpression(sym);
		while (lex.match("+") || lex.match("-") || lex.match("<<")) {
			String addOp = lex.tokenText();
			lex.nextLex();
			Ast right = timesExpression(sym);
			if(addOp.equals("<<")) {
				if ((! result.type.equals(PrimitiveType.IntegerType)) ||
				 (! right.type.equals(PrimitiveType.IntegerType)))
					parseError(41);
				
				result = new BinaryNode(BinaryNode.leftShift, PrimitiveType.IntegerType, result, right);
			} else { 
				if(result.type.equals(PrimitiveType.IntegerType) &&
				 right.type.equals(PrimitiveType.RealType))
					result = new UnaryNode(UnaryNode.convertToReal, PrimitiveType.RealType, result);
				if(right.type.equals(PrimitiveType.IntegerType) &&
				 result.type.equals(PrimitiveType.RealType))
					right = new UnaryNode(UnaryNode.convertToReal, PrimitiveType.RealType, right);
				if(! (result.type.equals(PrimitiveType.IntegerType) || result.type.equals(PrimitiveType.RealType) &&
				 right.type.equals(PrimitiveType.IntegerType) || right.type.equals(PrimitiveType.RealType)) )
					parseError(46);
				if(! result.type.equals(right.type))
					parseError(44);
			}
			if(addOp.equals("+")) 
				result = new BinaryNode(BinaryNode.plus, result.type, result, right);
			else 
				result = new BinaryNode(BinaryNode.minus, result.type, result, right);
		}
		stop("plusExpression");
		return result;
	}

	private Ast timesExpression (SymbolTable sym) throws ParseException {
		start("timesExpression");
		Ast result = term(sym);
		while (lex.match("*") || lex.match("/") || lex.match("%")) {
			String multOp = lex.tokenText();
			lex.nextLex();
			Ast right = term(sym);
			if(multOp.equals("%")) {
				if ((! result.type.equals(PrimitiveType.IntegerType)) ||
				 (! right.type.equals(PrimitiveType.IntegerType)))
					parseError(41);
				result = new BinaryNode(BinaryNode.remainder, PrimitiveType.IntegerType, result, right);
			} else { 
				if(result.type.equals(PrimitiveType.IntegerType) &&
				 right.type.equals(PrimitiveType.RealType))
					result = new UnaryNode(UnaryNode.convertToReal, PrimitiveType.RealType, result);
				if(right.type.equals(PrimitiveType.IntegerType) &&
				 result.type.equals(PrimitiveType.RealType))
					right = new UnaryNode(UnaryNode.convertToReal, PrimitiveType.RealType, right);
				if(! (result.type.equals(PrimitiveType.IntegerType) || result.type.equals(PrimitiveType.RealType) &&
				 right.type.equals(PrimitiveType.IntegerType) || right.type.equals(PrimitiveType.RealType)) )
					parseError(46);
				if(! result.type.equals(right.type))
					parseError(44);
			}
			if(multOp.equals("*")) 
				result = new BinaryNode(BinaryNode.times, result.type, result, right);
			else 
				result = new BinaryNode(BinaryNode.divide, result.type, result, right);			
		}
		stop("timesExpression");
		return result;
	}

	private Ast term (SymbolTable sym) throws ParseException {
		start("term");
		Ast result = null;
		if (lex.match("(")) {
			lex.nextLex();
			result = expression(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
		}
		else if (lex.match("not")) {
			lex.nextLex();
			result = term(sym);
			MustBeBoolean(result);
			result = new UnaryNode(UnaryNode.notOp, result.type, result);
		}
		else if (lex.match("new")) {
			lex.nextLex();
			Type t = type(sym);
			IntegerNode sizeNode = new IntegerNode(t.size());
			result = new UnaryNode(UnaryNode.newOp, t, sizeNode);			
		}
		else if (lex.match("-")) {
			lex.nextLex();
			result = term(sym);
			if(! (result.type.equals(PrimitiveType.IntegerType) || result.type.equals(PrimitiveType.RealType)) )
					parseError(46);
			result = new UnaryNode(UnaryNode.negation, result.type, result);
		}
		else if (lex.match("&")) {  //Needs testing
			lex.nextLex();
			result = reference(sym);
			result.type = new PointerType(addressBaseType(result.type));
		}
		else if (lex.tokenCategory() == Lexer.intToken) {
			result = new IntegerNode(new Integer(lex.tokenText()));
			lex.nextLex();
		}
		else if (lex.tokenCategory() == Lexer.realToken) {
			result = new RealNode(new Float(lex.tokenText()));
			lex.nextLex();
		}
		else if (lex.tokenCategory() == Lexer.stringToken) {
			result = new StringNode(new String(lex.tokenText()));
			lex.nextLex();
		}
		else if (lex.isIdentifier()) {
			result = reference(sym);
			//result.genCode();
			if (lex.match("(")) {
				if(! (result.type instanceof FunctionType) )
					parseError(45);
				lex.nextLex();
				Vector params = parameterList(sym);
				if (! lex.match(")"))
					parseError(22);
				lex.nextLex();
				result = new FunctionCallNode(result, params);
			} else {
				if(result.type instanceof AddressType ) {
					result = new UnaryNode(UnaryNode.dereference, addressBaseType(result.type), result);
				} else {
				//Result is unchanged from the reference call				
				}			
			}			
		}
		else
			parseError(33);
		stop("term");
		return result;
	}

	private Type addressBaseType(Type t) throws ParseException {
		if (! (t instanceof AddressType))
			parseError(37);
		AddressType at = (AddressType) t;
		return at.baseType;
	}

	private Ast reference (SymbolTable sym) throws ParseException {
		start("reference");
		Ast result = null;
		if (! lex.isIdentifier())
			parseError(27);
		result = sym.lookupName(new FramePointer(), lex.tokenText());
		lex.nextLex();
		while (lex.match("^") || lex.match(".") || lex.match("[")) {
			if (lex.match("^")) {
				Type b = addressBaseType(result.type);
				if ( !(b instanceof PointerType) )
					parseError(38);
				PointerType pb = (PointerType) b;
				result = new UnaryNode(UnaryNode.dereference,
					new AddressType(pb.baseType), result);
				lex.nextLex();
				}
			else if (lex.match(".")) {
				lex.nextLex();
				if (! lex.isIdentifier())
					parseError(27);
				Type b = addressBaseType(result.type);
				if ( !(b instanceof ClassType) )
					parseError(39);
				ClassType pb = (ClassType) b;
				if (! pb.symbolTable.nameDefined(lex.tokenText()))
				   throw new ParseException(29);
				result = pb.symbolTable.lookupName(result, lex.tokenText());
				lex.nextLex();
				}
			else {
				lex.nextLex();
				Ast indexExpression = expression(sym);
				Type b = addressBaseType(result.type);
				if ( !(b instanceof ArrayType) )
					parseError(40);
				ArrayType at = (ArrayType) b;
				if (! indexExpression.type.equals(
					PrimitiveType.IntegerType))
						parseError(41);
				indexExpression = new BinaryNode(
					BinaryNode.minus, 
					PrimitiveType.IntegerType,
					indexExpression, 
						new IntegerNode(at.lowerBound));
				indexExpression = new BinaryNode(
					BinaryNode.times, 
					PrimitiveType.IntegerType,
					indexExpression, 
						new IntegerNode(at.elementType.size()));
				result = new BinaryNode(
					BinaryNode.plus, 
					new AddressType(at.elementType),
					result,
					indexExpression);
				if (! lex.match("]"))
					parseError(24);
				lex.nextLex();
				}
			}
		stop("reference");
		return result;
		}

}
