package br.com.compiler.portugolo.ui.toolbar.button;

import br.com.compiler.portugolo.ui.CompilerUI;
import br.com.compiler.portugolo.ui.toolbar.action.Action;
import br.com.compiler.portugolo.ui.toolbar.action.CompileAction;

import javax.swing.JButton;

public class CompileButton extends JButton implements Action {
    private static final long serialVersionUID = 1L;

    public CompileButton() {
        super();
    }

    public CompileButton(String text) {
        super(text);
    }

    @Override
    public void executeAction(CompilerUI frame) {
        if (!frame.getTextEditor().getText().isEmpty()) {
            frame.getTextMsg().setText("");
            if (CompileAction.compile(frame)) {
                frame.getTextMsg().append("Compilado com sucesso!");
            }
        } else {
            frame.getTextMsg().setText("Nenhum programa para compilar!");
        }
    }

}
