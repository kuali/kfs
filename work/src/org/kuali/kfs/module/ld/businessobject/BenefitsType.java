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
public class BenefitsType extends BusinessObjectBase {

	private String positionBenefitTypeCode;
	private String positionBenefitTypeDescription;
	private boolean positionBenefitRetirementIndicator;

	/**
	 * Default constructor.
	 */
	public BenefitsType() {

	}

	/**
	 * Gets the positionBenefitTypeCode attribute.
	 * 
	 * @return - Returns the positionBenefitTypeCode
	 * 
	 */
	public String getPositionBenefitTypeCode() { 
		return positionBenefitTypeCode;
	}

	/**
	 * Sets the positionBenefitTypeCode attribute.
	 * 
	 * @param - positionBenefitTypeCode The positionBenefitTypeCode to set.
	 * 
	 */
	public void setPositionBenefitTypeCode(String positionBenefitTypeCode) {
		this.positionBenefitTypeCode = positionBenefitTypeCode;
	}


	/**
	 * Gets the positionBenefitTypeDescription attribute.
	 * 
	 * @return - Returns the positionBenefitTypeDescription
	 * 
	 */
	public String getPositionBenefitTypeDescription() { 
		return positionBenefitTypeDescription;
	}

	/**
	 * Sets the positionBenefitTypeDescription attribute.
	 * 
	 * @param - positionBenefitTypeDescription The positionBenefitTypeDescription to set.
	 * 
	 */
	public void setPositionBenefitTypeDescription(String positionBenefitTypeDescription) {
		this.positionBenefitTypeDescription = positionBenefitTypeDescription;
	}


	/**
	 * Gets the positionBenefitRetirementIndicator attribute.
	 * 
	 * @return - Returns the positionBenefitRetirementIndicator
	 * 
	 */
	public boolean isPositionBenefitRetirementIndicator() { 
		return positionBenefitRetirementIndicator;
	}
	

	/**
	 * Sets the positionBenefitRetirementIndicator attribute.
	 * 
	 * @param - positionBenefitRetirementIndicator The positionBenefitRetirementIndicator to set.
	 * 
	 */
	public void setPositionBenefitRetirementIndicator(boolean positionBenefitRetirementIndicator) {
		this.positionBenefitRetirementIndicator = positionBenefitRetirementIndicator;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("positionBenefitTypeCode", this.positionBenefitTypeCode);
	    return m;
    }
}
