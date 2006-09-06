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

package org.kuali.module.labor.bo;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class LedgerBalance extends BusinessObjectBase {

	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private String financialBalanceTypeCode;
	private String financialObjectTypeCode;
	private String positionNumber;
	private String emplid;
	private KualiDecimal accountLineAnnualBalanceAmount;
	private KualiDecimal financialBeginningBalanceLineAmount;
	private KualiDecimal beginningBalanceAccountAmount;
	private KualiDecimal month1AccountLineAmount;
	private KualiDecimal month2AccountLineAmount;
	private KualiDecimal month3AccountLineAmount;
	private KualiDecimal month4AccountLineAmount;
	private KualiDecimal month5AccountLineAmount;
	private KualiDecimal month6AccountLineAmount;
	private KualiDecimal month7AccountLineAmount;
	private KualiDecimal month8AccountLineAmount;
	private KualiDecimal month9AccountLineAmount;
	private KualiDecimal month10AccountLineAmount;
	private KualiDecimal month11AccountLineAmount;
	private KualiDecimal month12AccountLineAmount;
	private KualiDecimal month13AccountLineAmount;
	private Timestamp transactionDateTimeStamp;

    private ObjectCode financialObject;
	private Account account;
	private Chart chartOfAccounts;
    private SubAccount subAccount;
    private SubObjCd financialSubObject;    
    private ObjectType financialObjectType;
    private BalanceTyp balanceType;    
    
	/**
	 * Default constructor.
	 */
	public LedgerBalance() {

	}

	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return - Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param - universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return - Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param - chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the accountNumber attribute.
	 * 
	 * @return - Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param - accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	/**
	 * Gets the subAccountNumber attribute.
	 * 
	 * @return - Returns the subAccountNumber
	 * 
	 */
	public String getSubAccountNumber() { 
		return subAccountNumber;
	}

	/**
	 * Sets the subAccountNumber attribute.
	 * 
	 * @param - subAccountNumber The subAccountNumber to set.
	 * 
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
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
	 * Gets the financialSubObjectCode attribute.
	 * 
	 * @return - Returns the financialSubObjectCode
	 * 
	 */
	public String getFinancialSubObjectCode() { 
		return financialSubObjectCode;
	}

	/**
	 * Sets the financialSubObjectCode attribute.
	 * 
	 * @param - financialSubObjectCode The financialSubObjectCode to set.
	 * 
	 */
	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}


	/**
	 * Gets the financialBalanceTypeCode attribute.
	 * 
	 * @return - Returns the financialBalanceTypeCode
	 * 
	 */
	public String getFinancialBalanceTypeCode() { 
		return financialBalanceTypeCode;
	}

	/**
	 * Sets the financialBalanceTypeCode attribute.
	 * 
	 * @param - financialBalanceTypeCode The financialBalanceTypeCode to set.
	 * 
	 */
	public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
		this.financialBalanceTypeCode = financialBalanceTypeCode;
	}


	/**
	 * Gets the financialObjectTypeCode attribute.
	 * 
	 * @return - Returns the financialObjectTypeCode
	 * 
	 */
	public String getFinancialObjectTypeCode() { 
		return financialObjectTypeCode;
	}

	/**
	 * Sets the financialObjectTypeCode attribute.
	 * 
	 * @param - financialObjectTypeCode The financialObjectTypeCode to set.
	 * 
	 */
	public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
		this.financialObjectTypeCode = financialObjectTypeCode;
	}


	/**
	 * Gets the positionNumber attribute.
	 * 
	 * @return - Returns the positionNumber
	 * 
	 */
	public String getPositionNumber() { 
		return positionNumber;
	}

	/**
	 * Sets the positionNumber attribute.
	 * 
	 * @param - positionNumber The positionNumber to set.
	 * 
	 */
	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}


	/**
	 * Gets the emplid attribute.
	 * 
	 * @return - Returns the emplid
	 * 
	 */
	public String getEmplid() { 
		return emplid;
	}

	/**
	 * Sets the emplid attribute.
	 * 
	 * @param - emplid The emplid to set.
	 * 
	 */
	public void setEmplid(String emplid) {
		this.emplid = emplid;
	}


	/**
	 * Gets the accountLineAnnualBalanceAmount attribute.
	 * 
	 * @return - Returns the accountLineAnnualBalanceAmount
	 * 
	 */
	public KualiDecimal getAccountLineAnnualBalanceAmount() { 
		return accountLineAnnualBalanceAmount;
	}

	/**
	 * Sets the accountLineAnnualBalanceAmount attribute.
	 * 
	 * @param - accountLineAnnualBalanceAmount The accountLineAnnualBalanceAmount to set.
	 * 
	 */
	public void setAccountLineAnnualBalanceAmount(KualiDecimal accountLineAnnualBalanceAmount) {
		this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
	}


	/**
	 * Gets the financialBeginningBalanceLineAmount attribute.
	 * 
	 * @return - Returns the financialBeginningBalanceLineAmount
	 * 
	 */
	public KualiDecimal getFinancialBeginningBalanceLineAmount() { 
		return financialBeginningBalanceLineAmount;
	}

	/**
	 * Sets the financialBeginningBalanceLineAmount attribute.
	 * 
	 * @param - financialBeginningBalanceLineAmount The financialBeginningBalanceLineAmount to set.
	 * 
	 */
	public void setFinancialBeginningBalanceLineAmount(KualiDecimal financialBeginningBalanceLineAmount) {
		this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
	}


	/**
	 * Gets the beginningBalanceAccountAmount attribute.
	 * 
	 * @return - Returns the beginningBalanceAccountAmount
	 * 
	 */
	public KualiDecimal getBeginningBalanceAccountAmount() { 
		return beginningBalanceAccountAmount;
	}

	/**
	 * Sets the beginningBalanceAccountAmount attribute.
	 * 
	 * @param - beginningBalanceAccountAmount The beginningBalanceAccountAmount to set.
	 * 
	 */
	public void setBeginningBalanceAccountAmount(KualiDecimal beginningBalanceAccountAmount) {
		this.beginningBalanceAccountAmount = beginningBalanceAccountAmount;
	}


	/**
	 * Gets the month1AccountLineAmount attribute.
	 * 
	 * @return - Returns the month1AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth1AccountLineAmount() { 
		return month1AccountLineAmount;
	}

	/**
	 * Sets the month1AccountLineAmount attribute.
	 * 
	 * @param - month1AccountLineAmount The month1AccountLineAmount to set.
	 * 
	 */
	public void setMonth1AccountLineAmount(KualiDecimal month1AccountLineAmount) {
		this.month1AccountLineAmount = month1AccountLineAmount;
	}


	/**
	 * Gets the month2AccountLineAmount attribute.
	 * 
	 * @return - Returns the month2AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth2AccountLineAmount() { 
		return month2AccountLineAmount;
	}

	/**
	 * Sets the month2AccountLineAmount attribute.
	 * 
	 * @param - month2AccountLineAmount The month2AccountLineAmount to set.
	 * 
	 */
	public void setMonth2AccountLineAmount(KualiDecimal month2AccountLineAmount) {
		this.month2AccountLineAmount = month2AccountLineAmount;
	}


	/**
	 * Gets the month3AccountLineAmount attribute.
	 * 
	 * @return - Returns the month3AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth3AccountLineAmount() { 
		return month3AccountLineAmount;
	}

	/**
	 * Sets the month3AccountLineAmount attribute.
	 * 
	 * @param - month3AccountLineAmount The month3AccountLineAmount to set.
	 * 
	 */
	public void setMonth3AccountLineAmount(KualiDecimal month3AccountLineAmount) {
		this.month3AccountLineAmount = month3AccountLineAmount;
	}


	/**
	 * Gets the month4AccountLineAmount attribute.
	 * 
	 * @return - Returns the month4AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth4AccountLineAmount() { 
		return month4AccountLineAmount;
	}

	/**
	 * Sets the month4AccountLineAmount attribute.
	 * 
	 * @param - month4AccountLineAmount The month4AccountLineAmount to set.
	 * 
	 */
	public void setMonth4AccountLineAmount(KualiDecimal month4AccountLineAmount) {
		this.month4AccountLineAmount = month4AccountLineAmount;
	}


	/**
	 * Gets the month5AccountLineAmount attribute.
	 * 
	 * @return - Returns the month5AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth5AccountLineAmount() { 
		return month5AccountLineAmount;
	}

	/**
	 * Sets the month5AccountLineAmount attribute.
	 * 
	 * @param - month5AccountLineAmount The month5AccountLineAmount to set.
	 * 
	 */
	public void setMonth5AccountLineAmount(KualiDecimal month5AccountLineAmount) {
		this.month5AccountLineAmount = month5AccountLineAmount;
	}


	/**
	 * Gets the month6AccountLineAmount attribute.
	 * 
	 * @return - Returns the month6AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth6AccountLineAmount() { 
		return month6AccountLineAmount;
	}

	/**
	 * Sets the month6AccountLineAmount attribute.
	 * 
	 * @param - month6AccountLineAmount The month6AccountLineAmount to set.
	 * 
	 */
	public void setMonth6AccountLineAmount(KualiDecimal month6AccountLineAmount) {
		this.month6AccountLineAmount = month6AccountLineAmount;
	}


	/**
	 * Gets the month7AccountLineAmount attribute.
	 * 
	 * @return - Returns the month7AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth7AccountLineAmount() { 
		return month7AccountLineAmount;
	}

	/**
	 * Sets the month7AccountLineAmount attribute.
	 * 
	 * @param - month7AccountLineAmount The month7AccountLineAmount to set.
	 * 
	 */
	public void setMonth7AccountLineAmount(KualiDecimal month7AccountLineAmount) {
		this.month7AccountLineAmount = month7AccountLineAmount;
	}


	/**
	 * Gets the month8AccountLineAmount attribute.
	 * 
	 * @return - Returns the month8AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth8AccountLineAmount() { 
		return month8AccountLineAmount;
	}

	/**
	 * Sets the month8AccountLineAmount attribute.
	 * 
	 * @param - month8AccountLineAmount The month8AccountLineAmount to set.
	 * 
	 */
	public void setMonth8AccountLineAmount(KualiDecimal month8AccountLineAmount) {
		this.month8AccountLineAmount = month8AccountLineAmount;
	}


	/**
	 * Gets the month9AccountLineAmount attribute.
	 * 
	 * @return - Returns the month9AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth9AccountLineAmount() { 
		return month9AccountLineAmount;
	}

	/**
	 * Sets the month9AccountLineAmount attribute.
	 * 
	 * @param - month9AccountLineAmount The month9AccountLineAmount to set.
	 * 
	 */
	public void setMonth9AccountLineAmount(KualiDecimal month9AccountLineAmount) {
		this.month9AccountLineAmount = month9AccountLineAmount;
	}


	/**
	 * Gets the month10AccountLineAmount attribute.
	 * 
	 * @return - Returns the month10AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth10AccountLineAmount() { 
		return month10AccountLineAmount;
	}

	/**
	 * Sets the month10AccountLineAmount attribute.
	 * 
	 * @param - month10AccountLineAmount The month10AccountLineAmount to set.
	 * 
	 */
	public void setMonth10AccountLineAmount(KualiDecimal month10AccountLineAmount) {
		this.month10AccountLineAmount = month10AccountLineAmount;
	}


	/**
	 * Gets the month11AccountLineAmount attribute.
	 * 
	 * @return - Returns the month11AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth11AccountLineAmount() { 
		return month11AccountLineAmount;
	}

	/**
	 * Sets the month11AccountLineAmount attribute.
	 * 
	 * @param - month11AccountLineAmount The month11AccountLineAmount to set.
	 * 
	 */
	public void setMonth11AccountLineAmount(KualiDecimal month11AccountLineAmount) {
		this.month11AccountLineAmount = month11AccountLineAmount;
	}


	/**
	 * Gets the month12AccountLineAmount attribute.
	 * 
	 * @return - Returns the month12AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth12AccountLineAmount() { 
		return month12AccountLineAmount;
	}

	/**
	 * Sets the month12AccountLineAmount attribute.
	 * 
	 * @param - month12AccountLineAmount The month12AccountLineAmount to set.
	 * 
	 */
	public void setMonth12AccountLineAmount(KualiDecimal month12AccountLineAmount) {
		this.month12AccountLineAmount = month12AccountLineAmount;
	}


	/**
	 * Gets the month13AccountLineAmount attribute.
	 * 
	 * @return - Returns the month13AccountLineAmount
	 * 
	 */
	public KualiDecimal getMonth13AccountLineAmount() { 
		return month13AccountLineAmount;
	}

	/**
	 * Sets the month13AccountLineAmount attribute.
	 * 
	 * @param - month13AccountLineAmount The month13AccountLineAmount to set.
	 * 
	 */
	public void setMonth13AccountLineAmount(KualiDecimal month13AccountLineAmount) {
		this.month13AccountLineAmount = month13AccountLineAmount;
	}


	/**
	 * Gets the transactionDateTimeStamp attribute.
	 * 
	 * @return - Returns the transactionDateTimeStamp
	 * 
	 */
	public Timestamp getTransactionDateTimeStamp() { 
		return transactionDateTimeStamp;
	}

	/**
	 * Sets the transactionDateTimeStamp attribute.
	 * 
	 * @param - transactionDateTimeStamp The transactionDateTimeStamp to set.
	 * 
	 */
	public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
		this.transactionDateTimeStamp = transactionDateTimeStamp;
	}


	/**
	 * Gets the financialObject attribute.
	 * 
	 * @return - Returns the financialObject
	 * 
	 */
	public ObjectCode getFinancialObject() { 
		return financialObject;
	}

	/**
	 * Sets the financialObject attribute.
	 * 
	 * @param - financialObject The financialObject to set.
	 * @deprecated
	 */
	public void setFinancialObject(ObjectCode financialObject) {
		this.financialObject = financialObject;
	}

	/**
	 * Gets the account attribute.
	 * 
	 * @return - Returns the account
	 * 
	 */
	public Account getAccount() { 
		return account;
	}

	/**
	 * Sets the account attribute.
	 * 
	 * @param - account The account to set.
	 * @deprecated
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return - Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param - chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

    /**
     * Gets the balanceType attribute. 
     * @return Returns the balanceType.
     */
    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceType attribute value.
     * @param balanceType The balanceType to set.
     * @deprecated
     */
    public void setBalanceType(BalanceTyp balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the financialObjectType attribute. 
     * @return Returns the financialObjectType.
     */
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType attribute value.
     * @param financialObjectType The financialObjectType to set.
     * @deprecated
     */
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    /**
     * Gets the financialSubObject attribute. 
     * @return Returns the financialSubObject.
     */
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * @param financialSubObject The financialSubObject to set.
     * @deprecated
     */
    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the subAccount attribute. 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("financialBalanceTypeCode", this.financialBalanceTypeCode);
        m.put("financialObjectTypeCode", this.financialObjectTypeCode);
        m.put("positionNumber", this.positionNumber);
        m.put("emplid", this.emplid);
        return m;
    }

}
