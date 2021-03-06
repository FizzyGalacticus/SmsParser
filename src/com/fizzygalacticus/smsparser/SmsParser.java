/**
 * 
 */
package com.fizzygalacticus.smsparser;

import com.fizzygalacticus.html.BootstrapPage;
import com.fizzygalacticus.html.Br;
import com.fizzygalacticus.html.Element;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.HeadlessException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JList;

import javax.swing.JTextArea;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.FontFormatException;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Box;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

/**
 * @author FizzyGalacticus
 *
 */
public class SmsParser extends JFrame {
	private static final long serialVersionUID = 7280291368835838916L;
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem openMenuItem = new JMenuItem("Open...");
	private JMenuItem exitMenuItem = new JMenuItem("Exit");
	private JTextArea messageArea = new JTextArea();
	private JScrollPane messageScrollArea = new JScrollPane();	
	private JScrollPane contactScrollArea = new JScrollPane();	
	private JList<String> contactList = new JList<>(new DefaultListModel<String>());
	private JLabel infoLabel = new JLabel("Info: ");
	private JLabel infoText = new JLabel("Please open a valid *.xml file.");
	private GroupLayout groupLayout = new GroupLayout(getContentPane());
	
	private HashMap<String, ArrayList<Message>> messageMap = new HashMap<String, ArrayList<Message>>();
	private JTextField searchInput = new JTextField();
	private final Component horizontalStrut = Box.createHorizontalStrut(403);
	private final JMenu exportMenu = new JMenu("Export...");
	private final JMenuItem toPdfMenuItem = new JMenuItem("To PDF");
	private final JMenuItem toJsonMenuItem = new JMenuItem("To JSON");
	private final JMenuItem toHtmlMenuItem = new JMenuItem("To HTML");

	Font josefinFontRegular = null;
	Font josefinFontBold = null;

