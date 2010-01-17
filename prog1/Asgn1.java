//
//	CS 480/580
//		Driver for programming assignment 1
//		Written by Tim Budd, Winter Term 2006
//

import java.io.*;

class Asgn1 {
	public static void main(String [ ] args) {
		System.out.println("Reading file " + args[0]);
		try {
			FileReader instream = new FileReader(args[0]);
			Lexer lex = new Lexer(instream);
			lex.nextLex();
			while (lex.tokenCategory() != lex.endOfInput) {
				System.out.println("Token: " + lex.tokenText() + 
					" category " + lex.tokenCategory());
				lex.nextLex();
				}
		} 
		catch (ParseException e) 
			{ System.out.println(e.toString()); }
		catch(FileNotFoundException e) 
			{ System.out.println("File not found " + e); }
		catch(IOException e) 
			{ System.out.println("File IO Exception " + e);}
	}
}
