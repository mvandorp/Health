package com.health.gui.xmlwizard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.health.FileType;
import com.health.input.InputDescriptor;
import com.health.input.InputException;

/**
 * Represents the wizard panel where one can specify the delimiters and columns
 * of the Config XML.
 * @author Bjorn van der Laan
 *
 */
public class XmlEditPanel extends JPanel {
    /**
     * Constant serialized ID used for compatibility.
     */
    private static final long serialVersionUID = 2790653737107250316L;
    private Path xml;
    private XmlStartEditPanel startPanel;
    private XmlColumnEditPanel columnPanel;

    // private JButton backButton;
    private JButton continueButton;

    /**
     * Constructs a XmlEditPanel object.
     */
    public XmlEditPanel() {
        super();
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        startPanel = new XmlStartEditPanel();
        this.add(startPanel, BorderLayout.NORTH);
        columnPanel = new XmlColumnEditPanel();
        this.add(columnPanel, BorderLayout.CENTER);

        continueButton = new JButton("Continue");
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                XmlWizard.setXml(getValues());
                XmlWizard.savePanel.setValues();
                XmlWizard.nextPanel();
            }
        });
        this.add(continueButton, BorderLayout.SOUTH);
    }

    /**
     * Models the input values as a {@link XmlConfigObject} and returns it.
     * @return XmlConfigObject containing the input values
     */
    public final XmlConfigObject getValues() {
        XmlConfigObject config = new XmlConfigObject();

        config.setType(startPanel.getSelectedType());
        config.setValues(startPanel.getValues(config.getType()));
        config.setColumns(columnPanel.getColumns());
        config.setColumnTypes(columnPanel.getColumnTypes());

        if (this.xml != null) {
            config.setPath(this.xml);
        }
        
        return config;
    }

    /**
     * Loads current values of the selected XML file en sets the fields of the
     * panel.
     * @param xml
     *            Path of XML file to edit
     */
    public final void setValues(final Path xml) {
        try {
            InputDescriptor id = new InputDescriptor(xml.toString());

            if (id.getFormat().equals("xlsx") || id.getFormat().equals("xls")) {
                JOptionPane.showMessageDialog(new JFrame(),
                        "XLS support has not been implemented yet.", "Whoops!",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                String[] values = { id.getStartDelimiter(),
                        id.getEndDelimiter(), id.getDelimiter() };
                startPanel.setValues(values, FileType.TXT);
            }

            // set the columns
            columnPanel.setColumns(id.getColumns(), id.getColumnTypes());
            this.xml = xml;
        } catch (ParserConfigurationException | SAXException | IOException
                | InputException e) {
            System.out.println("Error loading: " + xml.toString());
        }
    }
}