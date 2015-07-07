package br.com.compiler.portugolo.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import br.com.compiler.portugolo.ui.highlighter.component.JEditTextArea;
import br.com.compiler.portugolo.ui.highlighter.tokenMarker.PortuGoloTokenMarker;
import br.com.compiler.portugolo.ui.toolbar.button.*;

@SuppressWarnings("serial")
public class CompilerUI extends JFrame {

    private final NewButton btnNew;
    private final OpenButton btnOpen;
    private final SaveButton btnSave;
    private final CopyButton btnCopy;
    private final PasteButton btnPaste;
    private final CutButton btnCut;
    private final CompileButton btnCompile;
    private final KeyListener keyListener;
    private final JEditTextArea textEditor;
    private final JLabel lbStatus;
    private final JLabel lbFilePath;
    private final JTextArea textMsg;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    CompilerUI frame = new CompilerUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public CompilerUI() {
        Font fonte = new Font("Dialog", Font.BOLD, 11);
        keyListener = new ShortCurt(this);
        addKeyListener(keyListener);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setTitle("Compilador - PortuGolo");
        setMinimumSize(new Dimension(800, 650));
        setLocationRelativeTo(null);
        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.addKeyListener(keyListener);
        setContentPane(contentPane);

        JPanel compilerPanel = new JPanel();
        contentPane.add(compilerPanel, BorderLayout.CENTER);
        compilerPanel.setLayout(new BorderLayout(0, 0));
        compilerPanel.addKeyListener(keyListener);

        JPanel panelToolBox = new JPanel();
        compilerPanel.add(panelToolBox, BorderLayout.NORTH);
        panelToolBox.setBorder(new LineBorder(new Color(0, 0, 0)));
        panelToolBox.setLayout(new GridLayout(0, 9, 0, 0));
        panelToolBox.addKeyListener(keyListener);

        btnNew = new NewButton("novo [ctrl-n]");
        btnNew.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                btnNew.executeAction(getInstance());
            }
        });

        btnNew.setIcon(getImageIcon("newFile.png"));
        btnNew.setHorizontalTextPosition(SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnNew.addKeyListener(keyListener);
        btnNew.setFont(fonte);
        panelToolBox.add(btnNew);

        btnOpen = new OpenButton("abrir [ctrl-a]");
        btnOpen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnOpen.executeAction(getInstance());
            }
        });
        btnOpen.setIcon(getImageIcon("openFile.png"));
        btnOpen.setHorizontalTextPosition(SwingConstants.CENTER);
        btnOpen.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnOpen.addKeyListener(keyListener);
        btnOpen.setFont(fonte);
        panelToolBox.add(btnOpen);

        btnSave = new SaveButton("salvar [ctrl-s]");
        btnSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                btnSave.executeAction(getInstance());
            }

        });
        btnSave.setIcon(getImageIcon("saveFile.png"));
        btnSave.setHorizontalTextPosition(SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnSave.addKeyListener(keyListener);
        btnSave.setFont(fonte);
        panelToolBox.add(btnSave);

        btnCopy = new CopyButton("copiar [ctrl-c]");
        btnCopy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnCopy.executeAction(getInstance());
            }
        });
        btnCopy.setIcon(getImageIcon("copy.png"));
        btnCopy.setHorizontalTextPosition(SwingConstants.CENTER);
        btnCopy.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnCopy.addKeyListener(keyListener);
        btnCopy.setFont(fonte);
        panelToolBox.add(btnCopy);

        btnPaste = new PasteButton("colar [ctrl-v]");
        btnPaste.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnPaste.executeAction(getInstance());
            }
        });
        btnPaste.setIcon(getImageIcon("paste.png"));
        btnPaste.setHorizontalTextPosition(SwingConstants.CENTER);
        btnPaste.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnPaste.addKeyListener(keyListener);
        btnPaste.setFont(fonte);
        panelToolBox.add(btnPaste);

        btnCut = new CutButton("recortar [ctrl-x]");
        btnCut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnCut.executeAction(getInstance());
            }
        });
        btnCut.setIcon(getImageIcon("cut.png"));
        btnCut.setHorizontalTextPosition(SwingConstants.CENTER);
        btnCut.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnCut.addKeyListener(keyListener);
        btnCut.setFont(fonte);
        panelToolBox.add(btnCut);

        btnCompile = new CompileButton("compilar [F8]");
        btnCompile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnCompile.executeAction(getInstance());
            }
        });
        btnCompile.setIcon(getImageIcon("compile.png"));
        btnCompile.setHorizontalTextPosition(SwingConstants.CENTER);
        btnCompile.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnCompile.addKeyListener(keyListener);
        btnCompile.setFont(fonte);
        panelToolBox.add(btnCompile);

        final JPanel panelCentral = new JPanel();
        panelCentral.addKeyListener(keyListener);
        compilerPanel.add(panelCentral, BorderLayout.CENTER);
        panelCentral.setLayout(new BorderLayout(0, 0));

        final JPanel panelEditor = new JPanel();
        panelEditor.addKeyListener(keyListener);

        textEditor = new JEditTextArea();
        textEditor.setTokenMarker(new PortuGoloTokenMarker());
        textEditor.setEditable(true);
        textEditor.setBorder(new NumberedBorder());
        textEditor.setKeyListener(keyListener);
        textEditor.getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), "none");
        textEditor.getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "none");
        textEditor.getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "none");
        textEditor.getInputMap(JPanel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), "none");
        panelEditor.setLayout(new BoxLayout(panelEditor, BoxLayout.X_AXIS));
        panelEditor.add(textEditor);
        panelCentral.add(panelEditor, BorderLayout.CENTER);

        final JPanel panelMsg = new JPanel();
        panelMsg.addKeyListener(keyListener);
        panelMsg.setLayout(new BoxLayout(panelMsg, BoxLayout.X_AXIS));
        panelMsg.setSize(panelMsg.getSize().width, 100);

        final JScrollPane scrollPaneMsg = new JScrollPane(panelMsg);
        scrollPaneMsg.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneMsg.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneMsg.addKeyListener(keyListener);
        scrollPaneMsg.setPreferredSize(new Dimension(0, 100));

        textMsg = new JTextArea();
        panelMsg.add(textMsg);
        textMsg.setEditable(false);
        textMsg.setBorder(new LineBorder(new Color(0, 0, 0)));
        panelCentral.add(scrollPaneMsg, BorderLayout.SOUTH);
        textMsg.addKeyListener(keyListener);
        textMsg.setFont(new Font("Console", Font.PLAIN, 11));

        final JPanel panelFooter = new JPanel();
        compilerPanel.add(panelFooter, BorderLayout.SOUTH);
        panelFooter.setLayout(new GridLayout(1, 2, 0, 0));
        panelFooter.addKeyListener(keyListener);
        panelFooter.addKeyListener(keyListener);

        lbStatus = new JLabel(TextStatus.NOT_MODIFIED.toString());
        lbStatus.addKeyListener(keyListener);
        panelFooter.add(lbStatus);

        lbFilePath = new JLabel("");
        lbFilePath.addKeyListener(keyListener);
        panelFooter.add(lbFilePath);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    private CompilerUI getInstance() {
        return this;
    }

    public ImageIcon getImageIcon(String iconName) {
        return new ImageIcon(this.getClass().getResource("/images/" + iconName));
    }

    public JEditTextArea getTextEditor() {
        return textEditor;
    }

    public JLabel getLbFilePath() {
        return lbFilePath;
    }

    public JLabel getLbStatus() {
        return lbStatus;
    }

    public JTextArea getTextMsg() {
        return textMsg;
    }

    public ShortCurt getKeyListener() {
        if (keyListener instanceof ShortCurt) {
            return (ShortCurt) keyListener;
        }
        return new ShortCurt(this);
    }

}
