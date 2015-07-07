package br.com.compiler.portugolo.ui.highlighter.tokenMarker;

import br.com.compiler.portugolo.ui.highlighter.component.KeywordMap;

public class PortuGoloTokenMarker extends CTokenMarker {
    private static KeywordMap portugoloKeywords;

    public PortuGoloTokenMarker() {
        super(false, getKeywords());
    }

    public static KeywordMap getKeywords() {
        if (portugoloKeywords == null) {
            portugoloKeywords = new KeywordMap(true);
            portugoloKeywords.add("algoritmo", Token.KEYWORD1);
            portugoloKeywords.add("fim", Token.KEYWORD1);
            portugoloKeywords.add("subrotina", Token.KEYWORD1);
            portugoloKeywords.add("retorne", Token.KEYWORD1);
            portugoloKeywords.add("se", Token.KEYWORD1);
            portugoloKeywords.add("para", Token.KEYWORD1);
            portugoloKeywords.add("ate", Token.KEYWORD1);
            portugoloKeywords.add("faca", Token.KEYWORD1);
            portugoloKeywords.add("declare", Token.KEYWORD1);
            portugoloKeywords.add("inicio", Token.KEYWORD1);
            portugoloKeywords.add("repita", Token.KEYWORD1);
            portugoloKeywords.add("escreva", Token.KEYWORD1);

            portugoloKeywords.add("logico", Token.KEYWORD3);
            portugoloKeywords.add("numerico", Token.KEYWORD3);
            portugoloKeywords.add("literal", Token.KEYWORD3);
            portugoloKeywords.add("danadanao", Token.KEYWORD3);

            portugoloKeywords.add("+", Token.OPERATOR);
            portugoloKeywords.add("-", Token.OPERATOR);
            portugoloKeywords.add("*", Token.OPERATOR);
            portugoloKeywords.add("/", Token.OPERATOR);

        }
        return portugoloKeywords;
    }
}
