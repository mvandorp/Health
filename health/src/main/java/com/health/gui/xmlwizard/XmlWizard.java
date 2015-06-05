package com.health.gui.xmlwizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * TODO make it possible to add columns with no label.
 * TODO implement way to delete columns.
 * TODO modify XmlConfigObject. Make attributes private en create getters/setters.
 * TODO back button.
 * TODO FileListing.fillfilelisting gebruiken om te refreshen na save.
 * TODO Na save meteen afsluiten.
 * TODO Save as alleen format naam automatisch naar goed folder en toevoegen .xml
 */

/**
 * Steps to add new dataset type
 * - add it to enum
 * - create a subclass of XmlStartEditSubPanel
 * - write a toXMLString in XmlConfigObject
 * </p>
 */

/**
 * The frame of the XML Wizard. Contains all panels and controls transition
 * between them.
 * 
 * @author Bjorn van der Laan
 *
 */
public class XmlWizard extends JPanel {
    private static Path path;

    private XmlFilePanel filePanel;
    private XmlEditPanel editPanel;
    private XmlSavePanel savePanel;

    /**
     * Constructs a XmlWizard containing the wizard panels.
     */
    public XmlWizard() {
        super();
        XmlWizard.path = null;
        init();
    }
    
    /**
     * Constructs a XmlWizard containing the wizard panels.
     * @param path
     *            a Path object referring to the Config XML folder
     */
    public XmlWizard(final Path path) {
        super();
        XmlWizard.path = path;
        init();
    }
    
    /**
     * Initializes the panel contents.
     */
    private void init() {
        filePanel = new XmlFilePanel();
        editPanel = new XmlEditPanel();
        savePanel = new XmlSavePanel();

        this.add(filePanel);

        XmlWizardListener wizardListener = new XmlWizardListener(this);
        wizardListener.attachListenerToButtons();

        this.setVisible(true);
    }

    /**
     * Changes the current visible panel.
     * 
     * @param next
     *            the JPanel that will be made visible
     */
    protected final void changePanel(final JPanel next) {
        this.removeAll();
        this.add(next);
        this.repaint();
        this.revalidate();
    }

    /**
     * Returns the XmlFilePanel. This method is used by the private class.
     * implementing ActionListener
     * 
     * @return the XmlFilePanel
     */
    protected final XmlFilePanel getFilePanel() {
        return filePanel;
    }

    /**
     * Returns the XmlEditPanel. This method is used by the private class
     * implementing ActionListener
     * 
     * @return the XmlEditPanel
     */
    protected final XmlEditPanel getEditPanel() {
        return editPanel;
    }

    /**
     * Returns the XmlSavePanel. This method is used by the private class.
     * implementing ActionListener
     * 
     * @return the XmlSavePanel
     */
    protected final XmlSavePanel getSavePanel() {
        return savePanel;
    }

    /**
     * XmlWizardListener handles all transitions between panels of the
     * XmlWizard. It implements the ActionListener interface.
     * 
     * @author Bjorn van der Laan
     *
     */
    private class XmlWizardListener implements ActionListener {
        private XmlWizard wizardFrame;
        private XmlFilePanel filePanel;
        private XmlEditPanel editPanel;
        private XmlSavePanel savePanel;

        public XmlWizardListener(final XmlWizard xw) {
            super();
            wizardFrame = xw;
            filePanel = wizardFrame.getFilePanel();
            editPanel = wizardFrame.getEditPanel();
            savePanel = wizardFrame.getSavePanel();
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            JButton source = (JButton) e.getSource();
            if (source.equals(filePanel.getNewFileButton())) {
                wizardFrame.changePanel(editPanel);
            } else if (source.equals(filePanel.getSelectFileButton())) {
                if (filePanel.getSelectedFile() != null) {
                    editPanel.setValues(filePanel.getSelectedFile());
                    wizardFrame.changePanel(editPanel);
                } else {
                    JOptionPane.showMessageDialog(new JFrame(),
                            "You have not selected a file yet!", "Error!",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (source.equals(editPanel.getContinueButton())) {
                XmlConfigObject config = editPanel.getValues();
                savePanel.setValues(config);
                wizardFrame.changePanel(savePanel);
            }
        }

        /**
         * Attaches the XmlWizardListener to the buttons in the different
         * panels.
         */
        public void attachListenerToButtons() {
            filePanel.addActionListenerToNewFileButton(this);
            filePanel.addActionListenerToSelectFileButton(this);
            editPanel.addActionListenerToContinueButton(this);
        }
    }
}