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

package org.kuali.module.gl.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * CollectorDetail Business Object.
 */
public class CollectorDetail extends PersistableBusinessObjectBase {

    private String universityFiscalPeriodCode;
    private Integer universityFiscalYear;
    private Date createDate;
    private String createSequence;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String collectorDetailSequenceNumber;
    private String financialDocumentTypeCode;
    private String financialSystemOriginationCode;
    private String documentNumber;
    private KualiDecimal collectorDetailItemAmount;
    private String collectorDetailNoteText;
    
    private ObjectCode financialObject;
    private Account account;
    private Chart chartOfAccounts;
    private ObjectType objectType;
    private BalanceTyp balanceType;

    /**
     * Default constructor.
     */
    public CollectorDetail() {
        setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
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
     * Gets the createDate attribute.
     * 
     * @return Returns the createDate
     * 
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets the createDate attribute.
     * 
     * @param createDate The createDate to set.
     * 
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    /**
     * Gets the createSequence attribute.
     * 
     * @return Returns the createSequence
     * 
     */
    public String getCreateSequence() {
        return createSequence;
    }

    /**
     * Sets the createSequence attribute.
     * 
     * @param createSequence The createSequence to set.
     * 
     */
    public void setCreateSequence(String createSequence) {
        this.createSequence = createSequence;
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
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     * 
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     * 
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     * 
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     * 
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     * 
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     * 
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the collectorDetailSequenceNumber attribute.
     * 
     * @return Returns the collectorDetailSequenceNumber
     * 
     */
    public String getCollectorDetailSequenceNumber() {
        return collectorDetailSequenceNumber;
    }

    /**
     * Sets the collectorDetailSequenceNumber attribute.
     * 
     * @param collectorDetailSequenceNumber The collectorDetailSequenceNumber to set.
     * 
     */
    public void setCollectorDetailSequenceNumber(String collectorDetailSequenceNumber) {
        this.collectorDetailSequenceNumber = collectorDetailSequenceNumber;
    }


    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
     * 
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     * 
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }


    /**
     * Gets the financialSystemOriginationCode attribute.
     * 
     * @return Returns the financialSystemOriginationCode
     * 
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Sets the financialSystemOriginationCode attribute.
     * 
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     * 
     */
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
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
     * Gets the collectorDetailItemAmount attribute.
     * 
     * @return Returns the collectorDetailItemAmount
     * 
     */
    public KualiDecimal getCollectorDetailItemAmount() {
        return collectorDetailItemAmount;
    }

    /**
     * Sets the collectorDetailItemAmount attribute.
     * 
     * @param collectorDetailItemAmount The collectorDetailItemAmount to set.
     * 
     */
    public void setCollectorDetailItemAmount(KualiDecimal collectorDetailItemAmount) {
        this.collectorDetailItemAmount = collectorDetailItemAmount;
    }

    public void setCollectorDetailItemAmount(String collectorDetailItemAmount) {
        this.collectorDetailItemAmount = new KualiDecimal(collectorDetailItemAmount);
    }
    
    public void clearcollectorDetailItemAmount() {
        this.collectorDetailItemAmount = null;
    }
    
    /**
     * Gets the collectorDetailNoteText attribute.
     * 
     * @return Returns the collectorDetailNoteText
     * 
     */
    public String getCollectorDetailNoteText() {
        return collectorDetailNoteText;
    }

    /**
     * Sets the collectorDetailNoteText attribute.
     * 
     * @param collectorDetailNoteText The collectorDetailNoteText to set.
     * 
     */
    public void setCollectorDetailNoteText(String collectorDetailNoteText) {
        this.collectorDetailNoteText = collectorDetailNoteText;
    }


    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject
     * 
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute.
     * 
     * @param financialObject The financialObject to set.
     * @deprecated
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
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
     * @deprecated
     */
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
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("universityFiscalPeriodCode", getUniversityFiscalPeriodCode());
        if (getUniversityFiscalYear() != null) {
            m.put("universityFiscalYear", getUniversityFiscalYear().toString());
        }
        if (getCreateDate() != null) {
            m.put("createDate", getCreateDate().toString());
        }
        m.put("createSequence", getCreateSequence());
        m.put("chartOfAccountsCode", getChartOfAccountsCode());
        m.put("accountNumber", getAccountNumber());
        m.put("subAccountNumber", getSubAccountNumber());
        m.put("financialObjectCode", getFinancialObjectCode());
        m.put("financialSubObjectCode", getFinancialSubObjectCode());
        m.put("collectorDetailSequenceNumber", getCollectorDetailSequenceNumber());
        m.put("financialDocumentTypeCode", getFinancialDocumentTypeCode());
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        return m;
    }

    /**
     * Gets the balanceTyp attribute. 
     * @return Returns the balanceTyp.
     */
    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceTyp attribute value.
     * @param balanceTyp The balanceTyp to set.
     */
    public void setBalanceType(BalanceTyp balanceTyp) {
        this.balanceType = balanceTyp;
    }

    /**
     * Gets the financialBalanceTypeCode attribute. 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute. 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the objectType attribute. 
     * @return Returns the objectType.
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * Sets the objectType attribute value.
     * @param objectType The objectType to set.
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }
}
