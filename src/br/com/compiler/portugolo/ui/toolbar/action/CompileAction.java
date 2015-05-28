package br.com.compiler.portugolo.ui.toolbar.action;

import br.com.compiler.portugolo.analyzer.Lexical;
import br.com.compiler.portugolo.analyzer.Syntactic;
import br.com.compiler.portugolo.common.Node;
import br.com.compiler.portugolo.symbol.SymbolTable;
import br.com.compiler.portugolo.ui.CompilerUI;

public class CompileAction {
    public static boolean compile(CompilerUI frame) {
        SymbolTable symbolTable = new SymbolTable();
        Lexical lexical = new Lexical(frame.getTextEditor().getText() + "\0", symbolTable, frame.getTextMsg());
        Syntactic syntactic = new Syntactic(lexical, symbolTable, frame.getTextMsg());
        Node raiz = syntactic.Compilador();
        if (raiz != null) {
            raiz.printContent();
        }
        return frame.getTextMsg().getText().isEmpty();
    }
}
