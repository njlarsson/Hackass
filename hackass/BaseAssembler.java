package hackass;

import hackass.grammar.*;
import java.io.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

abstract class BaseAssembler extends HackassBaseListener {
    private final String infnam;
    private final HashMap<String, Integer> symtab;
    
    BaseAssembler(String infnam, HashMap<String, Integer> symtab) throws IOException {
        this.infnam = infnam;
        this.symtab = symtab;
    }

    protected void error(int line, String msg) {
        System.err.println(infnam + ":" + line + ": " + msg);
    }
    
    protected void defSym(Token tok, int a) {
        String sym = tok.getText();
        Object old = symtab.put(sym, a);
        if (old != null) {
            error(tok.getLine(), "redefined " + sym);
        }
    }

    protected int getSym(Token tok) {
        String sym = tok.getText();
        Integer a = symtab.get(sym);
        if (a == null) {
            error(tok.getLine(), "undefined " + sym);
            return 0;
        } else {
            return a;
        }
    }
}
