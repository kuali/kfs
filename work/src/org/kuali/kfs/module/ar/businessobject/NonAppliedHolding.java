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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class NonAppliedHolding extends PersistableBusinessObjectBase {

	private String referenceFinancialDocumentNumber;
	private KualiDecimal financialDocumentLineAmount = KualiDecimal.ZERO;
	private String customerNumber;
	private Customer customer;
    private KualiDecimal availableUnappliedAmount = KualiDecimal.ZERO;
    private KualiDecimal appliedUnappliedAmount = KualiDecimal.ZERO;
    private Collection<NonInvoicedDistribution> nonInvoicedDistributions;
    private Collection<NonAppliedDistribution> nonAppliedDistributions;
    private FinancialSystemDocumentHeader documentHeader;

	/**
	 * Default constructor.
	 */
	public NonAppliedHolding() {
        nonAppliedDistributions = new ArrayList<NonAppliedDistribution>();
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
	 * Gets the customerNumber attribute.
	 * 
	 * @return Returns the customerNumber
	 * 
	 */
	public String getCustomerNumber() { 
		return customerNumber;
	}

	/**
	 * Sets the customerNumber attribute.
	 * 
	 * @param customerNumber The customerNumber to set.
	 * 
	 */
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}


	/**
	 * Gets the customer attribute.
	 * 
	 * @return Returns the customer
	 * 
	 */
	public Customer getCustomer() { 
		return customer;
	}

	/**
	 * Sets the customer attribute.
	 * 
	 * @param customer The customer to set.
	 * @deprecated
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
    
    /**
     * Gets the nonInvoicedDistributions attribute. 
     * @return Returns the nonInvoicedDistributions.
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributions() {
        return nonInvoicedDistributions;
    }

    /**
     * Sets the nonInvoicedDistributions attribute value.
     * @param nonInvoicedDistributions The nonInvoicedDistributions to set.
     */
    public void setNonInvoicedDistributions(Collection<NonInvoicedDistribution> nonInvoicedDistributions) {
        this.nonInvoicedDistributions = nonInvoicedDistributions;
    }

	/**
     * Gets the nonAppliedDistributions attribute. 
     * @return Returns the nonAppliedDistributions.
     */
    public Collection<NonAppliedDistribution> getNonAppliedDistributions() {
        return nonAppliedDistributions;
    }

    /**
     * Sets the nonAppliedDistributions attribute value.
     * @param nonAppliedDistributions The nonAppliedDistributions to set.
     */
    public void setNonAppliedDistributions(List<NonAppliedDistribution> nonAppliedDistributions) {
        this.nonAppliedDistributions = nonAppliedDistributions;
    }

    /**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("referenceFinancialDocumentNumber", this.referenceFinancialDocumentNumber);
	    return m;
    }

    /**
     * Gets the availableUnappliedAmount attribute.
     * @return Returns the availableUnappliedAmount.
     */
    public KualiDecimal getAvailableUnappliedAmount() {
        //  start with the original unapplied amount
        KualiDecimal amount = financialDocumentLineAmount;
        
        //  subtract any non-invoiced distributions made against it
        for (NonInvoicedDistribution nonInvoicedDistribution : nonInvoicedDistributions) {
            amount = amount.subtract(nonInvoicedDistribution.getFinancialDocumentLineAmount());
        }
        
        //  subtract any non-applied distributions made against it
        for (NonAppliedDistribution nonAppliedDistribution : nonAppliedDistributions) {
            amount = amount.subtract(nonAppliedDistribution.getFinancialDocumentLineAmount());
        }
        return amount;
    }

    /**
     * Gets the appliedUnappliedAmount attribute.
     * @return Returns the appliedUnappliedAmount.
     */
    public KualiDecimal getAppliedUnappliedAmount() {
        //  start with zero
        KualiDecimal amount = KualiDecimal.ZERO;
        
        //  add any non-invoiced distributions made against it
        for (NonInvoicedDistribution nonInvoicedDistribution : nonInvoicedDistributions) {
            amount = amount.add(nonInvoicedDistribution.getFinancialDocumentLineAmount());
        }
        
        //  add any non-applied distributions made against it
        for (NonAppliedDistribution nonAppliedDistribution : nonAppliedDistributions) {
            amount = amount.add(nonAppliedDistribution.getFinancialDocumentLineAmount());
        }
        return amount;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader.
     */
    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader.
     */
    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

}
