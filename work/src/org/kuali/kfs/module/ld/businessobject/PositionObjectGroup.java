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

package org.kuali.module.labor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PositionObjectGroup extends BusinessObjectBase {

	private String positionObjectGroupCode;
	private String positionObjectGroupName;
	private boolean rowActiveIndicator;

	/**
	 * Default constructor.
	 */
	public PositionObjectGroup() {

	}

	/**
	 * Gets the positionObjectGroupCode attribute.
	 * 
	 * @return - Returns the positionObjectGroupCode
	 * 
	 */
	public String getPositionObjectGroupCode() { 
		return positionObjectGroupCode;
	}

	/**
	 * Sets the positionObjectGroupCode attribute.
	 * 
	 * @param - positionObjectGroupCode The positionObjectGroupCode to set.
	 * 
	 */
	public void setPositionObjectGroupCode(String positionObjectGroupCode) {
		this.positionObjectGroupCode = positionObjectGroupCode;
	}


	/**
	 * Gets the positionObjectGroupName attribute.
	 * 
	 * @return - Returns the positionObjectGroupName
	 * 
	 */
	public String getPositionObjectGroupName() { 
		return positionObjectGroupName;
	}

	/**
	 * Sets the positionObjectGroupName attribute.
	 * 
	 * @param - positionObjectGroupName The positionObjectGroupName to set.
	 * 
	 */
	public void setPositionObjectGroupName(String positionObjectGroupName) {
		this.positionObjectGroupName = positionObjectGroupName;
	}


	/**
	 * Gets the rowActiveIndicator attribute.
	 * 
	 * @return - Returns the rowActiveIndicator
	 * 
	 */
	public boolean isRowActiveIndicator() { 
		return rowActiveIndicator;
	}
	

	/**
	 * Sets the rowActiveIndicator attribute.
	 * 
	 * @param - rowActiveIndicator The rowActiveIndicator to set.
	 * 
	 */
	public void setRowActiveIndicator(boolean rowActiveIndicator) {
		this.rowActiveIndicator = rowActiveIndicator;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("positionObjectGroupCode", this.positionObjectGroupCode);
	    return m;
    }
}
