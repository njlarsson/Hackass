import java.io.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Assembler extends HackassBaseListener {
    // Encapsulates IOException, to throw from overridden methods.
    static class IORuntimeException extends RuntimeException {
        final IOException iox;
        IORuntimeException(IOException iox) { this.iox = iox; }
    }
        
    private static final int varBase = 1024;

    private final String infnam;
    private final Writer out;
    
    private int curCodeAddr = 0;
    private int curVarAddr = varBase;
    private HashMap<String, Integer> symtab = new HashMap<String, Integer>();

    Assembler(String infnam, String outfnam) throws IOException {
        this.infnam = infnam;
        out = new OutputStreamWriter(new FileOutputStream(outfnam), "US-ASCII");
    }

    private void error(int line, String msg) {
        System.err.println(infnam + ":" + line + ": " + msg);
    }
    
    private void defSym(Token tok, int a) {
        String sym = tok.getText();
        Object old = symtab.put(sym, a);
        if (old != null) {
            error(tok.getLine(), "redefined " + sym);
        }
    }

    private int getSym(Token tok) {
        String sym = tok.getText();
        Integer a = symtab.get(sym);
        if (a == null) {
            error(tok.getLine(), "undefined " + sym);
            return 0;
        } else {
            return a;
        }
    }

    private void write(String s) {
        try {
            out.write(s);
        } catch (IOException iox) {
            throw new IORuntimeException(iox);
        }
    }

    // Returns a binary string zero-padded to w digits.
    private String binStr(int x, int w) {
        return String.format("%" + w + "s", Integer.toBinaryString(x)).replace(' ', '0');
    }

    @Override
    public void exitFile(HackassParser.FileContext ctx) {
        try {
            out.close();
        } catch (IOException iox) {
            throw new IORuntimeException(iox);
        }
    }

    @Override
    public void enterLabel(HackassParser.LabelContext ctx) {
        defSym(ctx.ID().getSymbol(), curCodeAddr);
    }
    
    @Override
    public void enterVar(HackassParser.VarContext ctx) {
        defSym(ctx.ID().getSymbol(), curVarAddr++);
    }

    @Override
    public void enterAinstr(HackassParser.AinstrContext ctx) {
        int a;
        if (ctx.ID() != null) { // is it an ID?
            a = getSym(ctx.ID().getSymbol());
        } else {                // no? then it's an integer
            TerminalNode node = ctx.ZERONE() != null ? ctx.ZERONE() : ctx.INT();
            a = Integer.parseInt(node.getText());
        }
        write(binStr(a, 16));   // write "0" + a in binary
        write("\n");
    }
}
