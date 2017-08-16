package com.fizzygalacticus.html;

public class Link extends Element {

	public Link() {
		super("link");
	}

	public String toString() {
		String base = super.toString();
		base = base.substring(0, base.indexOf('>'));
		base += "/>";
		return base;
	}
}
