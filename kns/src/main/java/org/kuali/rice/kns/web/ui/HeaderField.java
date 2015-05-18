/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.ui;

import java.io.Serializable;

/**
 * 
 */
@Deprecated
public class HeaderField implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final HeaderField EMPTY_FIELD = new HeaderField();
    
    private String id;  // to be used as a unique identifier if needed
    private String ddAttributeEntryName;
    private String displayValue;
    private String nonLookupValue;
    private boolean lookupAware;
    
    public HeaderField() {
    }
    
    public HeaderField(String id, String ddAttributeEntryName, String displayValue, String nonLookupValue) {
    	this(ddAttributeEntryName, displayValue, nonLookupValue);
    	this.id = id;
    }
    
    public HeaderField(String ddAttributeEntryName, String displayValue, String nonLookupValue) {
        this.ddAttributeEntryName = ddAttributeEntryName;
        this.displayValue = displayValue;
        this.nonLookupValue = nonLookupValue;
        this.lookupAware = true;
    }
    
    public HeaderField(String ddAttributeEntryName, String displayValue) {
        this.ddAttributeEntryName = ddAttributeEntryName;
        this.displayValue = displayValue;
        this.lookupAware = false;
    }
    
    public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDdAttributeEntryName() {
        return this.ddAttributeEntryName;
    }
    
    public void setDdAttributeEntryName(String ddAttributeEntryName) {
        this.ddAttributeEntryName = ddAttributeEntryName;
    }
    
    public String getDisplayValue() {
        return this.displayValue;
    }
    
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getNonLookupValue() {
        return this.nonLookupValue;
    }
    
    public void setNonLookupValue(String nonLookupValue) {
        this.nonLookupValue = nonLookupValue;
    }
    
    public boolean isLookupAware() {
        return this.lookupAware;
    }
    
    public void setLookupAware(boolean lookupAware) {
        this.lookupAware = lookupAware;
    }
}
