package edu.arizona.kfs.fp.document.validation.impl;

import java.util.Map;

public class EntryNotFoundException extends Exception {

	private Map<String, String> criteria;
	
	public EntryNotFoundException(Map<String, String> criteria) {
		super("Cannot find matching GL Entry with given criteria: " + criteria);
		this.criteria = criteria;
	}
	
	public Map<String, String> getCriteria() {
		return criteria;
	}
	
	public void setCriteria(Map<String, String> criteria) {
		this.criteria = criteria;
	}
}