	/**
	 * @throws HeadlessException
	 */
	public SmsParser() throws HeadlessException {
		try {
			this.josefinFontRegular = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResource("/fonts/JosefinSans-Regular.ttf").openStream());
			this.josefinFontRegular = this.josefinFontRegular.deriveFont(14f);
			this.josefinFontBold = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResource("/fonts/JosefinSans-Bold.ttf").openStream());
			this.josefinFontBold = this.josefinFontBold.deriveFont(14f);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getContentPane().setFont(this.josefinFontRegular);
		setIconImage(Toolkit.getDefaultToolkit().getImage(SmsParser.class.getResource("/icons/messageIcon.png")));
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(contactScrollArea, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(messageScrollArea, GroupLayout.PREFERRED_SIZE, 457, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(infoLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(infoText, GroupLayout.PREFERRED_SIZE, 470, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(contactScrollArea, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
						.addComponent(messageScrollArea, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(infoLabel)
						.addComponent(infoText))
					.addContainerGap())
		);
		setTitle("SMS Parser");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.setSize(640, 480);
		this.setResizable(false);
		setFont(this.josefinFontRegular);
		
		this.createMenus();
		messageArea.setFont(this.josefinFontRegular);
		
		this.messageArea.setLineWrap(true);
		contactList.setFont(this.josefinFontBold);
		contactList.addListSelectionListener(new SmsParserActionListener());
		
		messageScrollArea.setViewportView(this.messageArea);
		contactScrollArea.setViewportView(contactList);
		getContentPane().setLayout(groupLayout);
		
		this.setVisible(true);
			infoLabel.setFont(this.josefinFontBold);
			infoText.setFont(this.josefinFontRegular);
	}
	
	public void createMenus() {
		menuBar.setFont(this.josefinFontBold);
		setJMenuBar(this.menuBar);
		fileMenu.setFont(this.josefinFontBold);
		
		this.menuBar.add(this.fileMenu);
		openMenuItem.setFont(this.josefinFontBold);
		
		this.openMenuItem.addActionListener(new SmsParserActionListener());
		this.fileMenu.add(this.openMenuItem);
		exitMenuItem.setFont(this.josefinFontBold);
		
		this.exitMenuItem.addActionListener(new SmsParserActionListener());
		this.fileMenu.add(this.exitMenuItem);
		toPdfMenuItem.setFont(this.josefinFontBold);
		
		this.toPdfMenuItem.addActionListener(new SmsParserActionListener());
		exportMenu.setFont(this.josefinFontBold);
		this.exportMenu.add(this.toPdfMenuItem);
		
		menuBar.add(exportMenu);
		toJsonMenuItem.setFont(this.josefinFontBold);
		this.toJsonMenuItem.addActionListener(new SmsParserActionListener());
		
		exportMenu.add(toJsonMenuItem);
		
		toHtmlMenuItem.setFont(this.josefinFontBold);
		this.toHtmlMenuItem.addActionListener(new SmsParserActionListener());
		
		exportMenu.add(toHtmlMenuItem);
		
		menuBar.add(horizontalStrut);
		searchInput.addKeyListener(new SmsParserActionListener());
		
		searchInput.setFont(this.josefinFontRegular);
		searchInput.setText("Search...");
		menuBar.add(searchInput);
		searchInput.setColumns(3);
	}
	
	public void populateMessageFields() {
		this.infoText.setText("Displaying messages...");
		for(String contact : this.messageMap.keySet()) {
			DefaultListModel<String> model = (DefaultListModel<String>) this.contactList.getModel();
			model.addElement(contact);
		}
		
		this.contactList.setSelectedIndex(0);
		this.infoText.setText("Done.");
	}

	private class SmsParserActionListener extends KeyAdapter implements ActionListener, ListSelectionListener {
		private static final int LEFT_MOUSE_BUTTON = 16;
		private static final int RIGHT_MOUSE_BUTTON = 4;
		
		public void keyReleased(KeyEvent e) {
			String searchText = searchInput.getText();
			String messageAreaText = messageArea.getText();
			Highlighter highlighter = messageArea.getHighlighter();
			HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.decode("#85a7fc"));
			highlighter.removeAllHighlights();
			
			if(searchText.length() > 0 && messageAreaText.length() > 0) {
				ArrayList<Integer> indices = this.getAllIndicesOfMatchingWords(searchText, messageAreaText);
				
				for(Integer index : indices) {
					try {
						highlighter.addHighlight(index, index + searchText.length(), painter);
					} catch (BadLocationException except) {}
				}
			}
		}
		
		public void actionPerformed(ActionEvent arg0) {
			int modifier = arg0.getModifiers();
			Object source = arg0.getSource();
			
			//Actions on exit menu item
			if(source == exitMenuItem) {
				if(modifier == SmsParserActionListener.LEFT_MOUSE_BUTTON)
					this.exit();
			}
			//Actions on open menu item
			else if(source == openMenuItem) {
				if(modifier == SmsParserActionListener.LEFT_MOUSE_BUTTON) {
					String file = this.openFileChooser(new FileNameExtensionFilter("XML File", "xml", "XML"));
					if(file != null && file.length() > 0) {
						infoText.setText("Loading...");
						((DefaultListModel<String>) contactList.getModel()).removeAllElements();
						messageArea.setText("");
						messageMap.clear();
						String xml = this.getXmlFromFile(file);
						JSONObject json = this.convertXmlToJson(xml);
						this.handleMessages(json);
					}
				}
			}
			//Actions on export menu item
			else if(source == toPdfMenuItem) {
				if(modifier == SmsParserActionListener.LEFT_MOUSE_BUTTON) {
					String filename = this.openFileChooser(new FileNameExtensionFilter("PDF File", "pdf", "PDF"));
					Document document = new Document();
					infoText.setText("Exporting to PDF...");
					try {
						PdfWriter.getInstance(document, new FileOutputStream(new File(filename)));
						document.open();
						
						ArrayList<Message> selectedMessages = messageMap.get(contactList.getSelectedValue());
						for(Message message : selectedMessages) {
							document.add(new Paragraph(message.toString()));
							document.add(Chunk.NEWLINE);
						}
						
						document.close();
						infoText.setText("Successfully exported messages to: " + filename);
						
					} catch (Exception e) {
						infoText.setText("Failed to export: Could not write to file.");
					}
				}
			}
			else if(source == toJsonMenuItem) {
				if(modifier == SmsParserActionListener.LEFT_MOUSE_BUTTON) {
					String filename = this.openFileChooser(new FileNameExtensionFilter("JSON File", "json", "JSON"));
					JSONObject json = new JSONObject();
					ArrayList<Message> selectedMessages = messageMap.get(contactList.getSelectedValue());
					json.put("contact_name", selectedMessages.get(0).getContactName());
					JSONArray arr = new JSONArray();
					infoText.setText("Exporting to JSON...");
					for(Message message : selectedMessages) {
						JSONObject msg = new JSONObject();
						msg.put("message", message.getBody());
						msg.put("date", message.getReadableDateReceived());
						arr.put(msg);
					}
					json.put("messages", arr);
					
					try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
			            writer.write(json.toString());
			            infoText.setText("Successfully exported messages to: " + filename);
			        } catch (IOException e) {
						infoText.setText("Failed to export: Could not write to file.");
					}
				}
			}
			else if(source == toHtmlMenuItem) {
				String filename = this.openFileChooser(new FileNameExtensionFilter("HTML File", "html", "HTML"));
				ArrayList<Message> selectedMessages = messageMap.get(contactList.getSelectedValue());
				BootstrapPage page = new BootstrapPage("SMS Backup - " + selectedMessages.get(0).getContactName());
				page.addCss("https://codepen.io/8eni/pen/YWoRGm.css");
				
				for(int i = 0; i < selectedMessages.size(); i++) {
					Message message = selectedMessages.get(i);
					Element row = BootstrapPage.createRow();
					Element col = BootstrapPage.createColumn(12);
					col.addClass("speech-wrapper");
					
					Element bubble = new Element();
					bubble.addClass("bubble");
					
					Element txt = new Element();
					txt.addClass("txt");
					
					Element name = new Element("p");
					name.addClass("name");
					
					Element nameSpan = new Element("span");
					
					Element messageElem = new Element("p");
					messageElem.addClass("message");
					messageElem.addChild(message.getBody());
					
					Element timestamp = new Element("span");
					timestamp.addClass("timestamp");
					timestamp.addChild(message.getReadableDateReceived());
					
					Element bubbleArrow = new Element();
					bubbleArrow.addClass("bubble-arrow");
					
					if(message.getSenderType() == Message.TYPE_TO) {
						bubble.addClass("alt");
						name.addClass("alt");
						bubbleArrow.addClass("alt");

						name.addChild("You");
					}
					else {
						name.addChild(message.getContactName());
						nameSpan.addChild(" ~ " + message.getAddress());
					}
					
					name.addChild(nameSpan);
					txt.addChild(name);
					txt.addChild(messageElem);
					txt.addChild(new Br());
					txt.addChild(timestamp);
					bubble.addChild(txt);
					bubble.addChild(bubbleArrow);
					
					col.addChild(bubble);
					row.addChild(col);
					page.addBodyObject(row);
				}
				
				try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
		            writer.write(page.toString());
		            infoText.setText("Successfully exported messages to: " + filename);
		        } catch (IOException e) {
					infoText.setText("Failed to export: Could not write to file.");
				}
			}
		}
		
