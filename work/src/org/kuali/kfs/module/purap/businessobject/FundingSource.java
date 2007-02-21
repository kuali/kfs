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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class FundingSource extends PersistableBusinessObjectBase {

	private String fundingSourceCode;
	private String fundingSourceDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public FundingSource() {

	}

	/**
	 * Gets the fundingSourceCode attribute.
	 * 
	 * @return Returns the fundingSourceCode
	 * 
	 */
	public String getFundingSourceCode() { 
		return fundingSourceCode;
	}

	/**
	 * Sets the fundingSourceCode attribute.
	 * 
	 * @param fundingSourceCode The fundingSourceCode to set.
	 * 
	 */
	public void setFundingSourceCode(String fundingSourceCode) {
		this.fundingSourceCode = fundingSourceCode;
	}


	/**
	 * Gets the fundingSourceDescription attribute.
	 * 
	 * @return Returns the fundingSourceDescription
	 * 
	 */
	public String getFundingSourceDescription() { 
		return fundingSourceDescription;
	}

	/**
	 * Sets the fundingSourceDescription attribute.
	 * 
	 * @param fundingSourceDescription The fundingSourceDescription to set.
	 * 
	 */
	public void setFundingSourceDescription(String fundingSourceDescription) {
		this.fundingSourceDescription = fundingSourceDescription;
	}

	/**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("fundingSourceCode", this.fundingSourceCode);
	    return m;
    }
}
