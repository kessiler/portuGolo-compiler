package br.com.compiler.portugolo.ui.toolbar.button;

import javax.swing.JButton;

import br.com.compiler.portugolo.ui.CompilerUI;
import br.com.compiler.portugolo.ui.toolbar.action.Action;


public class CutButton extends JButton implements Action {
    private static final long serialVersionUID = 1L;

    public CutButton() {
        super();
    }

    public CutButton(String text) {
        super(text);
    }

    @Override
    public void executeAction(CompilerUI frame) {
        frame.getTextEditor().cut();
    }
}
