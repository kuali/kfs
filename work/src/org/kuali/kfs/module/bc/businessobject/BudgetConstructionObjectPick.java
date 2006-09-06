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
import org.kuali.module.chart.bo.ObjectCode;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionObjectPick extends BusinessObjectBase {

	private String financialObjectCode;
	private Integer selectFlag;
	private Long personUniversalIdentifier;

	/**
	 * Default constructor.
	 */
	public BudgetConstructionObjectPick() {

	}

	/**
	 * Gets the financialObjectCode attribute.
	 * 
	 * @return - Returns the financialObjectCode
	 * 
	 */
	public String getFinancialObjectCode() { 
		return financialObjectCode;
	}

	/**
	 * Sets the financialObjectCode attribute.
	 * 
	 * @param - financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the selectFlag attribute.
	 * 
	 * @return - Returns the selectFlag
	 * 
	 */
	public Integer getSelectFlag() { 
		return selectFlag;
	}

	/**
	 * Sets the selectFlag attribute.
	 * 
	 * @param - selectFlag The selectFlag to set.
	 * 
	 */
	public void setSelectFlag(Integer selectFlag) {
		this.selectFlag = selectFlag;
	}


	/**
	 * Gets the personUniversalIdentifier attribute.
	 * 
	 * @return - Returns the personUniversalIdentifier
	 * 
	 */
	public Long getPersonUniversalIdentifier() { 
		return personUniversalIdentifier;
	}

	/**
	 * Sets the personUniversalIdentifier attribute.
	 * 
	 * @param - personUniversalIdentifier The personUniversalIdentifier to set.
	 * 
	 */
	public void setPersonUniversalIdentifier(Long personUniversalIdentifier) {
		this.personUniversalIdentifier = personUniversalIdentifier;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.personUniversalIdentifier != null) {
            m.put("personUniversalIdentifier", this.personUniversalIdentifier.toString());
        }
        m.put("financialObjectCode", this.financialObjectCode);
	    return m;
    }
}