		public void valueChanged(ListSelectionEvent e) {
			@SuppressWarnings("unchecked")
			JList<String> source = (JList<String>) e.getSource();
			String selectedKey = source.getSelectedValue();
			
			if(messageMap.containsKey(selectedKey)) {
				ArrayList<Message> contactMessages = messageMap.get(selectedKey);
				messageArea.setText("");
				for(Message message : contactMessages) {
					messageArea.append(message.toString() + "\n\n");
				}
			}
		}
		
		private ArrayList<Integer> getAllIndicesOfMatchingWords(String searchStr, String fullStr) {
			ArrayList<Integer> ret = new ArrayList<Integer>();
			
			int index = 0;
			while(index > -1) {
				index = fullStr.toLowerCase().indexOf(searchStr.toLowerCase(), index+1);
				if(index > -1)
					ret.add(new Integer(index));
			}
			
			return ret;
		}
		
		private void handleMessages(JSONObject messages) {
			JSONObject smses = (JSONObject) messages.get("smses");
			JSONArray sms = smses.getJSONArray("sms");
			
			for(int i = 0; i < sms.length(); i++) {
				JSONObject jsonMessage = sms.getJSONObject(i);
				Message message = new Message();
				message.setDateReceived(jsonMessage.getLong("date"));
				message.setReadableDateReceived(jsonMessage.getString("readable_date"));
				message.setContactName(jsonMessage.getString("contact_name"));
				message.setDateSent(jsonMessage.getLong("date_sent"));
				message.setSenderType(jsonMessage.getInt("type"));
				infoText.setText("Reading message from: " + message.getContactName());
				
				message.setBody(jsonMessage.get("body").toString());
				message.setAddress(jsonMessage.get("address").toString());
				
				if(!messageMap.containsKey(message.getMapKey()))
					messageMap.put(message.getMapKey(), new ArrayList<Message>());
				
				messageMap.get(message.getMapKey()).add(message);
			}
			
			populateMessageFields();
		}
		
		private void exit() {
			System.exit(0);
		}
		
		private String openFileChooser() {
			return this.openFileChooser(null);
		}
		
		private String openFileChooser(FileNameExtensionFilter filter) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			String file = "";
			
			if(filter != null) {
				jfc.setFileFilter(filter);
			}

			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				file = selectedFile.getAbsolutePath();
			}
			
			return file;
		}
		
		private String getXmlFromFile(String file) {
			String xml = "";

			try {
				xml = new String(Files.readAllBytes(Paths.get(file)));
			}
			catch(Exception e) {}

			return xml;
		}
		
		private JSONObject convertXmlToJson(String xml) {
			return XML.toJSONObject(xml);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SmsParser();
	}
}
