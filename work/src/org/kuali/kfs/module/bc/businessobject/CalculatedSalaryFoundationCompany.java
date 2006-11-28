/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/businessobject/CalculatedSalaryFoundationCompany.java,v $
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

package org.kuali.module.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class CalculatedSalaryFoundationCompany extends BusinessObjectBase {

	private String hrmsCompany;

	/**
	 * Default constructor.
	 */
	public CalculatedSalaryFoundationCompany() {

	}

	/**
	 * Gets the hrmsCompany attribute.
	 * 
	 * @return Returns the hrmsCompany
	 * 
	 */
	public String getHrmsCompany() { 
		return hrmsCompany;
	}

	/**
	 * Sets the hrmsCompany attribute.
	 * 
	 * @param hrmsCompany The hrmsCompany to set.
	 * 
	 */
	public void setHrmsCompany(String hrmsCompany) {
		this.hrmsCompany = hrmsCompany;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("hrmsCompany", this.hrmsCompany);
	    return m;
    }
}
