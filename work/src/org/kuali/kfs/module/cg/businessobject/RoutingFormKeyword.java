/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormKeyword.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class RoutingFormKeyword extends BusinessObjectBase {

	private String routingFormKeywordDescription;
	private String documentNumber;

	/**
	 * Default constructor.
	 */
	public RoutingFormKeyword() {

	}

	/**
	 * Gets the routingFormKeywordDescription attribute.
	 * 
	 * @return Returns the routingFormKeywordDescription
	 * 
	 */
	public String getRoutingFormKeywordDescription() { 
		return routingFormKeywordDescription;
	}

	/**
	 * Sets the routingFormKeywordDescription attribute.
	 * 
	 * @param routingFormKeywordDescription The routingFormKeywordDescription to set.
	 * 
	 */
	public void setRoutingFormKeywordDescription(String routingFormKeywordDescription) {
		this.routingFormKeywordDescription = routingFormKeywordDescription;
	}


	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("routingFormKeywordDescription", this.routingFormKeywordDescription);
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
	    return m;
    }
    
    public boolean isEmpty() {
        return StringUtils.isBlank(documentNumber) && StringUtils.isBlank(routingFormKeywordDescription);
    }
}
