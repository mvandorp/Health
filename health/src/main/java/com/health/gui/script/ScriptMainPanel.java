package com.health.gui.script;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import externalClasses.TextLineNumber;

public class ScriptMainPanel extends JPanel {

    /**
     * Constant serialized ID used for compatibility.
     */
    private static final long serialVersionUID = 5017484222082184353L;
    private static JTextArea editor;
    
    public ScriptMainPanel() {
        super();
        this.setLayout(new BorderLayout());
        editor = new JTextArea("");
        editor.setFont(new Font("Helvetica", Font.BOLD, 12));
        JScrollPane scriptPane = new JScrollPane(editor);
        TextLineNumber tln = new TextLineNumber(editor);
        scriptPane.setRowHeaderView(tln);

        this.add(scriptPane);
    }
    
    /**
     * Sets the script.
     * @param newScript the new script
     */
    protected static void setScript(String newScript) {
        //not sure first line is needed.
        editor.setText("");
        editor.setText(newScript);
    }
    
    protected static String getScript() {
        return editor.getText();
    }
}
