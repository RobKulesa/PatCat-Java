package driver;

import java.awt.LayoutManager;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;

import engine.Engine;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Driver {
	private static JTextField titleWeightField;
	private static JTextField abstractWeightField;
	private static JTextField claimWeightField;
	private static JTextField preambleWeightField;
	private static JTextField categoriesFileField;
	private static JTextField patentsFileField;
	private static JTextArea consoleField;
	private static StringBuilder builder;
	public static void main(String[] args) {
		JFrame frame = new JFrame("PatCat-Java");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(705, 300);
	    frame.setLocationRelativeTo(null);
	    frame.getContentPane().setLayout(null);
	    
	    Config config = new Config();
	    
	    JLabel titleWeightLabel = new JLabel("Title Weight");
	    titleWeightLabel.setBounds(10, 11, 57, 14);
	    frame.getContentPane().add(titleWeightLabel);
	    
	    titleWeightField = new JTextField();
	    titleWeightLabel.setLabelFor(titleWeightField);
	    titleWeightField.setText(config.getProperty("titleWeight"));
	    titleWeightField.setBounds(96, 8, 36, 20);
	    frame.getContentPane().add(titleWeightField);
	    titleWeightField.setColumns(10);
	    
	    JLabel abstractWeightLabel = new JLabel("Abstract Weight");
	    abstractWeightLabel.setBounds(10, 39, 78, 14);
	    frame.getContentPane().add(abstractWeightLabel);
	    
	    abstractWeightField = new JTextField();
	    abstractWeightLabel.setLabelFor(abstractWeightField);
	    abstractWeightField.setText(config.getProperty("abstractWeight"));
	    abstractWeightField.setColumns(10);
	    abstractWeightField.setBounds(96, 36, 36, 20);
	    frame.getContentPane().add(abstractWeightField);
	    
	    JLabel preambleWeightLabel = new JLabel("Preamble Weight");
	    preambleWeightLabel.setBounds(10, 67, 87, 14);
	    frame.getContentPane().add(preambleWeightLabel);
	    
	    preambleWeightField = new JTextField();
	    preambleWeightLabel.setLabelFor(preambleWeightField);
	    preambleWeightField.setText(config.getProperty("preambleWeight"));
	    preambleWeightField.setColumns(10);
	    preambleWeightField.setBounds(96, 64, 36, 20);
	    frame.getContentPane().add(preambleWeightField);
	    
	    JLabel claimWeightLabel = new JLabel("Claim Weight");
	    claimWeightLabel.setBounds(10, 95, 78, 14);
	    frame.getContentPane().add(claimWeightLabel);
	    
	    claimWeightField = new JTextField();
	    claimWeightLabel.setLabelFor(claimWeightField);
	    claimWeightField.setText(config.getProperty("claimWeight"));
	    claimWeightField.setColumns(10);
	    claimWeightField.setBounds(96, 92, 36, 20);
	    frame.getContentPane().add(claimWeightField);
	    
	    JLabel categoriesFileLabel = new JLabel("Categories File");
	    categoriesFileLabel.setBounds(10, 123, 78, 14);
	    frame.getContentPane().add(categoriesFileLabel);
	    
	    categoriesFileField = new JTextField();
	    categoriesFileLabel.setLabelFor(categoriesFileField);
	    categoriesFileField.setText(config.getProperty("categoriesFile"));
	    categoriesFileField.setColumns(10);
	    categoriesFileField.setBounds(96, 120, 130, 20);
	    frame.getContentPane().add(categoriesFileField);
	    
	    JLabel patentsFileLabel = new JLabel("Patents File");
	    patentsFileLabel.setBounds(10, 151, 78, 14);
	    frame.getContentPane().add(patentsFileLabel);
	    
	    patentsFileField = new JTextField();
	    patentsFileLabel.setLabelFor(patentsFileField);
	    patentsFileField.setText(config.getProperty("patentsFile"));
	    patentsFileField.setColumns(10);
	    patentsFileField.setBounds(96, 148, 130, 20);
	    frame.getContentPane().add(patentsFileField);
	    
	    JButton categorize = new JButton("Categorize");
	    categorize.setBounds(48, 176, 108, 23);
	    frame.getContentPane().add(categorize);
	    
	    consoleField = new JTextArea();
	    consoleField.setEditable(false);
	    consoleField.setBounds(250, 22, 429, 228);
	    frame.getContentPane().add(consoleField);
	    
	    JLabel consoleLabel = new JLabel("Console");
	    consoleLabel.setLabelFor(consoleField);
	    consoleLabel.setBounds(251, 8, 46, 14);
	    frame.getContentPane().add(consoleLabel);
	    
	    JButton btnSaveDebug = new JButton("Save Debug");
	    btnSaveDebug.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		try {
	    			SimpleDateFormat format = new SimpleDateFormat("MM-dd-yy.HH.mm.ss");
					PrintWriter out = new PrintWriter("debug." + format.format(System.currentTimeMillis()) + ".txt");
					out.print(consoleField.getText());
					out.close();
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				}
	    	}
	    });
	    btnSaveDebug.setBounds(48, 207, 108, 23);
	    frame.getContentPane().add(btnSaveDebug);
	    
	    categorize.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		builder = new StringBuilder();
	    		consoleField.setText(null);
	    		Engine engine = new Engine();
	    		config.setProperty("titleWeight", titleWeightField.getText());
	    		config.setProperty("abstractWeight", abstractWeightField.getText());
	    		config.setProperty("preambleWeight", preambleWeightField.getText());
	    		config.setProperty("claimWeight", claimWeightField.getText());
	    		config.setProperty("categoriesFile", categoriesFileField.getText());
	    		config.setProperty("patentsFile", patentsFileField.getText());
	    		
	    		try {
	    			engine.makeIndex(config.getProperty("categoriesFile"), config.getProperty("patentsFile"));
	    		} catch (IOException ex) {
	    			addText("IOException @ engine.makeIndex, files not found?");
	    			addText(ex.getMessage());
	    		}
	    		addText("Finished");
	    	}
	    	
	    });
	    frame.setVisible(true);
	}
	/**
     * Add text to console
     * @param str text
     */
    public static void addText(String str) {
    	builder.append(str + "\n");
    	consoleField.setText(builder.toString());
    }
}
