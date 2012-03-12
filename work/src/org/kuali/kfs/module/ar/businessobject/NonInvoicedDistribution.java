/*
 * Copyright 2007-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class NonInvoicedDistribution extends PersistableBusinessObjectBase {

	private String documentNumber; // ??? It's not the payment application document number
	private Integer financialDocumentLineNumber;
	private String referenceFinancialDocumentNumber;
	private KualiDecimal financialDocumentLineAmount;

    private NonAppliedHolding nonAppliedHolding;
    
	/**
	 * Default constructor.
	 */
	public NonInvoicedDistribution() {

	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the financialDocumentLineNumber attribute.
	 * 
	 * @return Returns the financialDocumentLineNumber
	 * 
	 */
	public Integer getFinancialDocumentLineNumber() { 
		return financialDocumentLineNumber;
	}

	/**
	 * Sets the financialDocumentLineNumber attribute.
	 * 
	 * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
	 * 
	 */
	public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
		this.financialDocumentLineNumber = financialDocumentLineNumber;
	}


	/**
	 * Gets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @return Returns the referenceFinancialDocumentNumber
	 * 
	 */
	public String getReferenceFinancialDocumentNumber() { 
		return referenceFinancialDocumentNumber;
	}

	/**
	 * Sets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
	 * 
	 */
	public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
		this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
	}


	/**
	 * Gets the financialDocumentLineAmount attribute.
	 * 
	 * @return Returns the financialDocumentLineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentLineAmount() { 
		return financialDocumentLineAmount;
	}

	/**
	 * Sets the financialDocumentLineAmount attribute.
	 * 
	 * @param financialDocumentLineAmount The financialDocumentLineAmount to set.
	 * 
	 */
	public void setFinancialDocumentLineAmount(KualiDecimal financialDocumentLineAmount) {
		this.financialDocumentLineAmount = financialDocumentLineAmount;
	}


	/**
	 * Gets the nonAppliedHolding attribute.
	 * 
	 * @return Returns the nonAppliedHolding
	 * 
	 */
	public NonAppliedHolding getNonAppliedHolding() { 
		return nonAppliedHolding;
	}

	/**
	 * Sets the nonAppliedHolding attribute.
	 * 
	 * @param nonAppliedHolding The nonAppliedHolding to set.
	 */
	public void setNonAppliedHolding(NonAppliedHolding nonAppliedHolding) {
		this.nonAppliedHolding = nonAppliedHolding;
	}

	/**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        m.put("referenceFinancialDocumentNumber", this.referenceFinancialDocumentNumber);
	    return m;
    }

}
