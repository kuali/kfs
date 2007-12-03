/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.effort.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;

/**
 * Business Object for the Effort Certification Detail Reason Table.
 */
public class EffortCertificationDetailReason extends PersistableBusinessObjectBase {
	private String documentNumber;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String a21LaborEffortCategoryCode;
	private String a21LaborDetailLineReasonDescription;

    private Account account;
	private Chart chartOfAccounts;
    private DocumentHeader financialDocument;
    
	/**
	 * Default constructor.
	 */
	public EffortCertificationDetailReason() {

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
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the accountNumber attribute.
	 * 
	 * @return Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	/**
	 * Gets the a21LaborEffortCategoryCode attribute.
	 * 
	 * @return Returns the a21LaborEffortCategoryCode
	 * 
	 */
	public String getA21LaborEffortCategoryCode() { 
		return a21LaborEffortCategoryCode;
	}

	/**
	 * Sets the a21LaborEffortCategoryCode attribute.
	 * 
	 * @param a21LaborEffortCategoryCode The a21LaborEffortCategoryCode to set.
	 * 
	 */
	public void setA21LaborEffortCategoryCode(String a21LaborEffortCategoryCode) {
		this.a21LaborEffortCategoryCode = a21LaborEffortCategoryCode;
	}


	/**
	 * Gets the a21LaborDetailLineReasonDescription attribute.
	 * 
	 * @return Returns the a21LaborDetailLineReasonDescription
	 * 
	 */
	public String getA21LaborDetailLineReasonDescription() { 
		return a21LaborDetailLineReasonDescription;
	}

	/**
	 * Sets the a21LaborDetailLineReasonDescription attribute.
	 * 
	 * @param a21LaborDetailLineReasonDescription The a21LaborDetailLineReasonDescription to set.
	 * 
	 */
	public void setA21LaborDetailLineReasonDescription(String a21LaborDetailLineReasonDescription) {
		this.a21LaborDetailLineReasonDescription = a21LaborDetailLineReasonDescription;
	}


	/**
	 * Gets the account attribute.
	 * 
	 * @return Returns the account
	 * 
	 */
	public Account getAccount() { 
		return account;
	}

	/**
	 * Sets the account attribute.
	 * 
	 * @param account The account to set.
	 */
    @Deprecated
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param chartOfAccounts The chartOfAccounts to set.
	 */
    @Deprecated
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

    /**
     * Gets the financialDocument attribute. 
     * @return Returns the financialDocument.
     */
    public DocumentHeader getFinancialDocument() {
        return financialDocument;
    }

    /**
     * Sets the financialDocument attribute value.
     * @param financialDocument The financialDocument to set.
     */
    @Deprecated
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("a21LaborEffortCategoryCode", this.a21LaborEffortCategoryCode);
        return m;
    }

}
