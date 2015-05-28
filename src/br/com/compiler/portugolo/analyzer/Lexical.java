package br.com.compiler.portugolo.analyzer;

import br.com.compiler.portugolo.common.Tag;
import br.com.compiler.portugolo.common.Token;
import br.com.compiler.portugolo.symbol.SymbolTable;

import javax.swing.JTextArea;

public class Lexical {
    private final String sourceCode;
    private final SymbolTable table;
    private final JTextArea consoleOutput;
    private int position = 0;
    private int column = 0;
    private int line = 0;

    public Lexical(String sourceCode, SymbolTable table, JTextArea consoleOutput) {
        this.sourceCode = sourceCode;
        this.table = table;
        this.consoleOutput = consoleOutput;
    }

    // Reporta erro lexico ao usuario
    public void erroLexico(int line, int column, String mensagem) {
        this.consoleOutput.append("Erro lexico na line " + line + " e na column " + column + ":");
        this.consoleOutput.append(mensagem + "\n");
    }

    // Volta uma posição do carro de leitura
    public void retornaPonteiro(char c) {
        if (c == '\n') {
            this.line--;
        } else {
            this.column--;
        }
        this.position--;
    }

    // Obtem proximo token
    public Token proxToken() {
        StringBuilder lexema = new StringBuilder();
        int estado = 0;
        int comecoLin = 0;
        int comecoCol = column;
        char c;

        while (position < sourceCode.length()) {

            c = sourceCode.charAt(position);
            position++;
            column++;
            if (c == '\n') {
                line++;
                column = 0;
            }// Um tab desloca a column 4 posicoes
            else if (c == '\t') {
                column += 4;
            }
            switch (estado) {
                case 0:
                    comecoLin = line;
                    if (c == '\0') { // Usando abordagem de C para caractere de fim de String
                        return new Token(Tag.EOF, "EOF", comecoLin, comecoCol);
                    }
                    comecoCol = c == '\n' ? column : column - 1;
                    if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\b') {
                        // Permance no estado = 0;
                    } else if (Character.isLetter(c)) {
                        lexema.append(c);
                        estado = 2;
                    } else if (Character.isDigit(c)) {
                        lexema.append(c);
                        estado = 4;
                    } else if (c == '\"') {
                        lexema.append(c);
                        estado = 9;
                    } else if (c == ';') { // Estado 11
                        return new Token(Tag.PONTO_VIRGULA, ";", comecoLin, comecoCol);
                    } else if (c == '(') { // Estado 12
                        return new Token(Tag.ABRE_PARENTESES, "(", comecoLin, comecoCol);
                    } else if (c == ')') { // Estado 13
                        return new Token(Tag.FECHA_PARENTESES, ")", comecoLin, comecoCol);
                    } else if (c == '[') { // Estado 14
                        return new Token(Tag.ABRE_COLCHETES, "[", comecoLin, comecoCol);
                    } else if (c == ']') { // Estado 15
                        return new Token(Tag.FECHA_COLCHETES, "]", comecoLin, comecoCol);
                    } else if (c == ',') { // Estado 16
                        return new Token(Tag.VIRGULA, ",", comecoLin, comecoCol);
                    } else if (c == '>') {
                        estado = 17;
                    } else if (c == '<') {
                        estado = 20;
                    } else if (c == '=') { // Estado 26
                        return new Token(Tag.IGUAL, "=", comecoLin, comecoCol);
                    } else if (c == '/') {
                        estado = 27;
                    } else if (c == '*') { // Estado 33
                        return new Token(Tag.MULTIPLICACAO, "*", comecoLin, comecoCol);
                    } else if (c == '-') { // Estado 34
                        return new Token(Tag.SUBTRACAO, "-", comecoLin, comecoCol);
                    } else if (c == '+') { // Estado 35
                        return new Token(Tag.ADICAO, "+", comecoLin, comecoCol);
                    } else {
                        erroLexico(line, column, "Caractere Invalido: " + c);
                        lexema = new StringBuilder();
                        estado = 0;
                    }
                    break;
                case 2:
                    if (c == '_' || Character.isLetterOrDigit(c)) {
                        lexema.append(c);
                        // Permanece no Estado 02
                    } else { // Outro = Estado 04
                        retornaPonteiro(c);
                        // Verificando se o token ja esta na table de simbolos (Trata os acentos e maiusculas/minusculas)
                        Token token = table.get(new RemovedorAcentos().remover(lexema.toString()).toLowerCase());
                        if (token == null) {
                            token = new Token(Tag.ID, comecoLin, comecoCol);
                        } else {
                            token.setLine(comecoLin);
                            token.setColumn(comecoCol);
                        }
                        token.setLexeme(lexema.toString());
                        return token;
                    }
                    break;
                case 4:
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        // Permanece no Estado 04
                    } else if (c == '.') {
                        lexema.append(c);
                        estado = 5;
                    } else { // Outro = Estado 06
                        retornaPonteiro(c);
                        return new Token(Tag.NUMERICO_INT, lexema.toString(), comecoLin, comecoCol);
                    }
                    break;
                case 5:
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        estado = 7;
                    } else {
                        erroLexico(comecoLin, comecoCol, "Numerico real com formato invalido: " + lexema.toString());
                        lexema = new StringBuilder();
                        estado = 0;
                    }
                    break;
                case 7:
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        // Permanece no Estado 07
                    } else { // Outro = Estado 08
                        retornaPonteiro(c);
                        return new Token(Tag.NUMERICO_REAL, lexema.toString(), comecoLin, comecoCol);
                    }
                    break;
                case 9:
                    lexema.append(c);
                    if (c == '\"') { // Estado 10
                        return new Token(Tag.CONST_STRING, lexema.toString(), comecoLin, comecoCol);
                    } else if (c == '\0') {
                        erroLexico(line, column, "A string deve ser fechada com \" antes do fim de arquivo: " + lexema.toString());
                        lexema = new StringBuilder();
                        estado = 0;
                    } else {
                        // Permanece no Estado 09
                    }
                    break;
                case 17:
                    if (c == '=') { // Estado 18
                        return new Token(Tag.MAIOR_IGUAL, ">=", comecoLin, comecoCol);
                    } else { // Outro = Estado 19
                        retornaPonteiro(c);
                        return new Token(Tag.MAIOR, ">", comecoLin, comecoCol);
                    }
                case 20:
                    if (c == '=') { // Estado 21
                        return new Token(Tag.MENOR_IGUAL, "<=", comecoLin, comecoCol);
                    } else if (c == '>') { // Estado 22
                        return new Token(Tag.DIFERENTE, "<>", comecoLin, comecoCol);
                    } else if (c == '-') {
                        estado = 23;
                    } else { // Outro = Estado 25
                        retornaPonteiro(c);
                        return new Token(Tag.MENOR, "<", comecoLin, comecoCol);
                    }
                    break;
                case 23:
                    if (c == '-') { // Estado 24
                        return new Token(Tag.ATRIBUICAO, "<--", comecoLin, comecoCol);
                    } else {
                        retornaPonteiro(c);
                        erroLexico(line, column, "Comando incorreto para atribuição: <-");
                        estado = 0;
                    }
                    break;
                case 27:
                    if (c == '*') {
                        estado = 31;
                    } else if (c == '/') {
                        estado = 29;
                    } else { // Outro = Estado 28
                        retornaPonteiro(c);
                        return new Token(Tag.DIVISAO, "/", comecoLin, comecoCol);
                    }
                    break;
                case 29:
                    if (c == '\n' || c == '\r') {
                        estado = 0;
                    } else {
                        // Permanece no Estado 29
                    }
                    break;
                case 31:
                    if (c == '*') {
                        estado = 32;
                    } else if (c == '\0') {
                        erroLexico(line, column, "O comentario deve ser fechada com */ antes do fim de arquivo");
                        estado = 0;
                    } else {
                        // Permanece no Estado 31
                    }
                    break;
                case 32:
                    if (c == '/') {
                        estado = 0;
                    } else if (c == '\0') {
                        erroLexico(line, column, "O comentario deve ser fechada com */ antes do fim de arquivo");
                        estado = 0;
                    } else {
                        estado = 31;
                    }
                    break;
            }
        }
        return null;
    }

    private class RemovedorAcentos {

        private final String acentuado = "çÇáéíóúýÁÉÍÓÚÝàèìòùÀÈÌÒÙãõñäëïöüÿÄËÏÖÜÃÕÑâêîôûÂÊÎÔÛ";
        private final String semAcento = "cCaeiouyAEIOUYaeiouAEIOUaonaeiouyAEIOUAONaeiouAEIOU";
        private final char[] table = new char[256];

        public RemovedorAcentos() {
            for (int i = 0; i < table.length; ++i) {
                table[i] = (char) i;
            }
            for (int i = 0; i < acentuado.length(); ++i) {
                table[acentuado.charAt(i)] = semAcento.charAt(i);
            }
        }

        public String remover(String palavra) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < palavra.length(); ++i) {
                char ch = palavra.charAt(i);
                if (ch < 256) {
                    sb.append(table[ch]);
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
    }
}

