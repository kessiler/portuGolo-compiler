package br.com.compiler.portugolo.symbol;


import br.com.compiler.portugolo.common.Tag;
import br.com.compiler.portugolo.common.Token;

import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, Token> table;

    public SymbolTable() {
        table = new HashMap<String, Token>();

        table.put("algoritmo", new Token(Tag.ALGORITMO, 0, 0));
        table.put("declare", new Token(Tag.DECLARE, 0, 0));
        table.put("fim", new Token(Tag.FIM, 0, 0));
        table.put("subrotina", new Token(Tag.SUBROTINA, 0, 0));
        table.put("retorne", new Token(Tag.RETORNE, 0, 0));
        table.put("logico", new Token(Tag.TIPO_LOGICO, 0, 0));
        table.put("numerico", new Token(Tag.TIPO_NUMERICO, 0, 0));
        table.put("literal", new Token(Tag.TIPO_LITERAL, 0, 0));
        table.put("danadanao", new Token(Tag.TIPO_DANADANAO, 0, 0));
        table.put("nao", new Token(Tag.NAO, 0, 0));
        table.put("se", new Token(Tag.SE, 0, 0));
        table.put("inicio", new Token(Tag.INICIO, 0, 0));
        table.put("senao", new Token(Tag.SENAO, 0, 0));
        table.put("enquanto", new Token(Tag.ENQUANTO, 0, 0));
        table.put("para", new Token(Tag.PARA, 0, 0));
        table.put("faca", new Token(Tag.FACA, 0, 0));
        table.put("ate", new Token(Tag.ATE, 0, 0));
        table.put("repita", new Token(Tag.REPITA, 0, 0));
        table.put("escreva", new Token(Tag.ESCREVA, 0, 0));
        table.put("leia", new Token(Tag.LEIA, 0, 0));
        table.put("verdadeiro", new Token(Tag.VERDADEIRO, 0, 0));
        table.put("falso", new Token(Tag.FALSO, 0, 0));
        table.put("ou", new Token(Tag.OU, 0, 0));
        table.put("e", new Token(Tag.E, 0, 0));
    }

    public Token get(String lexeme) {
        return table.get(lexeme);
    }

    public void put(String lexeme, Token token) {
        table.put(lexeme, token);

    }

    public boolean has(String lexeme) {
        return table.containsKey(lexeme);
    }

}
