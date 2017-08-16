package com.fizzygalacticus.html;

public class Br extends Element {

	public Br() {
		super("br");
	}
	
	public String toString() {
		String base = super.toString();
		base = base.substring(0, base.indexOf('>'));
		base += " />";
		return base;
	}
}
