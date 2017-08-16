package com.fizzygalacticus.html;

import java.util.ArrayList;
import java.util.HashMap;

public class Element {
	protected String tagname = "div";
	protected HashMap<String, ArrayList<Object>> attributes = new HashMap<String, ArrayList<Object>>();
	protected ArrayList<Object> children = new ArrayList<Object>();

	public Element() {
		
	}
	
	public Element(String tagname) {
		this.tagname = tagname;
	}
	
	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	public void addAttribute(String key, Object value) {
		if(!this.attributes.containsKey(key))
			this.attributes.put(key, new ArrayList<Object>());
		
		this.attributes.get(key).add(value);
	}
	
	public void overwriteAttribute(String key, Object value) {
		if(this.attributes.containsKey(key))
			this.attributes.get(key).clear();

		this.addAttribute(key, value);
	}
	
	public void addClass(String cls) {
		this.addAttribute("class", cls);
	}
	
	public void removeClass(String cls) {
		if(this.attributes.containsKey("class")) {
			ArrayList<Object> classes = this.attributes.get("class");
			classes.remove(cls);
		}
	}
	
	public void setId(String id) {
		this.overwriteAttribute("id", id);
	}
	
	public void addChild(Object child) {
		if(child != null)
			this.children.add(child);
	}
	
	public void removeChildren() {
		this.children.clear();
	}
	
	public String toString() {
		String ret  = "";
		
		ret += "<" + this.tagname;
		
		for(String key : this.attributes.keySet()) {
			ret += " " + key + "=\"";
			ArrayList<Object> attrs = this.attributes.get(key);
			for(int i = 0; i < attrs.size(); i++) {
				ret += attrs.get(i);
				
				if(i < attrs.size()-1)
					ret += " ";
			}
			ret += "\"";
		}
		
		ret += ">";
		
		for(Object child : this.children)
			ret += child;
		
		ret += "</" + this.tagname + ">"; 
		
		return ret;
	}
}
