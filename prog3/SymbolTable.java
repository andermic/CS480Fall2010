import java.util.*;

//
//	written (and rewritten) by Tim Budd
//

interface SymbolTable {
	
		// methods to enter values into symbol table
	public void enterConstant (String name, Ast value);
	public void enterType (String name, Type type);
	public void enterVariable (String name, Type type);
	public void enterFunction (String name, FunctionType ft);
	public int size();

		// methods to search the symbol table
	public boolean nameDefined (String name);
	public Type lookupType (String name) throws ParseException;
	public Ast lookupName (Ast base, String name) throws ParseException;
	public Symbol findSymbol (String name);
}

class GlobalSymbolTable implements SymbolTable {
	private Hashtable<String, Symbol> table = new Hashtable<String, Symbol>();
	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type)
		{ enterSymbol (new GlobalSymbol(name, new AddressType(type), name)); }

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	private void enterSymbol (Symbol s) {
		// this if for you to figure out.
		// how should a symbol be stored?
		// ..!
		table.put(s.name, s);
	}

	public Symbol findSymbol (String name) {
		return (Symbol) table.get(name);
	}

	public boolean nameDefined (String name) {
		Symbol s = findSymbol(name);
		if (s != null) return true;
		else return false;
	}

	public Type lookupType (String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof TypeSymbol)) {
			TypeSymbol ts = (TypeSymbol) s;
			return ts.type;
			}
		throw new ParseException(30);
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if (s == null)
			throw new ParseException(41, name);
		// now have a valid symbol
		if (s instanceof GlobalSymbol) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if (s instanceof ConstantSymbol) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		return null; // should never happen
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
}

class FunctionSymbolTable implements SymbolTable {
	SymbolTable surrounding = null;
	private ArrayList<Symbol> locals = new ArrayList<Symbol>();
	private ArrayList<Symbol> parameters = new ArrayList<Symbol>();
	FunctionSymbolTable (SymbolTable st) { surrounding = st; }

	private int localOffset = 0;
	private int paramOffset = 8;
	
	public void enteringParameters (boolean flag) { //
		
	}

	public Type parameterType (int index) { //
		return null;
	}
	
	
	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type)
	{
		// this is for you to figure out.
		// I'll leave a stub, which you should
		// replace with the real thing
		if(doingArguments == true) {
			enterSymbol(new OffsetSymbol(name, new AddressType(type), paramOffset));
			paramOffset += type.size();
		}
		else {
			localOffset -= type.size();
			enterSymbol(new OffsetSymbol(name, new AddressType(type), localOffset));
		}		
	}

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	public boolean doingArguments = true;

	private void enterSymbol (Symbol s) {
		// you can just copy from the first one
		//System.out.println("name = " + s.name);
		if(doingArguments == true) {
			parameters.add(s);
			//System.out.println("Adding to parameters "+s.name);
		}
		else {
			locals.add(s);
			//System.out.println("Adding to locals "+s.name+s.type.baseType.size());
		}
	}

	public Symbol findSymbol (String name) {
		ArrayList<Symbol> list = new ArrayList<Symbol>();
		list.addAll(locals); 
		list.addAll(parameters);
		for(Symbol s : list) {
			//System.out.println(s.name + " vs " + name);
			if(s.name.equals(name))
				return s;
		}		
		return null;
	}

	public boolean nameDefined (String name) {
		Symbol s = findSymbol(name);
		if (s != null) return true;
		else return false;
	}

	public Type lookupType (String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof TypeSymbol)) {
			TypeSymbol ts = (TypeSymbol) s;
			return ts.type;
			}
		// note how we check the surrounding scopes
		return surrounding.lookupType(name);
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if (s == null)
			return surrounding.lookupName(base, name);
		// we have a symbol here
		if (s instanceof GlobalSymbol) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if (s instanceof OffsetSymbol) {
			OffsetSymbol os = (OffsetSymbol) s;
			return new BinaryNode(BinaryNode.plus, os.type,
				base, new IntegerNode(os.location));
			}
		if (s instanceof ConstantSymbol) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		return null; // should never happen
	}

	public int size() {
		int size = 0;
		for(Symbol s : locals) {
			Type t = ((OffsetSymbol) s).type;
			size += ((AddressType)t).baseType.size();
		}	    
		return size;
	}
}






class ClassSymbolTable implements SymbolTable {
	private SymbolTable surround = null;
	private ArrayList<Symbol> fields = new ArrayList<Symbol>();
	private int offset = 0;
	
	ClassSymbolTable (SymbolTable s) { surround = s; }

	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type)
		{ 
			// again, you need to do something different here.
			enterSymbol(new OffsetSymbol(name, new AddressType(type), offset));
			System.out.println("Entering symbol into ClassSymbolTable: "+name);
			offset += type.size();
		}

	public void enterFunction (String name, FunctionType ft) 
		// this should really be different as well,
		// but we will leave alone for now
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }


	private void enterSymbol (Symbol s) {
		// this if for you to figure out.
		// how should a symbol be stored?
		// ..!
		fields.add(s);
	}
		
	public Symbol findSymbol (String name) {
 		for(Symbol s : fields) {
 			//System.out.println(s.name + " vs " + name);
			if(s.name.equals(name))
				return s;
		}	
		return null;
	}
	

	public boolean nameDefined (String name) {
		Symbol s = findSymbol(name);
		if (s != null) return true;
		else return false;
	}

	public Type lookupType (String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof TypeSymbol)) {
			TypeSymbol ts = (TypeSymbol) s;
			return ts.type;
			}
		return surround.lookupType(name);
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if (s == null)
			return surround.lookupName(base, name);
		// else we have a symbol here
		if (s instanceof GlobalSymbol) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if (s instanceof OffsetSymbol) {
			OffsetSymbol os = (OffsetSymbol) s;
			return new BinaryNode(BinaryNode.plus, os.type,
				base, new IntegerNode(os.location));
			}
		if (s instanceof ConstantSymbol) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		return null; // should never happen
	}

	public int size() {
		/*int size = 0;
		for(Symbol s : fields) {
			Type t = null;
			if(s instanceof OffsetSymbol)
				t = ((OffsetSymbol) s).type;
			else if(s instanceof ConstantSymbol)
				t = ((ConstantSymbol) s).value.type;
			else {
				System.out.println("Hello");
				return 0;
			}
			
			
			size += ((AddressType)t).baseType.size();
		}	    
		return size;*/
		return offset;

	}
}
