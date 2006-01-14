package org.kuali.module.financial.bo;

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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */

public class BankAccount extends BusinessObjectBase {

    private static final long serialVersionUID = -1056266362255635896L;
    private String finDocumentBankAccountDesc;
	private String finDocumentBankAccountNumber;
	private String financialDocumentBankCode ;
    private String chartOfAccountsCode;
    private String universityAcctChartOfAcctCd;
    private String universityAccountNumber;
    private String organizationCode;
	
	private Bank bank;
	private Org organization;
	private Account universityAccount;

	/**
     * Gets the organizationCode attribute. 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute value.
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the universityAccountNumber attribute. 
     * @return Returns the universityAccountNumber.
     */
    public String getUniversityAccountNumber() {
        return universityAccountNumber;
    }

    /**
     * Sets the universityAccountNumber attribute value.
     * @param universityAccountNumber The universityAccountNumber to set.
     */
    public void setUniversityAccountNumber(String universityAccountNumber) {
        this.universityAccountNumber = universityAccountNumber;
    }

    /**
     * Gets the universityAcctChartOfAcctCd attribute. 
     * @return Returns the universityAcctChartOfAcctCd.
     */
    public String getUniversityAcctChartOfAcctCd() {
        return universityAcctChartOfAcctCd;
    }

    /**
     * Sets the universityAcctChartOfAcctCd attribute value.
     * @param universityAcctChartOfAcctCd The universityAcctChartOfAcctCd to set.
     */
    public void setUniversityAcctChartOfAcctCd(String universityAcctChartOfAcctCd) {
        this.universityAcctChartOfAcctCd = universityAcctChartOfAcctCd;
    }

    /**
	 * Default no-arg constructor.
	 */
	public BankAccount() {

	}

	/**
	 * Gets the bank attribute.
	 *
	 * @return - Returns the financialDocumentBankCode
	 *
	 */
	public Bank getBank() {
		return bank;
	}

	/**
	 * Sets the bank attribute.
	 *
	 * @param - bank The bank to set.
	 * @deprecated
	 */
	public void setBank(Bank bank) {
		this.bank = bank;
	}

	/**
	 * Gets the financialDocumentBankCode attribute.
	 *
	 * @return - Returns the financialDocumentBankCode
	 *
	 */
	public String getFinancialDocumentBankCode() {
		return financialDocumentBankCode;
	}

	/**
	 * Sets the financialDocumentBankCode attribute.
	 *
	 * @param - financialDocumentBankCode The financialDocumentBankCode to set.
	 * 
	 */
	public void setFinancialDocumentBankCode(String financialDocumentBankCode) {
		this.financialDocumentBankCode = financialDocumentBankCode;
	}
	
	/**
	 * Gets the finDocumentBankAccountDesc attribute.
	 *
	 * @return - Returns the finDocumentBankAccountDesc
	 *
	 */
	public String getFinDocumentBankAccountDesc() {
		return finDocumentBankAccountDesc;
	}

	/**
	 * Sets the finDocumentBankAccountDesc attribute.
	 *
	 * @param - finDocumentBankAccountDesc The finDocumentBankAccountDesc to set.
	 *
	 */
	public void setFinDocumentBankAccountDesc(String finDocumentBankAccountDesc) {
		this.finDocumentBankAccountDesc = finDocumentBankAccountDesc;
	}

	/**
	 * Gets the organization attribute.
	 *
	 * @return - Returns the organization
	 *
	 */
	public Org getOrganization() {
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 *
	 * @param - organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Org organization) {
		this.organization = organization;
	}

	/**
	 * Gets the finDocumentBankAccount attribute.
	 *
	 * @return - Returns the finDocumentBankAccountNumber
	 *
	 */
	public String getFinDocumentBankAccountNumber() {
		return finDocumentBankAccountNumber;
	}

	/**
	 * Sets the finDocumentBankAccountNumber attribute.
	 *
	 * @param - finDocumentBankAccountNumber The finDocumentBankAccountNumber to set.
	 *
	 */
	public void setFinDocumentBankAccountNumber(String finDocumentBankAccountNumber) {
		this.finDocumentBankAccountNumber = finDocumentBankAccountNumber;
	}


	/**
	 * Gets the universityAccount attribute.
	 *
	 * @return - Returns the universityAccount
	 *
	 */
	public Account getUniversityAccount() {
		return universityAccount;
	}

	/**
	 * Sets the universityAccount attribute.
	 *
	 * @param - universityAccount The universityAccount to set.
	 * @deprecated
	 */
	public void setUniversityAccount(Account universityAccount) {
		this.universityAccount = universityAccount;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
			LinkedHashMap m = new LinkedHashMap();

			 m.put( "bankCode", getFinancialDocumentBankCode() );
			 m.put( "bankAccountNumber", getFinDocumentBankAccountNumber() );

			return m;
	}
}

