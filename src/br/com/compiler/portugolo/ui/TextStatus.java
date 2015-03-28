package br.com.compiler.portugolo.ui;

public enum TextStatus {
    NOT_MODIFIED("Não modificado"), MODIFIED("Modificado");
    private String description;

    private TextStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
