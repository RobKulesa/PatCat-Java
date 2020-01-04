package driver;

import java.io.*;
import java.text.SimpleDateFormat;

import javax.swing.*;

import engine.Engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Contains the GUI and runs events on button presses
 * 
 * @author Robert Kulesa
 *
 */
public class Driver {
	private static JTextField titleWeightField;
	private static JTextField abstractWeightField;
	private static JTextField claimWeightField;
	private static JTextField preambleWeightField;
	private static JTextField categoriesFileField;
	private static JTextField patentsFileField;
	private static JTextArea consoleField;
	public static JCheckBox chckbxCategoriesDebug;
	public static JCheckBox chckbxPatentsDebug;
	public static JCheckBox chckbxIndexDebug;
	public static JCheckBox chckbxApplyCategoriesDebug;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("PatCat-Java");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(682, 407);
	    frame.setLocationRelativeTo(null);
	    frame.getContentPane().setLayout(null);
	    
	    Config config = new Config();
	    
	    JLabel titleWeightLabel = new JLabel("Title Weight");
	    titleWeightLabel.setBounds(10, 11, 170, 14);
	    frame.getContentPane().add(titleWeightLabel);
	    
	    titleWeightField = new JTextField();
	    titleWeightField.setHorizontalAlignment(SwingConstants.RIGHT);
	    titleWeightLabel.setLabelFor(titleWeightField);
	    titleWeightField.setText(config.getProperty("titleWeight"));
	    titleWeightField.setBounds(190, 8, 36, 20);
	    frame.getContentPane().add(titleWeightField);
	    titleWeightField.setColumns(10);
	    
	    JLabel abstractWeightLabel = new JLabel("Abstract Weight");
	    abstractWeightLabel.setBounds(10, 39, 170, 14);
	    frame.getContentPane().add(abstractWeightLabel);
	    
	    abstractWeightField = new JTextField();
	    abstractWeightField.setHorizontalAlignment(SwingConstants.RIGHT);
	    abstractWeightLabel.setLabelFor(abstractWeightField);
	    abstractWeightField.setText(config.getProperty("abstractWeight"));
	    abstractWeightField.setColumns(10);
	    abstractWeightField.setBounds(190, 36, 36, 20);
	    frame.getContentPane().add(abstractWeightField);
	    
	    JLabel preambleWeightLabel = new JLabel("Preamble Weight");
	    preambleWeightLabel.setBounds(10, 67, 170, 14);
	    frame.getContentPane().add(preambleWeightLabel);
	    
	    preambleWeightField = new JTextField();
	    preambleWeightField.setHorizontalAlignment(SwingConstants.RIGHT);
	    preambleWeightLabel.setLabelFor(preambleWeightField);
	    preambleWeightField.setText(config.getProperty("preambleWeight"));
	    preambleWeightField.setColumns(10);
	    preambleWeightField.setBounds(190, 64, 36, 20);
	    frame.getContentPane().add(preambleWeightField);
	    
	    JLabel claimWeightLabel = new JLabel("Claim Weight");
	    claimWeightLabel.setBounds(10, 95, 170, 14);
	    frame.getContentPane().add(claimWeightLabel);
	    
	    claimWeightField = new JTextField();
	    claimWeightField.setHorizontalAlignment(SwingConstants.RIGHT);
	    claimWeightLabel.setLabelFor(claimWeightField);
	    claimWeightField.setText(config.getProperty("claimWeight"));
	    claimWeightField.setColumns(10);
	    claimWeightField.setBounds(190, 92, 36, 20);
	    frame.getContentPane().add(claimWeightField);
	    
	    JLabel categoriesFileLabel = new JLabel("Categories File");
	    categoriesFileLabel.setBounds(10, 123, 88, 14);
	    frame.getContentPane().add(categoriesFileLabel);
	    
