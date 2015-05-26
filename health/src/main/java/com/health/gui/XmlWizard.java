package com.health.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.health.input.InputDescriptor;
import com.health.input.InputException;

public class XmlWizard extends JFrame implements ActionListener {
	XmlFilePanel filePanel;
	XmlDelimiterEditPanel delimPanel;
	
	public XmlWizard(String path) {
		super();
		this.setSize(500, 500);
				
		filePanel = new XmlFilePanel(path);
		delimPanel = new XmlDelimiterEditPanel();
		
		this.getContentPane().add(filePanel);
		filePanel.addActionListenerToNewFileButton(this);
		filePanel.addActionListenerToSelectFileButton(this);
			
		this.setTitle("XML Editor");
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		if(source.equals(filePanel.getNewFileButton())) {
			this.getContentPane().remove(filePanel);
			this.setContentPane(delimPanel);
			this.repaint();
			this.revalidate();
		}
		else if(source.equals(filePanel.getSelectFileButton())) {
			if(filePanel.getSelectedFile() != null) {
				delimPanel.setSelectedXML(filePanel.getSelectedFile());
				System.out.println("Selected XML: " + filePanel.getSelectedFile().toString());
				this.getContentPane().remove(filePanel);
				this.setContentPane(delimPanel);
				this.repaint();
				this.revalidate();	
			}
		}
	}
}

/**
 * Screen where the user can either select a xml file to edit, or create a new xml file
 * @author Bjorn
 *
 */
class XmlFilePanel extends JPanel {	
	private JButton newFileButton;
	private JButton selectFileButton;
	private FileList fileList;

	public XmlFilePanel(String path) {
		super();
		this.setLayout(new BorderLayout());
		
		//add list model
		DefaultListModel<Path> listModel = new DefaultListModel<Path>();
		fileList = new FileList(path, listModel);
		this.add(fileList, BorderLayout.CENTER);
		
		//add buttons
		JPanel buttonPanel = new JPanel();
		newFileButton = new JButton("Create a new file");
		selectFileButton = new JButton("Edit selected file");
		buttonPanel.add(newFileButton);
		buttonPanel.add(selectFileButton);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public JButton getNewFileButton() {
		return newFileButton;
	}
	
	public void setNewFileButton(JButton newButton) {
		this.newFileButton = newButton;
	}

	public JButton getSelectFileButton() {
		return selectFileButton;
	}
	
	public void setSelectFileButton(JButton selectButton) {
		this.selectFileButton = selectButton;
	}
	
	public void addActionListenerToNewFileButton(ActionListener al) {
		newFileButton.addActionListener(al);
	}
	
	public void addActionListenerToSelectFileButton(ActionListener al) {
		selectFileButton.addActionListener(al);
	}
	
	public Path getSelectedFile() {
		return this.fileList.getSelectedValue();
	}
}

class XmlDelimiterEditPanel extends JPanel {
	private JTextField startDelimField, endDelimField, delimiterField;
	private JLabel startDelimLabel, endDelimLabel, delimiterLabel;
	private InputDescriptor id;
	
	public XmlDelimiterEditPanel() {
		super();
		this.setLayout(new GridLayout(0,2));
		startDelimLabel = new JLabel("Start Delimiter");
		endDelimLabel = new JLabel("End Delimiter");
		delimiterLabel = new JLabel("Delimiter");
		
		startDelimField = new JTextField();
		startDelimField.setHorizontalAlignment(JTextField.CENTER);
		endDelimField = new JTextField();
		endDelimField.setHorizontalAlignment(JTextField.CENTER);
		delimiterField = new JTextField();
		delimiterField.setHorizontalAlignment(JTextField.CENTER);
		
		this.add(startDelimLabel);
		this.add(startDelimField);
		this.add(delimiterLabel);
		this.add(delimiterField);
		this.add(endDelimLabel);
		this.add(endDelimField);
	}

	public void setSelectedXML(Path selectedXML) {
		try {
			this.id = new InputDescriptor(selectedXML.toString());
			
			this.startDelimField.setText(this.getStartDelimiter());
			this.endDelimField.setText(this.getEndDelimiter());
			this.delimiterField.setText(this.getDelimiter());
			
		} catch (ParserConfigurationException | SAXException | IOException
				| InputException e) {
			System.out.println("Error creating inputdescriptor! "+selectedXML.toString());
		}
	}
	
	public String getStartDelimiter() {
		return this.id.getStartDelimiter();
	}
	
	public String getEndDelimiter() {
		return this.id.getEndDelimiter();
	}
	
	public String getDelimiter() {
		return this.id.getDelimiter();
	}
}

class FileList extends JList<Path> {
	public FileList(String path, DefaultListModel<Path> listModel) {
		super(listModel);
		this.setPreferredSize(new Dimension(200,300));
		this.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		this.setBackground(Color.WHITE);
		
		Path dir = Paths.get(path);

		try {
			DirectoryStream<Path> stream;
			stream = Files.newDirectoryStream(dir);
			Iterator<Path> iterator = stream.iterator();
			
			while(iterator.hasNext()) {
				listModel.addElement(iterator.next());
			}
		} catch (IOException e) {
			//Directory not found, add no elements
		}		
	}
}