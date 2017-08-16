package com.fizzygalacticus.html;

public class BootstrapPage extends Page {
	private Element container = new Element();

	public BootstrapPage() {
		this("Untitled Bootstrap Page");
	}

	public BootstrapPage(String title) {
		this(title, null);
	}

	public BootstrapPage(String title, Object body) {
		super(title, body);
		
		this.addCss("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css");
		this.addScript("https://code.jquery.com/jquery-3.2.1.min.js");
		this.addScript("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js");
		
		this.container.addClass("container");
		
		super.addBodyObject(this.container);
	}
	
	public void addBodyObject(Object obj) {
		this.container.addChild(obj);
	}
	
	public static Element createRow() {
		Element row = new Element();
		row.addClass("row");
		return row;
	}
	
	public static Element createColumn(Integer size) {
		Element column = new Element();
		column.addClass("xs-col-" + size);
		column.addClass("sm-col-" + size);
		column.addClass("md-col-" + size);
		column.addClass("lg-col-" + size);
		column.addClass("xl-col-" + size);
		return column;
	}
}