	    categoriesFileField = new JTextField();
	    categoriesFileField.setHorizontalAlignment(SwingConstants.RIGHT);
	    categoriesFileLabel.setLabelFor(categoriesFileField);
	    categoriesFileField.setText(config.getProperty("categoriesFile"));
	    categoriesFileField.setColumns(10);
	    categoriesFileField.setBounds(108, 120, 118, 20);
	    frame.getContentPane().add(categoriesFileField);
	    
	    JLabel patentsFileLabel = new JLabel("Patents File");
	    patentsFileLabel.setBounds(10, 151, 88, 14);
	    frame.getContentPane().add(patentsFileLabel);
	    
	    patentsFileField = new JTextField();
	    patentsFileField.setHorizontalAlignment(SwingConstants.RIGHT);
	    patentsFileLabel.setLabelFor(patentsFileField);
	    patentsFileField.setText(config.getProperty("patentsFile"));
	    patentsFileField.setColumns(10);
	    patentsFileField.setBounds(108, 148, 118, 20);
	    frame.getContentPane().add(patentsFileField);
	    
	    JButton categorize = new JButton("Categorize");
	    categorize.setBounds(48, 172, 108, 23);
	    frame.getContentPane().add(categorize);
	    
	    JLabel consoleLabel = new JLabel("Console");
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
	    btnSaveDebug.setBounds(48, 336, 108, 23);
	    frame.getContentPane().add(btnSaveDebug);
	    
	    JScrollPane scrollPane = new JScrollPane();
	    scrollPane.setBounds(250, 22, 408, 337);
	    frame.getContentPane().add(scrollPane);
	    
	    consoleField = new JTextArea();
	    scrollPane.setViewportView(consoleField);
	    consoleField.setEditable(false);
	    consoleLabel.setLabelFor(consoleField);
	   
	    chckbxCategoriesDebug = new JCheckBox("Show Categories Debug");
	    chckbxCategoriesDebug.setBounds(10, 228, 234, 23);
	    frame.getContentPane().add(chckbxCategoriesDebug);
	    
	    chckbxPatentsDebug = new JCheckBox("Show Patents Debug");
	    chckbxPatentsDebug.setBounds(10, 254, 234, 23);
	    frame.getContentPane().add(chckbxPatentsDebug);
	    
	    chckbxIndexDebug = new JCheckBox("Show Index Debug");
	    chckbxIndexDebug.setBounds(10, 280, 234, 23);
	    frame.getContentPane().add(chckbxIndexDebug);
	    
	    chckbxApplyCategoriesDebug = new JCheckBox("Show Apply Categories Debug");
	    chckbxApplyCategoriesDebug.setBounds(10, 306, 234, 23);
	    frame.getContentPane().add(chckbxApplyCategoriesDebug);
	    
	    JCheckBox chckbxSelectAll = new JCheckBox("Select All");
	    chckbxSelectAll.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if(chckbxSelectAll.isSelected()) {
	    			chckbxCategoriesDebug.setSelected(true);
	    			chckbxPatentsDebug.setSelected(true);
	    			chckbxIndexDebug.setSelected(true);
	    			chckbxApplyCategoriesDebug.setSelected(true);
	    		} else {
	    			chckbxCategoriesDebug.setSelected(false);
	    			chckbxPatentsDebug.setSelected(false);
	    			chckbxIndexDebug.setSelected(false);
	    			chckbxApplyCategoriesDebug.setSelected(false);
	    		}
	    	}
	    });
	    chckbxSelectAll.setBounds(10, 202, 234, 23);
	    frame.getContentPane().add(chckbxSelectAll);
	    
	    categorize.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
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
	    			addTextNew("IOException @ engine.makeIndex, files not found?");
	    			addTextNew(ex.getMessage());
	    		}
	    		addTextNew("Finished");
	    	}
	    	
	    });
	    frame.setVisible(true);
	}
	
	/**
     * Add text to console with newline
     * @param str text
     */
    public static void addTextNew(String str) {
    	consoleField.append(str + "\n");
    }
    
    public static void addText(String str) {
    	consoleField.append(str);
    }
}
