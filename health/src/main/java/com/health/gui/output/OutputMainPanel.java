package com.health.gui.output;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.health.Table;

/**
 * Represents the mainpanel of the Output section. Shows tables and
 * visualizations of the analysis selected in the sidepanel.
 * @author Bjorn van der Laan
 *
 */
public class OutputMainPanel extends JPanel {
    /**
     * Tabbed pane containing the generated results.
     */
    private static JTabbedPane pane = new JTabbedPane();
    /**
     * Constant serialized ID used for compatibility.
     */
    private static final long serialVersionUID = -5652640933659529127L;

    private static void setComponentData(final Object element, String key) {
        Component component = (Component) element;
        pane.add("Visual: " + key, component);
    }

    /**
     * Sets the data of the panel based on the input.
     * @param map2
     *            Map containing the data
     */
    public static final void setData(final Map<String, Object> map2) {
        pane.removeAll();
        for (String key : map2.keySet()) {
            Object element = map2.get(key);
            if (element instanceof Table) {
                setTableData(element, key);
            } else if (element instanceof JTable) {
                setJTableData(element, key);
            } else if (element instanceof Component) {
                setComponentData(element, key);
            }

            pane.repaint();
            pane.revalidate();
        }
    }

    private static void setJTableData(Object element, String key) {
        JTable jtable = (JTable) element;
        jtable.setEnabled(false);
        jtable.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(jtable);
        pane.add("Matrix: " + key, scroll);
    }

    private static void setTableData(Object element, String key) {
        Table table = (Table) element;
        JTable jtable = table.toJTable();
        jtable.setEnabled(false);
        jtable.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(jtable);
        pane.add("Table: " + key, scroll);
    }

    /**
     * Constructor.
     */
    public OutputMainPanel() {
        super();
        this.setLayout(new BorderLayout());

        pane.setBackground(Color.WHITE);
        this.add(pane, BorderLayout.CENTER);
    }

    /**
     * Gets the pane containing generated output.
     * @return the tabbed pane
     */
    protected static JTabbedPane getPane() {
        return pane;
    }
}
