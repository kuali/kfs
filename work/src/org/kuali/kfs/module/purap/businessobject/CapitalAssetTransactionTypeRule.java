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

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CapitalAssetTransactionTypeRule extends BusinessObjectBase {

	private String capitalAssetTransactionTypeCode;
	private String financialObjectSubTypeCode;
	private Integer capitalAssetRelationshipLimitNumber;

	/**
	 * Default constructor.
	 */
	public CapitalAssetTransactionTypeRule() {

	}

	/**
	 * Gets the capitalAssetTransactionTypeCode attribute.
	 * 
	 * @return - Returns the capitalAssetTransactionTypeCode
	 * 
	 */
	public String getCapitalAssetTransactionTypeCode() { 
		return capitalAssetTransactionTypeCode;
	}

	/**
	 * Sets the capitalAssetTransactionTypeCode attribute.
	 * 
	 * @param - capitalAssetTransactionTypeCode The capitalAssetTransactionTypeCode to set.
	 * 
	 */
	public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
		this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
	}


	/**
	 * Gets the financialObjectSubTypeCode attribute.
	 * 
	 * @return - Returns the financialObjectSubTypeCode
	 * 
	 */
	public String getFinancialObjectSubTypeCode() { 
		return financialObjectSubTypeCode;
	}

	/**
	 * Sets the financialObjectSubTypeCode attribute.
	 * 
	 * @param - financialObjectSubTypeCode The financialObjectSubTypeCode to set.
	 * 
	 */
	public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
		this.financialObjectSubTypeCode = financialObjectSubTypeCode;
	}


	/**
	 * Gets the capitalAssetRelationshipLimitNumber attribute.
	 * 
	 * @return - Returns the capitalAssetRelationshipLimitNumber
	 * 
	 */
	public Integer getCapitalAssetRelationshipLimitNumber() { 
		return capitalAssetRelationshipLimitNumber;
	}

	/**
	 * Sets the capitalAssetRelationshipLimitNumber attribute.
	 * 
	 * @param - capitalAssetRelationshipLimitNumber The capitalAssetRelationshipLimitNumber to set.
	 * 
	 */
	public void setCapitalAssetRelationshipLimitNumber(Integer capitalAssetRelationshipLimitNumber) {
		this.capitalAssetRelationshipLimitNumber = capitalAssetRelationshipLimitNumber;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("capitalAssetTransactionTypeCode", this.capitalAssetTransactionTypeCode);
        m.put("financialObjectSubTypeCode", this.financialObjectSubTypeCode);
	    return m;
    }
}
