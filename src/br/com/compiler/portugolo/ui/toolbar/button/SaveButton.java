package br.com.compiler.portugolo.ui.toolbar.button;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;

import javax.swing.JButton;

import br.com.compiler.portugolo.ui.CompilerUI;
import br.com.compiler.portugolo.ui.TextStatus;
import br.com.compiler.portugolo.ui.toolbar.action.Action;
import br.com.compiler.portugolo.ui.toolbar.action.SaveAction;

public class SaveButton extends JButton implements Action {
    private static final long serialVersionUID = 1L;

    public SaveButton() {
        super();
    }

    public SaveButton(String text) {
        super(text);
    }

    @Override
    public void executeAction(CompilerUI frame) {
        if (frame.getLbStatus().getText().equalsIgnoreCase(TextStatus.MODIFIED.toString())) {
            String absolutePath = frame.getLbFilePath().getText();

            if (absolutePath.isEmpty()) {
                Dialog dialog = new Dialog(frame, "Informe o diretório e o nome do arquivo", FileDialog.SAVE);

                dialog.showFileDialog();
                absolutePath = dialog.getAbsolutePath();
            }

            try {
                if (!absolutePath.equalsIgnoreCase("C:\\null")) {
                    SaveAction.saveToFile(absolutePath, frame.getTextEditor().getText());
                    frame.getLbFilePath().setText(absolutePath);
                    frame.getTextMsg().setText("");
                    frame.getLbStatus().setText(TextStatus.NOT_MODIFIED.toString());
                }
            } catch (IOException e) {
                System.err.println("ERRO FATAL!\nNão foi possível salvar o arquivo!");
                e.printStackTrace();
            }
        }
    }

    private class Dialog extends FileDialog {

        public Dialog(Frame parent, String title, int mode) {
            super(parent, title, mode);
        }

        private static final long serialVersionUID = 1L;
        private String absolutePath;

        public void showFileDialog() {
            this.setDirectory("C:\\");
            this.setVisible(true);

            absolutePath = this.getDirectory() + this.getFile();
        }

        public String getAbsolutePath() {
            return absolutePath;
        }
    }
}
