package hackass;

import hackass.grammar.*;
import java.io.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Pass2 extends Assembler {
    // Encapsulates IOException, to throw from overridden methods.
    static class IORuntimeException extends RuntimeException {
        final IOException iox;
        IORuntimeException(IOException iox) { this.iox = iox; }
    }

    private static final HashMap<String, Integer> jumpCodes = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> destCodes = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> compCodes = new HashMap<String, Integer>();
    private static final int varBase = 1024;

    static {
        jumpCodes.put("JGT", 0b001);
        jumpCodes.put("JEQ", 0b010);
        jumpCodes.put("JGE", 0b011);
        jumpCodes.put("JLT", 0b100);
        jumpCodes.put("JNE", 0b101);
        jumpCodes.put("JLE", 0b110);
        jumpCodes.put("JMP", 0b111);
        compCodes.put("0",   0b0101010);
        compCodes.put("1",   0b0111111);
        compCodes.put("-1",  0b0111010);
        compCodes.put("D",   0b0001100);
        compCodes.put("A",   0b0110000);
        compCodes.put("!D",  0b0001101);
        compCodes.put("!A",  0b0110001);
        compCodes.put("-D",  0b0001111);
        compCodes.put("-A",  0b0110011);
        compCodes.put("D+1", 0b0011111);
        compCodes.put("A+1", 0b0110111);
        compCodes.put("D-1", 0b0001110);
        compCodes.put("A-1", 0b0110010);
        compCodes.put("D+A", 0b0000010);
        compCodes.put("D-A", 0b0010011);
        compCodes.put("A-D", 0b0000111);
        compCodes.put("D&A", 0b0000000);
        compCodes.put("D|A", 0b0010101);
        compCodes.put("M",   0b1110000);
        compCodes.put("!M",  0b1110001);
        compCodes.put("-M",  0b1110011);
        compCodes.put("M+1", 0b1110111);
        compCodes.put("M-1", 0b1110010);
        compCodes.put("D+M", 0b1000010);
        compCodes.put("D-M", 0b1010011);
        compCodes.put("M-D", 0b1000111);
        compCodes.put("D&M", 0b1000000);
        compCodes.put("D|M", 0b1010101);
    }

    private final Writer out;
    
    private int curVarAddr = varBase;
    private int dest, comp, jump;

    Pass2(String infnam, String outfnam, HashMap<String, Integer> symtab) throws IOException {
        super(infnam, symtab);
        out = new OutputStreamWriter(new FileOutputStream(outfnam), "US-ASCII");
    }

    private void write(String s) {
        try {
            out.write(s);
        } catch (IOException iox) {
            throw new IORuntimeException(iox);
        }
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
        String binstr = Integer.toBinaryString(a);
        for (int i = binstr.length(); i < 16; i++) {
            write("0");         // pad with initial zeros
        }
        write(binstr);          // write "0" + a in binary
        write("\n");
        System.out.println(a);
    }

    @Override
    public void enterCinstr(HackassParser.CinstrContext ctx) {
        dest = 0;
        comp = 0;
        jump = 0;
    }
        
    @Override
    public void exitCinstr(HackassParser.CinstrContext ctx) {
        int c = 0b1110000000000000 | comp << 6 | dest << 3 | jump;
        System.out.println(comp + " " + dest + " " + jump);
        write(Integer.toBinaryString(c));
        write("\n");
    }
    
    @Override
    public void enterDest(HackassParser.DestContext ctx) {
        String mnem = ctx.getText();
        if (mnem.indexOf('M') >= 0) { dest |= 0b001; }
        if (mnem.indexOf('D') >= 0) { dest |= 0b010; }
        if (mnem.indexOf('A') >= 0) { dest |= 0b100; }
    }
    
    @Override
    public void enterComp(HackassParser.CompContext ctx) {
        Integer i = compCodes.get(ctx.getText());
        if (i == null) {
            error(ctx.getStart().getLine(), "Invalid comp: " + ctx.getText());
        }
        comp = i;
    }
    
    @Override
    public void enterJump(HackassParser.JumpContext ctx) {
        Integer i = jumpCodes.get(ctx.getText());
        if (i == null) {
            error(ctx.getStart().getLine(), "Invalid jump: " + ctx.getText());
        }
        jump = i;
    }
}
