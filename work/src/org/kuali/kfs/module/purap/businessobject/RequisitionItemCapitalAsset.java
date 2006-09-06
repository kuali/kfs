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
public class RequisitionItemCapitalAsset extends BusinessObjectBase {

	private Integer requisitionItemCapitalAssetIdentifier;
	private Integer requisitionItemIdentifier;
	private Long capitalAssetNumber;

    private RequisitionItem requisitionItem;

	/**
	 * Default constructor.
	 */
	public RequisitionItemCapitalAsset() {

	}

	/**
	 * Gets the requisitionItemCapitalAssetIdentifier attribute.
	 * 
	 * @return - Returns the requisitionItemCapitalAssetIdentifier
	 * 
	 */
	public Integer getRequisitionItemCapitalAssetIdentifier() { 
		return requisitionItemCapitalAssetIdentifier;
	}

	/**
	 * Sets the requisitionItemCapitalAssetIdentifier attribute.
	 * 
	 * @param - requisitionItemCapitalAssetIdentifier The requisitionItemCapitalAssetIdentifier to set.
	 * 
	 */
	public void setRequisitionItemCapitalAssetIdentifier(Integer requisitionItemCapitalAssetIdentifier) {
		this.requisitionItemCapitalAssetIdentifier = requisitionItemCapitalAssetIdentifier;
	}


	/**
	 * Gets the requisitionItemIdentifier attribute.
	 * 
	 * @return - Returns the requisitionItemIdentifier
	 * 
	 */
	public Integer getRequisitionItemIdentifier() { 
		return requisitionItemIdentifier;
	}

	/**
	 * Sets the requisitionItemIdentifier attribute.
	 * 
	 * @param - requisitionItemIdentifier The requisitionItemIdentifier to set.
	 * 
	 */
	public void setRequisitionItemIdentifier(Integer requisitionItemIdentifier) {
		this.requisitionItemIdentifier = requisitionItemIdentifier;
	}

	/**
	 * Gets the capitalAssetNumber attribute.
	 * 
	 * @return - Returns the capitalAssetNumber
	 * 
	 */
	public Long getCapitalAssetNumber() { 
		return capitalAssetNumber;
	}

	/**
	 * Sets the capitalAssetNumber attribute.
	 * 
	 * @param - capitalAssetNumber The capitalAssetNumber to set.
	 * 
	 */
	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}


	/**
	 * Gets the requisitionItem attribute.
	 * 
	 * @return - Returns the requisitionItem
	 * 
	 */
	public RequisitionItem getRequisitionItem() { 
		return requisitionItem;
	}

	/**
	 * Sets the requisitionItem attribute.
	 * 
	 * @param - requisitionItem The requisitionItem to set.
	 * @deprecated
	 */
	public void setRequisitionItem(RequisitionItem requisitionItem) {
		this.requisitionItem = requisitionItem;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.requisitionItemCapitalAssetIdentifier != null) {
            m.put("requisitionItemCapitalAssetIdentifier", this.requisitionItemCapitalAssetIdentifier.toString());
        }
	    return m;
    }
}
