package br.com.compiler.portugolo.analyzer;


import br.com.compiler.portugolo.common.Node;
import br.com.compiler.portugolo.common.Tag;
import br.com.compiler.portugolo.common.Token;
import br.com.compiler.portugolo.symbol.SymbolTable;

import javax.swing.JTextArea;

public class Syntactic {
    private final Lexical lexical;
    private final SymbolTable symbolTable;
    private final JTextArea consoleOutput;
    private Token token;
    private String expectedToken;
    private int numErrors;

    public Syntactic(Lexical lexical, SymbolTable symbolTable, JTextArea consoleOutput) {
        this.lexical = lexical;
        this.symbolTable = symbolTable;
        this.consoleOutput = consoleOutput;
        this.token = lexical.proxToken();
        this.numErrors = 0;
        this.expectedToken = null;
    }

    // Reporta erro sintatico ao usuario - Aqui deve ser tratado o Metodo do Panico
    public void erroSintatico(String esperado) throws Exception {
        this.consoleOutput.append("Erro sintatico na linha " + token.getLine() + " e na coluna " + token.getColumn());
        this.consoleOutput.append("\nEsperado: " + esperado + "\nEncontrado: " + token.getLexeme() + "\n");
        if (++numErrors > 7) {
            throw new Exception("Stopped by panic");
        } else {
            proxTokenAfterSync();
        }
    }

    private void proxTokenAfterSync() {
        token = lexical.proxToken();
        if (!casaToken(Tag.PONTO_VIRGULA) && !casaToken(Tag.FECHA_PARENTESES) && !casaToken(Tag.FIM) && token != null) {
            proxTokenAfterSync();
        }
    }

    public boolean casaToken(int code) {
        if (token.getCode() == code) {
            this.token = lexical.proxToken();
            return true;
        }
        return false;
    }

    public boolean isToken(int code) {
        return token.getCode() == code;
    }

    public Node compilador() throws Exception {
        Node noPrograma = programa();
        if (!casaToken(Tag.EOF)) {
            erroSintatico("Fim de Arquivo");
            return null;
        }
        return noPrograma;
    }

    public Node programa() throws Exception {
        Token algoritmo = token;
        if (casaToken(Tag.ALGORITMO)) {
            prepareToDeclara();
            listaCmd();
            if (!casaToken(Tag.FIM)) {
                erroSintatico("Fim");
                return null;
            } else if (!casaToken(Tag.ALGORITMO)) {
                erroSintatico("Algoritmo");
                return null;
            }
            listaRotina();
            return new Node(algoritmo);
        } else {
            erroSintatico("Algoritmo");
        }
        return null;
    }

    private void prepareToDeclara() throws Exception {
        if (casaToken(Tag.DECLARE)) {
            prepareToDeclaraVar();
        }
    }

    private void prepareToDeclaraVar() throws Exception {
        declaraVar();
        checkMoreDeclaraVar();
    }

    private void declaraVar() throws Exception {
        tipo();
        listaId();
        if (!casaToken(Tag.PONTO_VIRGULA)) {
            erroSintatico("Esperado ponto e virgula");
        }
    }

    private void checkMoreDeclaraVar() throws Exception {
        if (isToken(Tag.TIPO_LOGICO) || isToken(Tag.TIPO_NUMERICO) || isToken(Tag.TIPO_LITERAL) || isToken(Tag.TIPO_DANADANAO)) {
            prepareToDeclaraVar();
        }
    }

    private void listaRotina() throws Exception {
        if (isToken(Tag.SUBROTINA)) {
            rotina();
            listaRotina();
        }
    }

    void rotina() throws Exception {
        if (casaToken(Tag.SUBROTINA)) {
            if (casaToken(Tag.ID)) {
                if (casaToken(Tag.ABRE_PARENTESES)) {
                    listaParam();
                    if (casaToken(Tag.FECHA_PARENTESES)) {
                        prepareToDeclara();
                        listaCmd();
                        Retorno();
                        if (casaToken(Tag.FIM)) {
                            if (casaToken(Tag.SUBROTINA)) {
                                if (!casaToken(Tag.ID)) {
                                    erroSintatico("Esperado o nome da subrotina para ser finalizada");
                                }
                            } else {
                                erroSintatico("Esperado a tag subrotina para finalizar");
                            }
                        } else {
                            erroSintatico("Esperado a tag fim para finalizar a subrotina");
                        }
                    } else {
                        erroSintatico("Esperado fechamento de parenteses parametros da subrotina");
                    }
                } else {
                    erroSintatico("Esperado abertura de parenteses para declarar os parametros da subrotina");
                }
            } else {
                erroSintatico("Esperado o nome da subrotina");
            }
        } else {
            erroSintatico("Esperado a tag subrotina");
        }
    }

    private void listaParam() throws Exception {
        param();
        checkMoreParam();
    }

    private void checkMoreParam() throws Exception {
        if (casaToken(Tag.VIRGULA)) {
            listaParam();
        }
    }

    private void param() throws Exception {
        listaId();
        tipo();
    }

