package br.com.compiler.portugolo.ui;

import br.com.compiler.portugolo.ui.toolbar.button.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class ShortCurt implements KeyListener {

    private CompilerUI compUi;
    private String textEditor;

    public ShortCurt(CompilerUI compUi) {
        this.compUi = compUi;
        textEditor = "";
    }

    @Override
    public void keyPressed(KeyEvent event) {
        boolean isCtrlDown = event.isControlDown();
        switch (event.getKeyCode()) {
            case KeyEvent.VK_N:
                if (isCtrlDown) {
                    new NewButton().executeAction(compUi);
                }
                break;
            case KeyEvent.VK_A:
                if (isCtrlDown) {
                    new OpenButton().executeAction(compUi);
                }
                break;
            case KeyEvent.VK_S:
                if (isCtrlDown) {
                    new SaveButton().executeAction(compUi);
                }
                break;
            case KeyEvent.VK_C:
                if (isCtrlDown) {
                    new CopyButton().executeAction(compUi);
                }
                break;
            case KeyEvent.VK_V:
                if (isCtrlDown) {
                    new PasteButton().executeAction(compUi);
                }
                break;
            case KeyEvent.VK_X:
                if (isCtrlDown) {
                    new CutButton().executeAction(compUi);
                }
                break;
            case KeyEvent.VK_F8:
                new CompileButton().executeAction(compUi);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (!textEditor.equalsIgnoreCase(compUi.getTextEditor().getText())) {
            compUi.getLbStatus().setText(TextStatus.MODIFIED.toString());
        } else {
            compUi.getLbStatus().setText(TextStatus.NOT_MODIFIED.toString());
        }

    }

    @Override
    public void keyTyped(KeyEvent event) {
        // TODO Auto-generated method stub

    }

    public String getTextEditor() {
        return textEditor;
    }

    public void setTextEditor(String textEditor) {
        this.textEditor = textEditor;
    }

}
