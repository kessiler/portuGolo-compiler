package br.com.compiler.portugolo.common;

public class Token {
    private String lexeme;
    private int code;
    private int column;
    private int line;

    public Token(int code, String lexeme, int line, int column) {

        this.code = code;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    public Token(int code, int line, int column) {

        this.code = code;
        this.lexeme = "";
        this.line = line;
        this.column = column;
    }

    public int getCode() {

        return code;
    }

    public void setCode(int code) {

        this.code = code;
    }

    public int getColumn() {

        return column;
    }

    public void setColumn(int column) {

        this.column = column;
    }

    public String getLexeme() {

        return lexeme;
    }

    public void setLexeme(String lexeme) {

        this.lexeme = lexeme;
    }

    public int getLine() {

        return line;
    }

    public void setLine(int line) {

        this.line = line;
    }

    @Override
    public String toString() {
        String codeNumber = code < 10 ? "0" + code : "" + code;
        return lexeme + " | Token: " + codeNumber + " | Linha: " + line + " | Coluna: " + column + "\n";
    }

}
