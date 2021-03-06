package com.health.gui.input;

import javax.swing.JPanel;

import com.health.gui.VidneyPanel;
import com.health.gui.input.xmlwizard.XmlWizard;

/**
 * Represents the panel where all files are loaded into the program.
 * @author Bjorn van der Laan
 *
 */
public class VInputPanel extends VidneyPanel {
    /**
     * Constant serialized ID used for compatibility.
     */
    private static final long serialVersionUID = 2343407366589926543L;

    /**
     * Constructor.
     */
    public VInputPanel() {
        super();

        JPanel fileSelectionPanel = new FileSelectionPanel();
        this.setLeft(fileSelectionPanel);

        XmlWizard filePanel = new XmlWizard();
        this.setRight(filePanel);
    }
}
