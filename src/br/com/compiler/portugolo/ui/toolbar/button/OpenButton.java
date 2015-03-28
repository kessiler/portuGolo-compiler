package br.com.compiler.portugolo.ui.toolbar.button;

import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;

import br.com.compiler.portugolo.ui.CompilerUI;
import br.com.compiler.portugolo.ui.TextStatus;
import br.com.compiler.portugolo.ui.toolbar.action.Action;


public class OpenButton extends JButton implements Action {
    private static final long serialVersionUID = 1L;

    public OpenButton() {
        super();
    }

    public OpenButton(String text) {
        super(text);
    }

    @Override
    public void executeAction(CompilerUI frame) {
        String filePath = "";
        FileDialog fileDialog = new FileDialog(frame, "Open", FileDialog.LOAD);
        fileDialog.setDirectory("C:\\");
        fileDialog.setVisible(true);

        filePath = fileDialog.getDirectory() + fileDialog.getFile();

        if (!filePath.equalsIgnoreCase("C:\\null")) try {
            frame.getTextEditor().setText(textFileRead(filePath));
            frame.getLbFilePath().setText(filePath);
            frame.getKeyListener().setTextEditor("");
            frame.getTextMsg().setText("");
            frame.getLbStatus().setText(TextStatus.NOT_MODIFIED.toString());
            frame.getKeyListener().setTextEditor(frame.getTextEditor().getText());
        } catch (Exception e) {
            System.err.println("ERRO FATAL!\nNão foi possível realizar a leitura do arquivo!");
            e.printStackTrace();
        }
    }

    private String textFileRead(String fileName) throws ClassNotFoundException, IOException {
        FileReader fr = new FileReader(fileName);
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(fr);

        int c = 0;
        while ((c = bufferedReader.read()) != -1) {
            sb.append((char) c);
        }

        bufferedReader.close();
        fr.close();

        return sb.toString();
    }

}
