package br.com.compiler.portugolo.common;

import java.util.ArrayList;

public class Node {

    private Token parent;
    private final ArrayList<Node> childrens;
    private static int whiteSpace = 0;

    public Node(Token token) {
        this.parent = token;
        this.childrens = new ArrayList<Node>();
    }

    public void setParent(Token token) {
        this.parent = token;
    }

    public ArrayList<Node> getChildrens() {
        return childrens;
    }

    public void addChildrens(ArrayList<Node> childrens) {
        this.childrens.addAll(childrens);
    }

    public void addChildren(Node children) {
        this.childrens.add(children);
    }

    public void printContent() {
        if (this.parent != null) {
            for (int i = 0; i < whiteSpace; i++) {
                System.out.print(".   ");
            }
            System.out.print(this.parent.toString());
            whiteSpace++;
        }
        for (Node children : childrens) {
            children.printContent();
        }
        if (this.parent != null) {
            whiteSpace--;
        }
    }

}
