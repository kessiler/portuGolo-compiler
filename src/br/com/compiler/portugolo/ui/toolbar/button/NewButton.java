package br.com.compiler.portugolo.ui.toolbar.button;

import javax.swing.JButton;

import br.com.compiler.portugolo.ui.CompilerUI;
import br.com.compiler.portugolo.ui.TextStatus;
import br.com.compiler.portugolo.ui.toolbar.action.Action;

public class NewButton extends JButton implements Action {
	private static final long serialVersionUID = 1L;

	public NewButton() {
		super();
	}

	public NewButton(String text) {
		super(text);
	}

	@Override
	public void executeAction(CompilerUI frame) {
		frame.getTextEditor().setText("");
		frame.getTextMsg().setText("");
		frame.getLbFilePath().setText("");
		frame.getKeyListener().setTextEditor("");
		frame.getLbStatus().setText(TextStatus.NOT_MODIFIED.toString());
	}
}
