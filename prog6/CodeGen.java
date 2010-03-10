// Modified by Michael Anderson, Sam Heinith, & Rob McGuire-Dale

import java.util.Vector;

class CodeGen {
	
	static void gen (String op) {
		System.out.println("\t" + op);
		}

	static void gen (String op, String a) {
		System.out.println("\t" + op + "\t" + a);
		}

	static void gen (String op, String a, String b) {
		System.out.println("\t" + op + "\t" + a + "," + b);
		}

	static private Label endLabel;
	static private Vector stringLabel;
	static private Vector constantTable;

	static void genProlog (String name, int size) {
		// put your code here
		gen(".globl", name);
		gen(".type", name, "@function");
		System.out.println(name + ':');
		gen("pushl", "%ebp");
		gen("movl",	"%esp", "%ebp");
		if(size != 0)
			gen("subl",	"$" + String.valueOf(size), "%esp");
		// end of your code
		endLabel = new Label();
		constantTable = new Vector();
		stringLabel = new Vector();
		}

	static void addConstant(Label l, Object s) {
		stringLabel.addElement(l);
		constantTable.addElement(s);
		}

	static void genEpilog (String name) {
		endLabel.genCode();
		gen("leave");
		gen("ret");
		for (int i = 0; i < constantTable.size(); i++) {
			gen(".align","4");
			Label l = (Label) stringLabel.elementAt(i);
			l.genCode();
			Object v = constantTable.elementAt(i);
			if (v instanceof String)
				gen(".string", "\"" + v + "\"");
			else if (v instanceof Double)
				gen(".float", "" + v);
			}
		}

	static void genGlobal (String name, int size) {
		// put your code here
		gen(".comm", name, String.valueOf(size));
		}

	static void genAssign (Ast left, Ast right) {
		// put your code here
		if((left instanceof BinaryNode) && (((BinaryNode)left).isSum() != null) &&
		 (((BinaryNode)left).LeftChild instanceof FramePointer) &&
		 (((BinaryNode)left).RightChild.isConstant()) ) {
			right.genCode();
			if(((BinaryNode)left).RightChild instanceof RealNode) {
				String constString = String.valueOf(((BinaryNode)left).RightChild.cValue());
				gen("flds",	"0(%esp)");
				gen("addl",	"$4,%esp");
				gen("fstps", constString + "(%ebp)");
			}
			else {
				String constString = String.valueOf(((BinaryNode)left).RightChild.cValue());
				gen("popl", constString + "(%ebp)");
			}
		}
		else if(left instanceof GlobalNode) {
			right.genCode();
			gen("popl",	"%eax");
			gen("movl",	"%eax", ((GlobalNode)left).name);
		}
		else {
			left.genCode();
			right.genCode();
			if(left instanceof RealNode) {
				gen("flds",	"0(%esp)");
				gen("addl", "$4", "%esp");
				gen("popl",	"%ecx");
				gen("fstps", "0(%ecx)");
			}
			else {
				gen("popl", "%eax");
				gen("popl", "%ecx");
				gen("movl",	"%eax", "0(%ecx)");				
			}
		}
	}

	static void genReturn (Ast e) {
		// put your code here
		if (e != null) {
			e.genCode();
			if( e.type.equals(PrimitiveType.RealType)) {
				gen("flds", "0(%esp)");
				gen("addl", "$4", "%esp");
				//gen("jmp", endLabel.toString());
			}
			else {
				gen("popl", "%eax");
				//gen("jmp", endLabel.toString());
			}
		}
		endLabel.genBranch();
	}
}

class Label {
	static int number = 0;
	public int n;

	Label () { n = ++number; }

	public String toString () { return ".L" + n; }

	public void genCode () { System.out.println(toString()+":"); }

	public void genBranch () { genBranch("jmp"); }

	public void genBranch (String cond) { 
		CodeGen.gen(cond, toString());
	}
}

