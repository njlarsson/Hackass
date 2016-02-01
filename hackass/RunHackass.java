import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class RunHackass {
    public static void main(String[] args) throws IOException {
        String infnam = args[0];
        String outfnam = args[1];
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(infnam));
        HackassLexer lexer = new HackassLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HackassParser parser = new HackassParser(tokens);
        ParseTree tree = parser.file();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new Assembler(infnam, outfnam), tree);
        System.out.println();
    }
}
