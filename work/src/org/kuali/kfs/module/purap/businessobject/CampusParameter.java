/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/CampusParameter.java,v $
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

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Campus;

/**
 * 
 */
public class CampusParameter extends BusinessObjectBase {

	private String campusCode;
	private String campusPurchasingDirectorName;
	private String campusPurchasingDirectorTitle;
	private String campusAccountsPayableEmailAddress;

    private Campus campus;

	/**
	 * Default constructor.
	 */
	public CampusParameter() {

	}

	/**
	 * Gets the campusCode attribute.
	 * 
	 * @return Returns the campusCode
	 * 
	 */
	public String getCampusCode() { 
		return campusCode;
	}

	/**
	 * Sets the campusCode attribute.
	 * 
	 * @param campusCode The campusCode to set.
	 * 
	 */
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}


	/**
	 * Gets the campusPurchasingDirectorName attribute.
	 * 
	 * @return Returns the campusPurchasingDirectorName
	 * 
	 */
	public String getCampusPurchasingDirectorName() { 
		return campusPurchasingDirectorName;
	}

	/**
	 * Sets the campusPurchasingDirectorName attribute.
	 * 
	 * @param campusPurchasingDirectorName The campusPurchasingDirectorName to set.
	 * 
	 */
	public void setCampusPurchasingDirectorName(String campusPurchasingDirectorName) {
		this.campusPurchasingDirectorName = campusPurchasingDirectorName;
	}


	/**
	 * Gets the campusPurchasingDirectorTitle attribute.
	 * 
	 * @return Returns the campusPurchasingDirectorTitle
	 * 
	 */
	public String getCampusPurchasingDirectorTitle() { 
		return campusPurchasingDirectorTitle;
	}

	/**
	 * Sets the campusPurchasingDirectorTitle attribute.
	 * 
	 * @param campusPurchasingDirectorTitle The campusPurchasingDirectorTitle to set.
	 * 
	 */
	public void setCampusPurchasingDirectorTitle(String campusPurchasingDirectorTitle) {
		this.campusPurchasingDirectorTitle = campusPurchasingDirectorTitle;
	}


	/**
	 * Gets the campusAccountsPayableEmailAddress attribute.
	 * 
	 * @return Returns the campusAccountsPayableEmailAddress
	 * 
	 */
	public String getCampusAccountsPayableEmailAddress() { 
		return campusAccountsPayableEmailAddress;
	}

	/**
	 * Sets the campusAccountsPayableEmailAddress attribute.
	 * 
	 * @param campusAccountsPayableEmailAddress The campusAccountsPayableEmailAddress to set.
	 * 
	 */
	public void setCampusAccountsPayableEmailAddress(String campusAccountsPayableEmailAddress) {
		this.campusAccountsPayableEmailAddress = campusAccountsPayableEmailAddress;
	}


	/**
	 * Gets the campus attribute.
	 * 
	 * @return Returns the campus
	 * 
	 */
	public Campus getCampus() { 
		return campus;
	}

	/**
	 * Sets the campus attribute.
	 * 
	 * @param campus The campus to set.
	 * @deprecated
	 */
	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("campusCode", this.campusCode);
	    return m;
    }
}
