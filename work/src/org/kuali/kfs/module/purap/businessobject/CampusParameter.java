/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Campus;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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
	 * @return - Returns the campusCode
	 * 
	 */
	public String getCampusCode() { 
		return campusCode;
	}

	/**
	 * Sets the campusCode attribute.
	 * 
	 * @param - campusCode The campusCode to set.
	 * 
	 */
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}


	/**
	 * Gets the campusPurchasingDirectorName attribute.
	 * 
	 * @return - Returns the campusPurchasingDirectorName
	 * 
	 */
	public String getCampusPurchasingDirectorName() { 
		return campusPurchasingDirectorName;
	}

	/**
	 * Sets the campusPurchasingDirectorName attribute.
	 * 
	 * @param - campusPurchasingDirectorName The campusPurchasingDirectorName to set.
	 * 
	 */
	public void setCampusPurchasingDirectorName(String campusPurchasingDirectorName) {
		this.campusPurchasingDirectorName = campusPurchasingDirectorName;
	}


	/**
	 * Gets the campusPurchasingDirectorTitle attribute.
	 * 
	 * @return - Returns the campusPurchasingDirectorTitle
	 * 
	 */
	public String getCampusPurchasingDirectorTitle() { 
		return campusPurchasingDirectorTitle;
	}

	/**
	 * Sets the campusPurchasingDirectorTitle attribute.
	 * 
	 * @param - campusPurchasingDirectorTitle The campusPurchasingDirectorTitle to set.
	 * 
	 */
	public void setCampusPurchasingDirectorTitle(String campusPurchasingDirectorTitle) {
		this.campusPurchasingDirectorTitle = campusPurchasingDirectorTitle;
	}


	/**
	 * Gets the campusAccountsPayableEmailAddress attribute.
	 * 
	 * @return - Returns the campusAccountsPayableEmailAddress
	 * 
	 */
	public String getCampusAccountsPayableEmailAddress() { 
		return campusAccountsPayableEmailAddress;
	}

	/**
	 * Sets the campusAccountsPayableEmailAddress attribute.
	 * 
	 * @param - campusAccountsPayableEmailAddress The campusAccountsPayableEmailAddress to set.
	 * 
	 */
	public void setCampusAccountsPayableEmailAddress(String campusAccountsPayableEmailAddress) {
		this.campusAccountsPayableEmailAddress = campusAccountsPayableEmailAddress;
	}


	/**
	 * Gets the campus attribute.
	 * 
	 * @return - Returns the campus
	 * 
	 */
	public Campus getCampus() { 
		return campus;
	}

	/**
	 * Sets the campus attribute.
	 * 
	 * @param - campus The campus to set.
	 * @deprecated
	 */
	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("campusCode", this.campusCode);
	    return m;
    }
}
