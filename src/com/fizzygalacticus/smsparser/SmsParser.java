/**
 * 
 */
package com.fizzygalacticus.smsparser;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
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
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
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
	private final JMenu editMenu = new JMenu("Edit");
	private final JMenuItem exportMenuItem = new JMenuItem("Export");

	/**
	 * @throws HeadlessException
	 */
	public SmsParser() throws HeadlessException {
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
		setFont(new Font("DejaVu Serif", Font.PLAIN, 12));
		
		this.createMenus();
		
		this.messageArea.setLineWrap(true);
		contactList.addListSelectionListener(new SmsParserActionListener());
		
		messageScrollArea.setViewportView(this.messageArea);
		contactScrollArea.setViewportView(contactList);
		getContentPane().setLayout(groupLayout);
		
		this.setVisible(true);
			infoLabel.setFont(new Font("DejaVu Serif", Font.BOLD, 12));
			infoText.setFont(new Font("DejaVu Serif", Font.BOLD, 12));
	}
	
	public void createMenus() {
		menuBar.setFont(new Font("DejaVu Serif", Font.BOLD, 12));
		setJMenuBar(this.menuBar);
		fileMenu.setFont(new Font("DejaVu Serif", Font.BOLD, 12));
		
		this.menuBar.add(this.fileMenu);
		openMenuItem.setFont(new Font("DejaVu Serif", Font.BOLD, 12));
		
		this.openMenuItem.addActionListener(new SmsParserActionListener());
		this.fileMenu.add(this.openMenuItem);
		exitMenuItem.setFont(new Font("DejaVu Serif", Font.BOLD, 12));
		
		this.exitMenuItem.addActionListener(new SmsParserActionListener());
		this.fileMenu.add(this.exitMenuItem);
		exportMenuItem.setFont(new Font("DejaVu Serif", Font.BOLD, 12));
		
		this.exportMenuItem.addActionListener(new SmsParserActionListener());
		this.editMenu.add(this.exportMenuItem);
		
		menuBar.add(editMenu);
		
		menuBar.add(horizontalStrut);
		searchInput.addKeyListener(new SmsParserActionListener());
		
		searchInput.setFont(new Font("DejaVu Serif", Font.PLAIN, 12));
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
	
	private class Message {
		private static final int TYPE_TO = 2;
		private static final int TYPE_FROM = 1;
		private Long dateReceived = null;
		private Long dateSent = null;
		private String readableDateReceived = null;
		private String contactName = null;
		private int type = 0;
		private String address = null;
		private String body = null;
		
		public Long getDateReceived() {
			return dateReceived;
		}
		public void setDateReceived(Long dateReceived) {
			this.dateReceived = dateReceived;
		}
		public String getReadableDateReceived() {
			return readableDateReceived;
		}
		public void setReadableDateReceived(String readableDateReceived) {
			this.readableDateReceived = readableDateReceived;
		}
		public Long getDateSent() {
			return dateSent;
		}
		public void setDateSent(Long dateSent) {
			this.dateSent = dateSent;
		}
		public String getContactName() {
			return contactName;
		}
		public void setContactName(String contactName) {
			this.contactName = contactName;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = this.formatPhoneNumber(address);
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		
		private String formatPhoneNumber(String num) {
			String ret = "";
			
			if(num != null) {
				if(num.length() < 7)
					ret = num;
				else {
					PhoneNumberUtil util = PhoneNumberUtil.getInstance();
					PhoneNumber number;
					try {
						number = util.parseAndKeepRawInput(num, "US");
						ret  = util.format(number, PhoneNumberFormat.INTERNATIONAL);
					} catch (NumberParseException e) {
						ret = num;
					}
				}
			}
			
			return ret;
		}
		
		public String getMapKey() {
			return this.getContactName() + " (" + this.getAddress() + ")";
		}
		
		public String toString() {
			String ret = "";
			
			if(this.getType() == Message.TYPE_FROM)
				ret += this.getContactName() + " ";
			else if(this.getType() == Message.TYPE_TO)
				ret += "You ";
			
			ret += "(" + this.readableDateReceived + "): ";
			ret += this.getBody();
			
			return ret;
		}
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
					String file = this.openFileChooser();
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
			else if(source == exportMenuItem) {
				if(modifier == SmsParserActionListener.LEFT_MOUSE_BUTTON) {
					String filename = this.openFileChooser();
					Document document = new Document();
					try {
						PdfWriter.getInstance(document, new FileOutputStream(new File(filename)));
						document.open();
						ArrayList<Message> messages = messageMap.get(contactList.getSelectedValue());
						
						for(Message message : messages) {
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
				message.setType(jsonMessage.getInt("type"));
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
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			String file = "";

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
