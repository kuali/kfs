/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * 
 */
public class RoutingFormKeyword extends PersistableBusinessObjectBase {

	private String routingFormKeywordDescription;
	private String documentNumber;

	/**
	 * Default constructor.
	 */
	public RoutingFormKeyword() {

	}
    
    /**
     * Constructs a RoutingFormKeyword.
     * @param documentNumber
     * @param keyword
     */
    public RoutingFormKeyword(String documentNumber, Keyword keyword) {
        this.documentNumber = documentNumber;
        this.routingFormKeywordDescription = keyword.getRoutingFormKeywordDescription();
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

	@Override
    public boolean equals(Object obj) {
        boolean equals = true;

        if (ObjectUtils.isNotNull(obj) && obj instanceof RoutingFormKeyword) {
            RoutingFormKeyword objCompare = (RoutingFormKeyword) obj;
            
            equals &= this.documentNumber.equals(objCompare.getDocumentNumber());
            equals &= this.routingFormKeywordDescription.equals(objCompare.getRoutingFormKeywordDescription());
        }
        
        return equals;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("routingFormKeywordDescription", this.routingFormKeywordDescription);
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
	    return m;
    }
    
    public boolean isEmpty() {
        return StringUtils.isBlank(documentNumber) && StringUtils.isBlank(routingFormKeywordDescription);
    }
}