    private void listaId() throws Exception {
        Token auxToken = token;
        if (casaToken(Tag.ID)) {
            symbolTable.put(auxToken.getLexeme(), auxToken);
            checkMoreId();
        } else {
            erroSintatico("Esperado um identificador");
        }
    }

    private void checkMoreId() throws Exception {
        if (casaToken(Tag.VIRGULA)) {
            listaId();
        }
    }

    private void tipo() throws Exception {
        if (casaToken(Tag.ABRE_COLCHETES)) {
            Expressao();
            checkMoreExpressao();
            if (casaToken(Tag.FECHA_COLCHETES)) {
                tipoPrimitivo();
            } else {
                erroSintatico("Esperado fecha colchetes");
            }
        } else {
            tipoPrimitivo();
        }
    }

    private void tipoPrimitivo() throws Exception {
        if (!(casaToken(Tag.TIPO_LOGICO) || casaToken(Tag.TIPO_NUMERICO) || casaToken(Tag.TIPO_LITERAL) || casaToken(Tag.TIPO_DANADANAO))) {
            erroSintatico("Esperado um dos tipo primitivo");
        }
    }

    private void checkMoreExpressao() throws Exception {
        if (casaToken(Tag.VIRGULA)) {
            Expressao();
        }
    }

    private void listaCmd() throws Exception {
        cmd();
        if (isToken(Tag.SE) || isToken(Tag.ENQUANTO) || isToken(Tag.PARA)
                || isToken(Tag.REPITA) || isToken(Tag.ID)
                || isToken(Tag.ESCREVA) || isToken(Tag.LEIA)) {
            listaCmd();
        }
    }

    private void cmd() throws Exception {
        switch (token.getCode()) {
            case Tag.SE:
                cmdSe();
                break;
            case Tag.ENQUANTO:
                cmdEnquanto();
                break;
            case Tag.PARA:
                cmdPara();
                break;
            case Tag.REPITA:
                cmdRepita();
                break;
            case Tag.ID:
                prepareCmdAtribOrRotina();
                break;
            case Tag.ESCREVA:
                cmdEscreva();
                break;
            case Tag.LEIA:
                cmdLeia();
                break;
        }
    }

    private void prepareCmdAtribOrRotina() throws Exception {
        if (casaToken(Tag.ID)) {
            if (isToken(Tag.ABRE_PARENTESES)) {
                cmdChamaRotina();
            } else if (isToken(Tag.ATRIBUICAO)) {
                cmdAtrib();
            }
        } else {
            erroSintatico("identificador");
        }
    }

    private void cmdSe() throws Exception {
        if (casaToken(Tag.SE) && casaToken(Tag.ABRE_PARENTESES)) {
            Expressao();
            if (casaToken(Tag.FECHA_PARENTESES)) {
                if (casaToken(Tag.INICIO)) {
                    listaCmd();
                    if (casaToken(Tag.FIM)) {
                        checkSenao();
                    } else {
                        erroSintatico("Esperado 'fim'");
                    }
                } else {
                    erroSintatico("Esperado 'inicio'");
                }
            } else {
                erroSintatico("Esperado '}'");
            }
        }
    }

    private void checkSenao() throws Exception {
        if (isToken(Tag.SENAO)) {
            if (!casaToken(Tag.SENAO) && !casaToken(Tag.INICIO)) {
                erroSintatico("Esperado 'Senao'");
            } else {
                listaCmd();
                if (!casaToken(Tag.FIM)) {
                    erroSintatico("Esperado 'fim'");
                }
            }
        }
    }

    private void cmdEnquanto() throws Exception {
        if (!casaToken(Tag.ENQUANTO) && !casaToken(Tag.ABRE_PARENTESES)) {
            erroSintatico("Esperado 'enquanto'");
        } else {
            Expressao();
            expectedToken = null;
            if (!casaToken(Tag.FACA)) {
                expectedToken = "faça";
            } else if (casaToken(Tag.INICIO)) {
                expectedToken = "inicio";
            }
            if (expectedToken != null) {
                erroSintatico(expectedToken);
            } else {
                listaCmd();
                if (!casaToken(Tag.FIM)) {
                    erroSintatico("fim");
                }
            }
        }
    }

    private void cmdPara() throws Exception {
        if (casaToken(Tag.PARA)) {
            prepareCmdAtribOrRotina();
            if (casaToken(Tag.ATE)) {
                Expressao();
                expectedToken = null;
                if (!casaToken(Tag.FACA)) {
                    expectedToken = "faça";
                } else if (!casaToken(Tag.INICIO)) {
                    expectedToken = "inicio";
                }
                if (expectedToken != null) {
                    erroSintatico(expectedToken);
                } else {
                    listaCmd();
                    if (!casaToken(Tag.FIM)) {
                        erroSintatico("fim");
                    }
                }
            } else {
                erroSintatico("ate");
            }
        } else {
            erroSintatico("para");
        }
    }

    private void cmdRepita() throws Exception {
        if (casaToken(Tag.REPITA)) {
            listaCmd();
            if (casaToken(Tag.ATE)) {
                Expressao();
            } else {
                erroSintatico("ate");
            }
        } else {
            erroSintatico("repita");
        }
    }

