package br.com.compiler.portugolo.ui.toolbar.button;

import javax.swing.JButton;

import br.com.compiler.portugolo.ui.CompilerUI;
import br.com.compiler.portugolo.ui.TextStatus;
import br.com.compiler.portugolo.ui.toolbar.action.Action;

public class PasteButton extends JButton implements Action {
	private static final long serialVersionUID = 1L;

	public PasteButton() {
		super();
	}

	public PasteButton(String text) {
		super(text);
	}

	@Override
	public void executeAction(CompilerUI frame) {
		frame.getTextEditor().paste();
		frame.getLbStatus().setText(TextStatus.MODIFIED.toString());
	}
}
