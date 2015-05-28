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
    private String expectedToken = null;

    public Syntactic(Lexical lexical, SymbolTable symbolTable, JTextArea consoleOutput) {
        this.lexical = lexical;
        this.symbolTable = symbolTable;
        this.consoleOutput = consoleOutput;
        this.token = lexical.proxToken();
    }

    // Reporta erro sintatico ao usuario - Aqui deve ser tratado o Metodo do Panico
    public void erroSintatico(String esperado) {
        this.consoleOutput.append("Erro sintatico na linha " + token.getLine() + " e na coluna " + token.getColumn());
        this.consoleOutput.append("\nEsperado: " + esperado + "\nEncontrado: " + token.getLexeme() + "\n");
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

    public Node compilador() {
        Node noPrograma = programa();
        if (!casaToken(Tag.EOF)) {
            erroSintatico("Fim de Arquivo");
            return null;
        }
        return noPrograma;
    }

    public Node programa() {
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
        }
        return null;
    }

    private void prepareToDeclara() {
        if (casaToken(Tag.DECLARE)) {
            prepareToDeclaraVar();
        }
    }

    private void prepareToDeclaraVar() {
        declaraVar();
        checkMoreDeclaraVar();
    }

    private void declaraVar() {
        tipo();
        listaId();
        if (!casaToken(Tag.PONTO_VIRGULA)) {
            erroSintatico("Esperado ponto e virgula");
        }
    }

    private void checkMoreDeclaraVar() {
        if (casaToken(Tag.TIPO_LOGICO) || casaToken(Tag.TIPO_NUMERICO) || casaToken(Tag.TIPO_LITERAL) || casaToken(Tag.TIPO_DANADANAO)) {
            prepareToDeclaraVar();
        }
    }

    private void listaRotina() {
        if (isToken(Tag.SUBROTINA)) {
            rotina();
            listaRotina();
        }
    }

    void rotina() {
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

    private void listaParam() {
        param();
        checkMoreParam();
    }

    private void checkMoreParam() {
        if (casaToken(Tag.VIRGULA)) {
            listaParam();
        }
    }

    private void param() {
        listaId();
        tipo();
    }

    private void listaId() {
        Token auxToken = token;
        if (casaToken(Tag.ID)) {
            symbolTable.put(auxToken.getLexeme(), auxToken);
            checkMoreId();
        } else {
            erroSintatico("Esperado um identificador");
        }
    }

    private void checkMoreId() {
        if (casaToken(Tag.VIRGULA)) {
            listaId();
        }
    }

    private void tipo() {
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

    private void tipoPrimitivo() {
        if (!(casaToken(Tag.TIPO_LOGICO) || casaToken(Tag.TIPO_NUMERICO) || casaToken(Tag.TIPO_LITERAL) || casaToken(Tag.TIPO_DANADANAO))) {
            erroSintatico("Esperado um dos tipo primitivo");
        }
    }

    private void checkMoreExpressao() {
        if (casaToken(Tag.VIRGULA)) {
            Expressao();
        }
    }

    private void listaCmd() {
        cmd();
        if (isToken(Tag.SE) || isToken(Tag.ENQUANTO) || isToken(Tag.PARA)
                || isToken(Tag.REPITA) || isToken(Tag.ID)
                || isToken(Tag.ESCREVA) || isToken(Tag.LEIA)) {
            listaCmd();
        }
    }

    private void cmd() {
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

    private void prepareCmdAtribOrRotina() {
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

    private void cmdSe() {
        if (casaToken(Tag.SE) && casaToken(Tag.ABRE_PARENTESES)) {
            Expressao();
            if (casaToken(Tag.FECHA_PARENTESES)) {
                if (casaToken(Tag.INICIO)) {
                    listaCmd();
                    if (casaToken(Tag.FIM)) {
                        P6();
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

    private void P6() {
        if (!casaToken(Tag.SENAO) && !casaToken(Tag.INICIO)) {
            erroSintatico("Esperado 'Senao'");
        } else {
            listaCmd();
            if (!casaToken(Tag.FIM)) {
                erroSintatico("Esperado 'fim'");
            }
        }
    }

    private void cmdEnquanto() {
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

    private void cmdPara() {
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

    private void cmdRepita() {
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

    private void cmdAtrib() {
        prepareAtrib();
    }

    private void prepareAtrib() {
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

    private void cmdChamaRotina() {
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

    private void prepareParams() {
        Expressao();
        checkMoreExpressao();
    }

    private void cmdEscreva() {
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

    private void cmdLeia() {
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


    private void Retorno() {
        if (casaToken(Tag.RETORNE)) {
            Expressao();
        }
    }

    private void Expressao() {
        if (isToken(Tag.OU)) {
            ResolveK();
            OpA();
        } else if (casaToken(Tag.ID)) {
            ResolveH();
        } else if (casaToken(Tag.ABRE_PARENTESES)) {
            Expressao();
            if (!casaToken(Tag.FECHA_PARENTESES)) {
                erroSintatico(")");
            }
        } else if (!casaToken(Tag.TIPO_NUMERICO) && !casaToken(Tag.TIPO_LITERAL) && !casaToken(Tag.VERDADEIRO) && !casaToken(Tag.FALSO)) {
            erroSintatico("numero, literal, verdadeiro ou falso");
        }
    }

    private void ResolveG() {
        if (isToken(Tag.ID) || isToken(Tag.TIPO_LITERAL) || isToken(Tag.TIPO_NUMERICO) || isToken(Tag.TIPO_LOGICO) || isToken(Tag.OU) /*|| isToken(Tag.OPUNARIO_NEGATIVO) */ || isToken(Tag.NAO)) {
            Expressao();
            checkMoreExpressao();
        }
    }

    private void ResolveH() {
        if (casaToken(Tag.ABRE_COLCHETES)) {
            Expressao();
            checkMoreExpressao();
            if (!casaToken(Tag.FECHA_COLCHETES)) {
                erroSintatico("]");
            }
        }
        if (casaToken(Tag.ABRE_PARENTESES)) {
            ResolveG();
            if (!casaToken(Tag.FECHA_PARENTESES)) {
                erroSintatico(")");
            }
        }
    }

    private void ResolveK() {
        if (casaToken(Tag.OU)) {
            OpA();
            ResolveK();
        }
    }

    private void OpA() {
        if (isToken(Tag.NAO)) {
            OpB();
            ResolveL();
        }
    }

    private void ResolveL() {
        if (casaToken(Tag.E)) {
            OpB();
            ResolveL();
        }
    }

    private void OpB() {
        if (isToken(Tag.NAO)) {
            OpC();
            ResolveM();
        }
    }

    private void ResolveM() {
        if (casaToken(Tag.IGUAL) || casaToken(Tag.DIFERENTE)) {
            OpC();
            ResolveM();
        }
    }

    private void OpC() {
        if (isToken(Tag.NAO)) {
            OpD();
            ResolveN();
        }
    }

    private void ResolveN() {
        if (casaToken(Tag.MAIOR) || casaToken(Tag.MENOR) || casaToken(Tag.MAIOR_IGUAL) || casaToken(Tag.MENOR_IGUAL)) {
            OpD();
            ResolveN();
        }
    }

    private void OpD() {
        if (isToken(Tag.NAO)) {
            OpE();
            ResolveO();
        }
    }

    private void ResolveO() {
        if (casaToken(Tag.ADICAO) || casaToken(Tag.SUBTRACAO)) {
            OpE();
            ResolveO();
        }
    }

    private void OpE() {
        if (isToken(Tag.NAO)) {
            OpUnario();
            ResolveP();
        }
    }

    private void ResolveP() {
        if (casaToken(Tag.MULTIPLICACAO) || casaToken(Tag.DIVISAO)) {
            OpUnario();
            ResolveP();
        }
    }

    private void OpUnario() {
        if (!casaToken(Tag.NAO) && !casaToken(Tag.SUBTRACAO)) {
            erroSintatico(" ");
        }
    }


}
