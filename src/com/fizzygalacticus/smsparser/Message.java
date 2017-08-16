package com.fizzygalacticus.smsparser;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class Message {
	public static final int TYPE_TO = 2;
	public static final int TYPE_FROM = 1;
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