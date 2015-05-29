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
            Node nodePrepareToDeclara = prepareToDeclara();
            Node nodeListaCmd = listaCmd();
            if (!casaToken(Tag.FIM)) {
                erroSintatico("Fim");
                return null;
            } else if (!casaToken(Tag.ALGORITMO)) {
                erroSintatico("Algoritmo");
                return null;
            }
            Node listaRotina = listaRotina();
            Node noAlgoritmo = new Node(algoritmo);
            if (nodePrepareToDeclara != null) {
                noAlgoritmo.addChildrens(nodePrepareToDeclara.getChildrens());
            }
            if (nodeListaCmd != null) {
                noAlgoritmo.addChildrens(nodeListaCmd.getChildrens());
            }
            if (listaRotina != null) {
                noAlgoritmo.addChildrens(listaRotina.getChildrens());
            }
            return noAlgoritmo;
        } else {
            erroSintatico("Algoritmo");
        }
        return null;
    }

    private Node prepareToDeclara() throws Exception {
        Token declare = token;
        if (casaToken(Tag.DECLARE)) {
            Node nodeDeclare = new Node(declare);
            nodeDeclare.addChildren(prepareToDeclaraVar());
            return nodeDeclare;
        }
        return null;
    }

    private Node prepareToDeclaraVar() throws Exception {
        Node nodePrepareToDeclaraVar = new Node(null);
        nodePrepareToDeclaraVar.addChildren(declaraVar());
        Node nodeCheckMoreDeclaraVar;
        if ((nodeCheckMoreDeclaraVar = checkMoreDeclaraVar()) != null) {
            nodeCheckMoreDeclaraVar.addChildren(nodePrepareToDeclaraVar);
            return nodeCheckMoreDeclaraVar;
        }
        return nodePrepareToDeclaraVar;
    }

    private Node declaraVar() throws Exception {
        Node nodeDeclaraVar = new Node(null);
        nodeDeclaraVar.addChildren(tipo());
        Node nodeListaId;
        if ((nodeListaId = listaId()) != null) {
            nodeDeclaraVar.addChildren(nodeListaId);
        }
        if (!casaToken(Tag.PONTO_VIRGULA)) {
            erroSintatico("Esperado ponto e virgula");
        }
        return nodeDeclaraVar;
    }

    private Node checkMoreDeclaraVar() throws Exception {
        if (isToken(Tag.TIPO_LOGICO) || isToken(Tag.TIPO_NUMERICO) || isToken(Tag.TIPO_LITERAL) || isToken(Tag.TIPO_DANADANAO)) {
            return prepareToDeclaraVar();
        } else {
            return null;
        }
    }

    private Node listaRotina() throws Exception {
        if (isToken(Tag.SUBROTINA)) {
            Node nodeRotina = rotina();
            Node nodeListaRotina;
            if ((nodeListaRotina = listaRotina()) != null) {
                nodeListaRotina.addChildren(nodeRotina);
                return nodeListaRotina;
            }
            return nodeRotina;
        }
        return null;
    }

    private Node rotina() throws Exception {
        if (casaToken(Tag.SUBROTINA)) {
            Token rotinaId = token;
            if (casaToken(Tag.ID)) {
                Node nodeRotinaId = new Node(rotinaId);
                if (casaToken(Tag.ABRE_PARENTESES)) {
                    Node nodeListaParam;
                    if ((nodeListaParam = listaParam()) != null) {
                        nodeRotinaId.addChildren(nodeListaParam);
                    }
                    if (casaToken(Tag.FECHA_PARENTESES)) {
                        Node nodePrepareToDeclara;
                        if ((nodePrepareToDeclara = prepareToDeclara()) != null) {
                            nodeRotinaId.addChildren(nodePrepareToDeclara);
                        }
                        listaCmd();
                        Retorno();
                        if (casaToken(Tag.FIM)) {
                            if (casaToken(Tag.SUBROTINA)) {
                                if (!casaToken(Tag.ID)) {
                                    erroSintatico("o nome da subrotina");
                                }
                                return nodeRotinaId;
                            } else {
                                erroSintatico("subrotina");
                                return nodeRotinaId;
                            }
                        } else {
                            erroSintatico("fim");
                            return nodeRotinaId;
                        }
                    } else {
                        erroSintatico(")");
                        return nodeRotinaId;
                    }
                } else {
                    erroSintatico(")");
                    return nodeRotinaId;
                }
            } else {
                erroSintatico("o nome da subrotina");
                return null;
            }
        } else {
            erroSintatico("subrotina");
            return null;
        }
    }

    private Node listaParam() throws Exception {
        Node nodeParam = param();
        Node nodeCheckNoreParam;
        if ((nodeCheckNoreParam = checkMoreParam()) != null) {
            nodeCheckNoreParam.addChildren(nodeParam);
            return nodeCheckNoreParam;
        }
        return nodeParam;
    }

    private Node checkMoreParam() throws Exception {
        if (casaToken(Tag.VIRGULA)) {
            return listaParam();
        } else {
            return null;
        }
    }

    private Node param() throws Exception {
        Node nodeParam = new Node(null);
        nodeParam.addChildren(listaId());
        Node nodeTipo;
        if ((nodeTipo = tipo()) != null) {
            nodeParam.addChildren(nodeTipo);
        }
        return nodeParam;
    }

    private Node listaId() throws Exception {
        Token auxToken = token;
        if (casaToken(Tag.ID)) {
            Node nodeId = new Node(auxToken);
            symbolTable.put(auxToken.getLexeme(), auxToken);
            Node nodeCheckMoreId;
            if ((nodeCheckMoreId = checkMoreId()) != null) {
                nodeCheckMoreId.addChildren(nodeId);
                return nodeCheckMoreId;
            }
            return nodeId;
        } else {
            erroSintatico("Esperado um identificador");
            return null;
        }
    }

    private Node checkMoreId() throws Exception {
        if (casaToken(Tag.VIRGULA)) {
            return listaId();
        } else {
            return null;
        }
    }

    private Node tipo() throws Exception {
        Node nodeTipo = new Node(null);
        if (casaToken(Tag.ABRE_COLCHETES)) {
            nodeTipo.addChildren(Expressao());
            Node nodeCheckMoreExpressao;
            if ((nodeCheckMoreExpressao = checkMoreExpressao()) != null) {
                nodeTipo.addChildren(nodeCheckMoreExpressao);
            }
            if (casaToken(Tag.FECHA_COLCHETES)) {
                nodeTipo.addChildren(tipoPrimitivo());
                return nodeTipo;
            } else {
                erroSintatico("Esperado fecha colchetes");
                return null;
            }
        } else {
            return tipoPrimitivo();
        }
    }

    private Node tipoPrimitivo() throws Exception {
        Token tipoPrimitivo = token;
        if (casaToken(Tag.TIPO_LOGICO)) {
            return new Node(tipoPrimitivo);
        } else if (casaToken(Tag.TIPO_NUMERICO)) {
            return new Node(tipoPrimitivo);
        } else if (casaToken(Tag.TIPO_LITERAL)) {
            return new Node(tipoPrimitivo);
        } else if (casaToken(Tag.TIPO_DANADANAO)) {
            return new Node(tipoPrimitivo);
        } else {
            erroSintatico("Esperado um dos tipo primitivo");
            return null;
        }
    }

    private Node checkMoreExpressao() throws Exception {
        if (casaToken(Tag.VIRGULA)) {
            return Expressao();
        }
        return null;
    }

    private Node listaCmd() throws Exception {
        if (isToken(Tag.SE) || isToken(Tag.ENQUANTO) || isToken(Tag.PARA)
                || isToken(Tag.REPITA) || isToken(Tag.ID)
                || isToken(Tag.ESCREVA) || isToken(Tag.LEIA)) {
            Node nodeCmd = cmd();
            Node nodeListaCmd;
            if ((nodeListaCmd = listaCmd()) != null) {
                nodeListaCmd.addChildren(nodeCmd);
            }
            return nodeListaCmd;
        }
        return null;
    }

    private Node cmd() throws Exception {
        switch (token.getCode()) {
            case Tag.SE:
                return cmdSe();
            case Tag.ENQUANTO:
                return cmdEnquanto();
            case Tag.PARA:
                return cmdPara();
            case Tag.REPITA:
                return cmdRepita();
            case Tag.ID:
                return prepareCmdAtribOrRotina();
            case Tag.ESCREVA:
                return cmdEscreva();
            case Tag.LEIA:
                return cmdLeia();
        }
        return null;
    }

    private Node prepareCmdAtribOrRotina() throws Exception {
        Token id = token;
        if (casaToken(Tag.ID)) {
            Node nodeCmdAtribOrRotina = new Node(id);
            Node nodeCmd;
            if (isToken(Tag.ABRE_PARENTESES)) {
                if ((nodeCmd = cmdChamaRotina()) != null) {
                    nodeCmdAtribOrRotina.addChildren(nodeCmd);
                }
            } else if (isToken(Tag.ATRIBUICAO)) {
                if ((nodeCmd = cmdAtrib()) != null) {
                    nodeCmdAtribOrRotina.addChildren(nodeCmd);
                }
            }
            return nodeCmdAtribOrRotina;
        } else {
            erroSintatico("identificador");
        }
        return null;
    }

    private Node cmdSe() throws Exception {
        Token se = token;
        if (casaToken(Tag.SE) && casaToken(Tag.ABRE_PARENTESES)) {
            Node nodeSe = new Node(se);
            nodeSe.addChildren(Expressao());
            if (casaToken(Tag.FECHA_PARENTESES)) {
                if (casaToken(Tag.INICIO)) {
                    Node nodeListaCmd;
                    if ((nodeListaCmd = listaCmd()) != null) {
                        nodeSe.addChildren(nodeListaCmd);
                    }
                    if (casaToken(Tag.FIM)) {
                        Node nodeCheckSenao;
                        if ((nodeCheckSenao = checkSenao()) != null) {
                            nodeSe.addChildren(nodeCheckSenao);
                        }
                        return nodeSe;
                    } else {
                        erroSintatico("Esperado 'fim'");
                        return nodeSe;
                    }
                } else {
                    erroSintatico("Esperado 'inicio'");
                    return nodeSe;
                }
            } else {
                erroSintatico("Esperado '}'");
                return nodeSe;
            }
        }
        return null;
    }

    private Node checkSenao() throws Exception {
        if (isToken(Tag.SENAO)) {
            Token senao = token;
            if (!casaToken(Tag.SENAO) && !casaToken(Tag.INICIO)) {
                erroSintatico("Esperado 'Senao'");
            } else {
                Node nodeSenao = new Node(senao);
                Node nodeListaCmd;
                if ((nodeListaCmd = listaCmd()) != null) {
                    nodeSenao.addChildren(nodeListaCmd);
                }
                if (!casaToken(Tag.FIM)) {
                    erroSintatico("Esperado 'fim'");
                }
                return nodeSenao;
            }
        }
        return null;
    }

    private Node cmdEnquanto() throws Exception {
        Token enquanto = token;
        if (!casaToken(Tag.ENQUANTO) && !casaToken(Tag.ABRE_PARENTESES)) {
            erroSintatico("Esperado 'enquanto'");
        } else {
            Node nodeEnquanto = new Node(enquanto);
            nodeEnquanto.addChildren(Expressao());
            expectedToken = null;
            if (!casaToken(Tag.FACA)) {
                expectedToken = "faça";
            } else if (casaToken(Tag.INICIO)) {
                expectedToken = "inicio";
            }
            if (expectedToken != null) {
                erroSintatico(expectedToken);
            } else {
                Node nodeListaCmd;
                if ((nodeListaCmd = listaCmd()) != null) {
                    nodeEnquanto.addChildren(nodeListaCmd);
                }
                if (!casaToken(Tag.FIM)) {
                    erroSintatico("fim");
                }
                return nodeEnquanto;
            }
        }
        return null;
    }

    private Node cmdPara() throws Exception {
        Token para = token;
        if (casaToken(Tag.PARA)) {
            Node nodePara = new Node(para);
            nodePara.addChildren(prepareCmdAtribOrRotina());
            if (casaToken(Tag.ATE)) {
                nodePara.addChildren(Expressao());
                expectedToken = null;
                if (!casaToken(Tag.FACA)) {
                    expectedToken = "faça";
                } else if (!casaToken(Tag.INICIO)) {
                    expectedToken = "inicio";
                }
                if (expectedToken != null) {
                    erroSintatico(expectedToken);
                } else {
                    Node nodeListaCmd;
                    if ((nodeListaCmd = listaCmd()) != null) {
                        nodePara.addChildren(nodeListaCmd);
                    }
                    if (!casaToken(Tag.FIM)) {
                        erroSintatico("fim");
                    }
                }
            } else {
                erroSintatico("ate");
            }
            return nodePara;
        } else {
            erroSintatico("para");
        }
        return null;
    }

    private Node cmdRepita() throws Exception {
        Token repita = token;
        if (casaToken(Tag.REPITA)) {
            Node nodeRepita = new Node(repita);
            Node nodeListaCmd;
            if ((nodeListaCmd = listaCmd()) != null) {
                nodeRepita.addChildren(nodeListaCmd);
            }
            if (casaToken(Tag.ATE)) {
                nodeRepita.addChildren(Expressao());
            } else {
                erroSintatico("ate");
            }
            return nodeRepita;
        } else {
            erroSintatico("repita");
        }
        return null;
    }

    private Node cmdAtrib() throws Exception {
        return prepareAtrib();
    }

    private Node prepareAtrib() throws Exception {
        Token atribuicao = token;
        if (casaToken(Tag.ATRIBUICAO)) {
            Node nodeAtribuicao = new Node(atribuicao);
            nodeAtribuicao.addChildren(Expressao());
            if (!casaToken(Tag.PONTO_VIRGULA)) {
                erroSintatico(";");
            }
            return nodeAtribuicao;
        } else if (casaToken(Tag.ABRE_COLCHETES)) {
            Node nodeN = new Node(null);
            nodeN.addChildren(Expressao());
            Node nodeCheckMoreExpressao;
            if ((nodeCheckMoreExpressao = checkMoreExpressao()) != null) {
                nodeN.addChildren(nodeCheckMoreExpressao);
            }
            if (!casaToken(Tag.FECHA_COLCHETES)) {
                erroSintatico(expectedToken);
            } else if (!casaToken(Tag.ATRIBUICAO)) {
                Node nodeAtribuicao = new Node(atribuicao);
                nodeAtribuicao.addChildren(Expressao());
                if (!casaToken(Tag.PONTO_VIRGULA)) {
                    erroSintatico(";");
                }
                return nodeAtribuicao;
            }
        } else {
            erroSintatico("<-- ou [");
        }
        return null;
    }

    private Node cmdChamaRotina() throws Exception {
        if (!casaToken(Tag.ABRE_PARENTESES)) {
            erroSintatico("(");
        } else {
            Node nodeCmdChamaRotina = new Node(null);
            nodeCmdChamaRotina.addChildren(prepareParams());
            if (casaToken(Tag.FECHA_PARENTESES)) {
                if (!casaToken(Tag.PONTO_VIRGULA)) {
                    erroSintatico(";");
                }
            } else {
                erroSintatico(")");
            }
            return nodeCmdChamaRotina;
        }
        return null;
    }

    private Node prepareParams() throws Exception {
        Node nodePrepareParams = new Node(null);
        nodePrepareParams.addChildren(Expressao());
        Node nodeCheckMoreExpressao;
        if ((nodeCheckMoreExpressao = checkMoreExpressao()) != null) {
            nodePrepareParams.addChildren(nodeCheckMoreExpressao);
        }
        return nodePrepareParams;
    }

    private Node cmdEscreva() throws Exception {
        expectedToken = null;
        Token escreva = token;
        if (!casaToken(Tag.ESCREVA)) {
            expectedToken = "escreva";
        } else if (!casaToken(Tag.ABRE_PARENTESES)) {
            expectedToken = "(";
        }
        if (expectedToken != null) {
            erroSintatico(expectedToken);
        } else {
            Node nodeEscreva = new Node(escreva);
            nodeEscreva.addChildren(Expressao());
            if (!casaToken(Tag.FECHA_PARENTESES)) {
                expectedToken = ")";
            } else if (!casaToken(Tag.PONTO_VIRGULA)) {
                expectedToken = ";";
            }
            if (expectedToken != null) {
                erroSintatico(expectedToken);
            }
            return nodeEscreva;
        }
        return null;
    }

    private Node cmdLeia() throws Exception {
        expectedToken = null;
        Token leia = token;
        if (!casaToken(Tag.LEIA)) {
            expectedToken = "leia";
        } else if (!casaToken(Tag.ABRE_PARENTESES)) {
            expectedToken = "(";
        }
        if (expectedToken != null) {
            erroSintatico(expectedToken);
        } else {
            Node nodeLeia = new Node(leia);
            nodeLeia.addChildren(Expressao());
            expectedToken = null;
            if (!casaToken(Tag.FECHA_PARENTESES)) {
                expectedToken = ")";
            } else if (!casaToken(Tag.PONTO_VIRGULA)) {
                expectedToken = ";";
            }
            if (expectedToken != null) {
                erroSintatico(expectedToken);
            }
            return nodeLeia;
        }
        return null;
    }


    private Node Retorno() throws Exception {
        Token retorno = token;
        if (casaToken(Tag.RETORNE)) {
            Node nodeRetorno = new Node(retorno);
            Node nodeExpressao;
            if ((nodeExpressao = Expressao()) != null) {
                nodeRetorno.addChildren(nodeExpressao);
            }
        }
        return null;
    }

    private Node Expressao() throws Exception {
        P2();
        P1();
        return null;
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
