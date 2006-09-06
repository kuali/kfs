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

package org.kuali.module.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionAdministrativePost extends BusinessObjectBase {

	private String emplid;
	private String positionNumber;
	private String administrativePost;

	/**
	 * Default constructor.
	 */
	public BudgetConstructionAdministrativePost() {

	}

	/**
	 * Gets the emplid attribute.
	 * 
	 * @return - Returns the emplid
	 * 
	 */
	public String getEmplid() { 
		return emplid;
	}

	/**
	 * Sets the emplid attribute.
	 * 
	 * @param - emplid The emplid to set.
	 * 
	 */
	public void setEmplid(String emplid) {
		this.emplid = emplid;
	}


	/**
	 * Gets the positionNumber attribute.
	 * 
	 * @return - Returns the positionNumber
	 * 
	 */
	public String getPositionNumber() { 
		return positionNumber;
	}

	/**
	 * Sets the positionNumber attribute.
	 * 
	 * @param - positionNumber The positionNumber to set.
	 * 
	 */
	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}


	/**
	 * Gets the administrativePost attribute.
	 * 
	 * @return - Returns the administrativePost
	 * 
	 */
	public String getAdministrativePost() { 
		return administrativePost;
	}

	/**
	 * Sets the administrativePost attribute.
	 * 
	 * @param - administrativePost The administrativePost to set.
	 * 
	 */
	public void setAdministrativePost(String administrativePost) {
		this.administrativePost = administrativePost;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("emplid", this.emplid);
        m.put("positionNumber", this.positionNumber);
	    return m;
    }
}
