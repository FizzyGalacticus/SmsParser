/**
 * 
 */
package com.fizzygalacticus.smsparser;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.awt.GraphicsConfiguration;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

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
	private GroupLayout groupLayout = new GroupLayout(getContentPane());
	
	private HashMap<String, ArrayList<Message>> messageMap = new HashMap<String, ArrayList<Message>>();

	/**
	 * @throws HeadlessException
	 */
	public SmsParser() throws HeadlessException {
		this("Ike's SMS Parser");	
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(infoLabel, GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(contactScrollArea, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(messageScrollArea, GroupLayout.PREFERRED_SIZE, 457, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(messageScrollArea, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
						.addComponent(contactScrollArea, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(infoLabel)
					.addContainerGap())
		);
	}

	/**
	 * @param gc
	 */
	public SmsParser(GraphicsConfiguration gc) {
		this("Ike's SMS Parser", gc);
	}

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public SmsParser(String title) throws HeadlessException {
		this(title, null);
	}

	/**
	 * @param title
	 * @param gc
	 */
	public SmsParser(String title, GraphicsConfiguration gc) {
		super(title, gc);
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
	}
	
	public void createMenus() {
		setJMenuBar(this.menuBar);
		
		this.menuBar.add(this.fileMenu);
		
		this.openMenuItem.addActionListener(new SmsParserActionListener());
		this.fileMenu.add(this.openMenuItem);
		
		this.exitMenuItem.addActionListener(new SmsParserActionListener());
		this.fileMenu.add(this.exitMenuItem);
	}
	
	public void populateMessageFields() {
		for(String contact : this.messageMap.keySet()) {
			DefaultListModel<String> model = (DefaultListModel<String>) this.contactList.getModel();
			model.addElement(contact);
		}
		
		this.contactList.setSelectedIndex(0);
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
			PhoneNumber addr = new PhoneNumber();
			if(address.length() > 0 & address.charAt(0) == '+')
				addr.setRawInput(address.substring(1, address.length()));
			else
				addr.setRawInput(address);
			
			PhoneNumberUtil util = PhoneNumberUtil.getInstance();
			this.address = util.format(addr, PhoneNumberFormat.RFC3966);
		}
		public void setAddress(Long address) {
			this.setAddress(address.toString());
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public void setBody(Long body) {
			this.body = body.toString();
		}
		
		public String getMapKey() {
			return this.getContactName() + " (" + this.getAddress() + ")";
		}
		
		public String toString() {
			String ret = "";
			
			if(this.getType() == Message.TYPE_FROM)
				ret += "FROM ";
			else if(this.getType() == Message.TYPE_TO)
				ret += "TO ";
			
			ret += "(" + this.readableDateReceived + "): ";
			ret += this.getBody();
			
			return ret;
		}
	}
	
	private class SmsParserActionListener implements ActionListener, ListSelectionListener {
		private static final int LEFT_MOUSE_BUTTON = 16;
		private static final int RIGHT_MOUSE_BUTTON = 4;
		
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
						((DefaultListModel) contactList.getModel()).removeAllElements();
						messageArea.setText("");
						messageMap.clear();
						String xml = this.getXmlFromFile(file);
						JSONObject json = this.convertXmlToJson(xml);
						this.handleMessages(json);
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
					messageArea.append(message.toString() + "\n");
				}
			}
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
				
				try {
					message.setBody(jsonMessage.getString("body"));
				}
				catch(JSONException e) {
					message.setBody(jsonMessage.getLong("body"));
				}
				
				try {
					message.setAddress(jsonMessage.getString("address"));
				}
				catch(JSONException e) {
					message.setAddress(jsonMessage.getLong("address"));
				}
				
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
		
		private Map<String, Object> jsonToMap(JSONObject json) {
			Map<String, Object> retMap = new HashMap<String, Object>();

			if(json != JSONObject.NULL) {
				retMap = this.toMap(json);
			}

			return retMap;
		}

		private Map<String, Object> toMap(JSONObject object) throws JSONException {
			Map<String, Object> map = new HashMap<String, Object>();

			Iterator<String> keysItr = object.keys();
			while(keysItr.hasNext()) {
				String key = keysItr.next();
				Object value = object.get(key);

				if(value instanceof JSONArray) {
					value = this.toList((JSONArray) value);
				}

				else if(value instanceof JSONObject) {
					value = toMap((JSONObject) value);
				}
				map.put(key, value);
			}

			return map;
		}

		private List<Object> toList(JSONArray array) throws JSONException {
			List<Object> list = new ArrayList<Object>();
			for(int i = 0; i < array.length(); i++) {
				Object value = array.get(i);
				if(value instanceof JSONArray) {
					value = toList((JSONArray) value);
				}

				else if(value instanceof JSONObject) {
					value = toMap((JSONObject) value);
				}
					list.add(value);
			}
			return list;
		}
		
		private void writeStringToFile(String str, String file) {
			try {
				Files.write(Paths.get(file), str.getBytes());
			}
			catch(Exception e) {
				System.out.println("Could not write to file. =(");
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SmsParser();

	}
}
