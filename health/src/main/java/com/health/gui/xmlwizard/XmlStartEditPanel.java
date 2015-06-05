package com.health.gui.xmlwizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.health.FileType;
import com.health.gui.xmlwizard.starteditsubpanels.XmlStartEditSubPanel;
import com.health.gui.xmlwizard.starteditsubpanels.XmlTxtEditPanel;
import com.health.gui.xmlwizard.starteditsubpanels.XmlXlsEditPanel;

/**
 * Displays options to modify elements of the config xml that are not about the
 * columns.
 * 
 * @author Bjorn van der Laan
 *
 */
public class XmlStartEditPanel extends JPanel implements ItemListener {
    /**
     * Constant serialized ID used for compatibility.
     */
    private static final long serialVersionUID = 277573064575216805L;
    private JComboBox<FileType> fileTypeSelector;
    private JPanel editPanel;
    private HashMap<FileType, XmlStartEditSubPanel> panels;

    /**
     * Constructs an XmlStartEditPanel object with no type specified. This
     * constructor is used if the user wants to create a new config XML.
     */
    public XmlStartEditPanel() {
        super();
        init();
    }

    /**
     * Constructs an XmlStartEditPanel object with a type specified. This
     * constructor is used if the user wants to edit an existing config XML.
     * 
     * @param type
     */
    public XmlStartEditPanel(FileType type) {
        super();
        init();

        fileTypeSelector.setSelectedItem(type);
    }

    private void init() {
        panels = new HashMap<FileType, XmlStartEditSubPanel>();
        this.setLayout(new BorderLayout());

        fileTypeSelector = new JComboBox<FileType>(FileType.values());
        fileTypeSelector.addItemListener(this);
        this.add(fileTypeSelector, BorderLayout.NORTH);

        editPanel = new JPanel();
        editPanel.setLayout(new CardLayout());
        XmlTxtEditPanel txtPanel = new XmlTxtEditPanel();
        XmlXlsEditPanel xlsPanel = new XmlXlsEditPanel();
        editPanel.add(txtPanel, "TXT");
        editPanel.add(xlsPanel, "XLS");
        this.add(editPanel);

        // add panels to the map
        panels.put(FileType.TXT, txtPanel);
        panels.put(FileType.XLS, xlsPanel);
    }

    /**
     * Calls the getValues method of the panel associated with the specified
     * FileType.
     * 
     * @param type
     *            FileType specifying the panel you want to call.
     * @return
     */
    public final String[] getValues(final FileType type) {
        if (panels.containsKey(type)) {
            return panels.get(type).getValues();
        }
        return null;
    }

    /**
     * Gets the FileType that is currently selected in fileTypeSelector.
     * 
     * @return FileType that is currently selected.
     */
    public final FileType getSelectedType() {
        return (FileType) fileTypeSelector.getSelectedItem();
    }

    /**
     * Calls the setValues method of the panel associated with the specified
     * FileType.
     * 
     * @param values
     *            array containing the values to set
     * @param type
     *            FileType related with the panel to access
     */
    public final void setValues(final String[] values, final FileType type) {
        if (panels.containsKey(type)) {
            panels.get(type).setValues(values);
        }
    }

    /**
     * Shows a panel depending on the FileType selected in fileTypeSelector.
     * 
     * @param evt
     *            ItemEvent object containing information
     */
    public final void itemStateChanged(final ItemEvent evt) {
        CardLayout cl = (CardLayout) (editPanel.getLayout());
        cl.show(editPanel, evt.getItem().toString());
    }
}