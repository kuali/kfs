/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Put any functions here which need to be included on every page to override
 * the Rice implementations.
 */

/**
 * Gets the value of an element with the given name. 
 * When the element is an input field, we can directly retrieve its value using the name as the element ID;
 * otherwise when it's readOnly, we need to add ".div" to the name as the element ID.
 * This function also filters out white spaces as well as any URL links that might be associated with the field.
 * @param name the given field name.
 * @return the value of the element
 */
function getElementValue(name) {
	var value = null;

	// retrieve element using name as ID
	var el = kualiElements[name];
	//alert("overriden getElementValue: kualiElements el = " + el);
	
	// if the element exists, then it's an input field, get its value directly
	if ( el ) {
		value = el.value.toUpperCase().trim();
		//alert("input el value = " + value);
	}
	
	// otherwise the el is readOnly, retrieve the value using name.div as ID 
	else {
		el = document.getElementById(name + ".div");
		//alert("getElementById el = " + el); 
		
		if (el) {
			value = dwr.util.getValue(name + ".div");
			//alert("DWR getValue = " + value);

			// trim html link if any
			value = value.replace(/(<([^>]+)>)/ig,"");
			//alert("After striping html, value = " + value);

			// trim &nbsp's and white spaces if any
			value = value.replace("&nbsp;", "").replace(/^\s+|\s+$/g,"");
			//alert("After striping spaces, value = " + value);		

			// value most likely already in upper case, but just in case
			value = value.toUpperCase();
			//alert("After upper case, value = " + value);		
		}
	}
	
	return value;
}

/** executes a function on all tabs.  The function will be passed a document & partial tab name. */
function doToAllTabs(func) {	
	// get the target node
	var targetElement = document.getElementsByTagName('body')[0];
	NodeIterator.iterate(func, targetElement);
	
	return false;
}

