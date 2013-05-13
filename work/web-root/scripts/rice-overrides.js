/*
 * Copyright 2010-2012 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

