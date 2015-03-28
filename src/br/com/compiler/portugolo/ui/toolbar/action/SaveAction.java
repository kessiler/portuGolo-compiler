package br.com.compiler.portugolo.ui.toolbar.action;

import java.io.FileWriter;
import java.io.IOException;

public class SaveAction {
    public static void saveToFile(String absolutePath, String buffer) throws IOException {
        FileWriter fw = new FileWriter(absolutePath, false);
        fw.write(buffer);
        fw.flush();
        fw.close();
    }
}
