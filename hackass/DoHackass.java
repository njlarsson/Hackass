package hackass;

import hackass.grammar.*;
import java.io.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class DoHackass {
    public static void main(String[] args) throws IOException {
        String infnam = args[0];
        String outfnam = args[1];
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(infnam));
        HackassLexer lexer = new HackassLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HackassParser parser = new HackassParser(tokens);
        ParseTree tree = parser.file();
        ParseTreeWalker walker = new ParseTreeWalker();

        HashMap<String, Integer> symtab = new HashMap<String, Integer>();        
        walker.walk(new Pass1(infnam, symtab), tree);
        walker.walk(new Pass2(infnam, outfnam, symtab), tree);
    }
}
