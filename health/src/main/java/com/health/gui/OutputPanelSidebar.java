package com.health.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Date;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

public class OutputPanelSidebar extends JPanel {
    /**
     * Constant serialized ID used for compatibility.
     */
    private static final long serialVersionUID = 9050949741413643882L;
    protected static JList<String> list;
    private static HashMap<String, HashMap<String, Object>> dataMap;

    public OutputPanelSidebar() {
        super();
        this.setLayout(new BorderLayout());

        dataMap = new HashMap<String, HashMap<String, Object>>();

        DefaultListModel<String> listModel = new DefaultListModel<String>();
        list = new JList<String>(listModel);
        list.setBackground(Color.white);
        this.add(list, BorderLayout.CENTER);

        this.setVisible(true);
    }

    private static void addElement(String el) {
        DefaultListModel<String> model = (DefaultListModel<String>) list
                .getModel();
        model.addElement(el);
        list.repaint();
        list.revalidate();
    }

    protected static HashMap<String, Object> getData(String el) {
        return dataMap.get(el);
    }

    public static void add(HashMap<String, Object> data) {
        Date date = new Date();
        String name = "Analysis " + date.toString();
        OutputPanelSidebar.addElement(name);
        dataMap.put(name, data);
    }
}
