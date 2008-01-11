package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.AccountingPeriod;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class NonAppliedDistribution extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer paidAppliedItemNumber;
	private String referenceFinancialDocumentNumber;
	private Integer universityFiscalYear;
	private String universityFiscalPeriodCode;
	private KualiDecimal financialDocumentLineAmount;

	private AccountingPeriod universityFiscalPeriod;

	/**
	 * Default constructor.
	 */
	public NonAppliedDistribution() {

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
	public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
		this.universityFiscalPeriod = universityFiscalPeriod;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        if (this.paidAppliedItemNumber != null) {
            m.put("paidAppliedItemNumber", this.paidAppliedItemNumber.toString());
        }
        m.put("referenceFinancialDocumentNumber", this.referenceFinancialDocumentNumber);
	    return m;
    }

}
