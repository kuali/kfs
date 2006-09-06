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
public class PurchaseOrderQuoteList extends BusinessObjectBase {

	private Integer purchaseOrderQuoteListIdentifier;
	private String purchaseOrderQuoteListName;
	private Integer contractManagerCode;
    
    private ContractManager contractManager;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderQuoteList() {

	}

	/**
	 * Gets the purchaseOrderQuoteListIdentifier attribute.
	 * 
	 * @return - Returns the purchaseOrderQuoteListIdentifier
	 * 
	 */
	public Integer getPurchaseOrderQuoteListIdentifier() { 
		return purchaseOrderQuoteListIdentifier;
	}

	/**
	 * Sets the purchaseOrderQuoteListIdentifier attribute.
	 * 
	 * @param - purchaseOrderQuoteListIdentifier The purchaseOrderQuoteListIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderQuoteListIdentifier(Integer purchaseOrderQuoteListIdentifier) {
		this.purchaseOrderQuoteListIdentifier = purchaseOrderQuoteListIdentifier;
	}


	/**
	 * Gets the purchaseOrderQuoteListName attribute.
	 * 
	 * @return - Returns the purchaseOrderQuoteListName
	 * 
	 */
	public String getPurchaseOrderQuoteListName() { 
		return purchaseOrderQuoteListName;
	}

	/**
	 * Sets the purchaseOrderQuoteListName attribute.
	 * 
	 * @param - purchaseOrderQuoteListName The purchaseOrderQuoteListName to set.
	 * 
	 */
	public void setPurchaseOrderQuoteListName(String purchaseOrderQuoteListName) {
		this.purchaseOrderQuoteListName = purchaseOrderQuoteListName;
	}


	/**
	 * Gets the contractManagerCode attribute.
	 * 
	 * @return - Returns the contractManagerCode
	 * 
	 */
	public Integer getContractManagerCode() { 
		return contractManagerCode;
	}

	/**
	 * Sets the contractManagerCode attribute.
	 * 
	 * @param - contractManagerCode The contractManagerCode to set.
	 * 
	 */
	public void setContractManagerCode(Integer contractManagerCode) {
		this.contractManagerCode = contractManagerCode;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.purchaseOrderQuoteListIdentifier != null) {
            m.put("purchaseOrderQuoteListIdentifier", this.purchaseOrderQuoteListIdentifier.toString());
        }
	    return m;
    }

    /**
     * Gets the contractManager attribute. 
     * @return Returns the contractManager.
     */
    public ContractManager getContractManager() {
        return contractManager;
    }
}
