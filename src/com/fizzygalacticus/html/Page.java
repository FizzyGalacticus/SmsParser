package com.fizzygalacticus.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Page {
	private Element head = new Element("head");
	private Element body = new Element("body");
	private ArrayList<Link> css = new ArrayList<Link>();
	private ArrayList<Element> scripts = new ArrayList<Element>();

	public Page() {
		this("Untitled Page");
	}
	
	public Page(String title) {
		Element titleElem = new Element("title");
		titleElem.addChild(title);
		this.head.addChild(titleElem);
	}
	
	public Page(String title, Object body) {
		this(title);
		
		this.body.addChild(body);
	}
	
	public void addHeaderElement(Element elem) {
		this.head.addChild(elem);
	}
	
	public void addBodyObject(Object obj) {
		this.body.addChild(obj);
	}
	
	public void addCss(String href) {
		Link link = new Link();
		link.addAttribute("href", href);
		link.addAttribute("rel", "stylesheet");
		link.addAttribute("type", "text/css");
		this.addCss(link);
	}
	
	public void addCss(Link link) {
		this.css.add(link);
	}
	
	public ArrayList<Link> getCss() {
		return this.css;
	}
	
	public void setCss(ArrayList<Link> css) {
		this.css = css;
	}
	
	public void addScript(String src) {
		Element script = new Element("script");
		script.addAttribute("src", src);
		script.addAttribute("type", "text/javascript");
		this.addScript(script);
	}
	
	public void addScript(Element script) {
		this.scripts.add(script);
	}
	
	public ArrayList<Element> getScripts() {
		return this.scripts;
	}
	
	public void setScripts(ArrayList<Element> scripts) {
		this.scripts = scripts;
	}
	
	public String toString() {
		String ret = "";
		
		Element htmlElem = new Element("html");
		
		for(Element cssElem : this.css)
			this.head.addChild(cssElem);
		
		htmlElem.addChild(this.head);
		htmlElem.addChild(this.body);
		
		for(Element script : this.scripts)
			htmlElem.addChild(script);
		
		ret += htmlElem;
		
		return ret;
	}
}