    private void cmdAtrib() throws Exception {
        prepareAtrib();
    }

    private void prepareAtrib() throws Exception {
        if (casaToken(Tag.ATRIBUICAO)) {
            Expressao();
            if (!casaToken(Tag.PONTO_VIRGULA)) {
                erroSintatico(";");
            }
        } else if (casaToken(Tag.ABRE_COLCHETES)) {
            Expressao();
            checkMoreExpressao();
            if (!casaToken(Tag.FECHA_COLCHETES)) {
                erroSintatico(expectedToken);
            } else if (!casaToken(Tag.ATRIBUICAO)) {
                Expressao();
                if (!casaToken(Tag.PONTO_VIRGULA)) {
                    erroSintatico(";");
                }
            }
        } else {
            erroSintatico("<-- ou [");
        }
    }

    private void cmdChamaRotina() throws Exception {
        if (!casaToken(Tag.ABRE_PARENTESES)) {
            erroSintatico("(");
        } else {
            prepareParams();
            if (casaToken(Tag.FECHA_PARENTESES)) {
                if (!casaToken(Tag.PONTO_VIRGULA)) {
                    erroSintatico(";");
                }
            } else {
                erroSintatico(")");
            }
        }
    }

    private void prepareParams() throws Exception {
        Expressao();
        checkMoreExpressao();
    }

    private void cmdEscreva() throws Exception {
        expectedToken = null;
        if (!casaToken(Tag.ESCREVA)) {
            expectedToken = "escreva";
        } else if (!casaToken(Tag.ABRE_PARENTESES)) {
            expectedToken = "(";
        }
        if (expectedToken != null) {
            erroSintatico(expectedToken);
        } else {
            Expressao();
            if (!casaToken(Tag.FECHA_PARENTESES)) {
                expectedToken = ")";
            } else if (!casaToken(Tag.PONTO_VIRGULA)) {
                expectedToken = ";";
            }
            if (expectedToken != null) {
                erroSintatico(expectedToken);
            }
        }
    }

    private void cmdLeia() throws Exception {
        expectedToken = null;
        if (!casaToken(Tag.LEIA)) {
            expectedToken = "leia";
        } else if (!casaToken(Tag.ABRE_PARENTESES)) {
            expectedToken = "(";
        }
        if (expectedToken != null) {
            erroSintatico(expectedToken);
        } else {
            Expressao();
            expectedToken = null;
            if (!casaToken(Tag.FECHA_PARENTESES)) {
                expectedToken = ")";
            } else if (!casaToken(Tag.PONTO_VIRGULA)) {
                expectedToken = ";";
            }
            if (expectedToken != null) {
                erroSintatico(expectedToken);
            }
        }
    }


    private void Retorno() throws Exception {
        if (casaToken(Tag.RETORNE)) {
            Expressao();
        }
    }

    private void Expressao() throws Exception {
        P2();
        P1();
    }

    private void P1() throws Exception {
        if (casaToken(Tag.OU) || casaToken(Tag.E)) {
            P2();
            P1();
        }
    }

    private void P2() throws Exception {
        P3();
        P4();
    }

    private void P4() throws Exception {
        if (casaToken(Tag.DIFERENTE) || casaToken(Tag.IGUAL) || casaToken(Tag.MAIOR) || casaToken(Tag.MAIOR_IGUAL) || casaToken(Tag.MENOR) || casaToken(Tag.MENOR_IGUAL)) {
            P3();
            P4();
        }
    }

    private void P3() throws Exception {
        P10();
        P5();
    }

    private void P5() throws Exception {
        if (casaToken(Tag.SUBTRACAO) || casaToken(Tag.ADICAO)) {
            P10();
            P5();
        }
    }

    private void P10() throws Exception {
        P12();
        P6();
    }

    private void P6() throws Exception {
        if (casaToken(Tag.DIVISAO) || casaToken(Tag.MULTIPLICACAO)) {
            P12();
            P6();
        }
    }

    private void P12() throws Exception {
        P14();
        P7();
    }

    private void P7() throws Exception {
        if (casaToken(Tag.SUBTRACAO) || casaToken(Tag.NAO)) {
            P14();
            P7();
        }
    }

    private void P14() throws Exception {
        if (casaToken(Tag.ID)) {
            if (casaToken(Tag.ABRE_COLCHETES)) {
                Expressao();
                checkMoreExpressao();
                if (!casaToken(Tag.FECHA_COLCHETES)) {
                    erroSintatico("]");
                }
            } else if (casaToken(Tag.ABRE_PARENTESES)) {
                prepareParams();
                if (!casaToken(Tag.FECHA_PARENTESES)) {
                    erroSintatico(")");
                }
            } else {
                P14();
            }
        } else if (casaToken(Tag.NUMERICO_INT) || casaToken(Tag.NUMERICO_REAL) || casaToken(Tag.CONST_STRING) || casaToken(Tag.VERDADEIRO) || casaToken(Tag.FALSO)) {
        } else if (casaToken(Tag.ABRE_PARENTESES)) {
            Expressao();
            if (!casaToken(Tag.FECHA_PARENTESES)) {
                erroSintatico(")");
            }
        }
    }


}
