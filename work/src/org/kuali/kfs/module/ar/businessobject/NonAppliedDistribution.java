/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class NonAppliedDistribution extends PersistableBusinessObjectBase {

	private String documentNumber; // document that generated this distribution
	private Integer paidAppliedItemNumber; // ???
	private String referenceFinancialDocumentNumber; // document that created the non-applied-holding that this is distributing
	private Integer universityFiscalYear; // ???
	private String universityFiscalPeriodCode; // ???
	private KualiDecimal financialDocumentLineAmount;

	private AccountingPeriod universityFiscalPeriod;

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
	 * Gets the paidAppliedItemNumber attribute.
	 *
	 * @return Returns the paidAppliedItemNumber
	 *
	 */
	public Integer getPaidAppliedItemNumber() {
		return paidAppliedItemNumber;
	}

	/**
	 * Sets the paidAppliedItemNumber attribute.
	 *
	 * @param paidAppliedItemNumber The paidAppliedItemNumber to set.
	 *
	 */
	public void setPaidAppliedItemNumber(Integer paidAppliedItemNumber) {
		this.paidAppliedItemNumber = paidAppliedItemNumber;
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
	 * Gets the universityFiscalYear attribute.
	 *
	 * @return Returns the universityFiscalYear
	 *
	 */
	public Integer getUniversityFiscalYear() {
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 *
	 * @param universityFiscalYear The universityFiscalYear to set.
	 *
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the universityFiscalPeriodCode attribute.
	 *
	 * @return Returns the universityFiscalPeriodCode
	 *
	 */
	public String getUniversityFiscalPeriodCode() {
		return universityFiscalPeriodCode;
	}

	/**
	 * Sets the universityFiscalPeriodCode attribute.
	 *
	 * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
	 *
	 */
	public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
		this.universityFiscalPeriodCode = universityFiscalPeriodCode;
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
	 * Gets the universityFiscalPeriod attribute.
	 *
	 * @return Returns the universityFiscalPeriod
	 *
	 */
	public AccountingPeriod getUniversityFiscalPeriod() {
		return universityFiscalPeriod;
	}

	/**
	 * Sets the universityFiscalPeriod attribute.
	 *
	 * @param universityFiscalPeriod The universityFiscalPeriod to set.
	 * @deprecated
	 */
	@Deprecated
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
		this.universityFiscalPeriod = universityFiscalPeriod;
	}

	/**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
    @SuppressWarnings("unchecked")
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.paidAppliedItemNumber != null) {
            m.put("paidAppliedItemNumber", this.paidAppliedItemNumber.toString());
        }
        m.put("referenceFinancialDocumentNumber", this.referenceFinancialDocumentNumber);
	    return m;
    }

}
