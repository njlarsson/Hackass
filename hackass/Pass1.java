package hackass;

import hackass.grammar.*;
import java.io.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Pass1 extends BaseAssembler {
    private int curCodeAddr = 0;

    Pass1(String infnam, HashMap<String, Integer> symtab) throws IOException {
        super(infnam, symtab);
    }
    
    @Override
    public void enterLabel(HackassParser.LabelContext ctx) {
        defSym(ctx.ID().getSymbol(), curCodeAddr);
    }
    
    @Override
    public void enterAinstr(HackassParser.AinstrContext ctx) {
        curCodeAddr++;
    }
    
    @Override
    public void enterCinstr(HackassParser.CinstrContext ctx) {
        curCodeAddr++;
    }
}
