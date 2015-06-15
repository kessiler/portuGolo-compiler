package br.com.compiler.portugolo.ui.highlighter.tokenMarker;

import br.com.compiler.portugolo.ui.highlighter.component.KeywordMap;

public class PortuGoloTokenMarker extends CTokenMarker {
    private static KeywordMap portugoloKeywords;

    public PortuGoloTokenMarker() {
        super(false, getKeywords());
    }

    public static KeywordMap getKeywords() {
        if (portugoloKeywords == null) {
            portugoloKeywords = new KeywordMap(false);
            portugoloKeywords.add("algoritmo", Token.KEYWORD2);
            portugoloKeywords.add("fim", Token.KEYWORD2);
            portugoloKeywords.add("logico", Token.KEYWORD3);
            portugoloKeywords.add("numerico", Token.KEYWORD3);
            portugoloKeywords.add("literal", Token.KEYWORD3);
            portugoloKeywords.add("danadanao", Token.KEYWORD3);
        }
        return portugoloKeywords;
    }
}
