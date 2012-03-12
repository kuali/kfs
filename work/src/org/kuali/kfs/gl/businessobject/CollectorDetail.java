/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.gl.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.MessageMap;

/**
 * CollectorDetail Business Object.
 */
public class CollectorDetail extends PersistableBusinessObjectBase {

    private String universityFiscalPeriodCode;
    private Integer universityFiscalYear;
    private Date createDate;
    private Integer transactionLedgerEntrySequenceNumber;
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
    private String glCreditCode;
    
    private ObjectCode financialObject;
    private Account account;
    private Chart chartOfAccounts;
    private ObjectType objectType;
    private BalanceType balanceType;

    private static CollectorDetailFieldUtil collectorDetailFieldUtil;
    
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
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute.
     * 
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }


    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the createDate attribute.
     * 
     * @return Returns the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets the createDate attribute.
     * 
     * @param createDate The createDate to set.
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets the transactionLedgerEntrySequenceNumber attribute. 
     * @return Returns the transactionLedgerEntrySequenceNumber.
     */
    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    /**
     * Sets the transactionLedgerEntrySequenceNumber attribute value.
     * @param transactionLedgerEntrySequenceNumber The transactionLedgerEntrySequenceNumber to set.
     */
    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the collectorDetailSequenceNumber attribute.
     * 
     * @return Returns the collectorDetailSequenceNumber
     */
    public String getCollectorDetailSequenceNumber() {
        return collectorDetailSequenceNumber;
    }

    /**
     * Sets the collectorDetailSequenceNumber attribute.
     * 
     * @param collectorDetailSequenceNumber The collectorDetailSequenceNumber to set.
     */
    public void setCollectorDetailSequenceNumber(String collectorDetailSequenceNumber) {
        this.collectorDetailSequenceNumber = collectorDetailSequenceNumber;
    }


    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }


    /**
     * Gets the financialSystemOriginationCode attribute.
     * 
     * @return Returns the financialSystemOriginationCode
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Sets the financialSystemOriginationCode attribute.
     * 
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     */
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }


    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the collectorDetailItemAmount attribute.
     * 
     * @return Returns the collectorDetailItemAmount
     */
    public KualiDecimal getCollectorDetailItemAmount() {
        return collectorDetailItemAmount;
    }

    /**
     * Sets the collectorDetailItemAmount attribute.
     * 
     * @param collectorDetailItemAmount The collectorDetailItemAmount to set.
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
     */
    public String getCollectorDetailNoteText() {
        return collectorDetailNoteText;
    }

    /**
     * Sets the collectorDetailNoteText attribute.
     * 
     * @param collectorDetailNoteText The collectorDetailNoteText to set.
     */
    public void setCollectorDetailNoteText(String collectorDetailNoteText) {
        this.collectorDetailNoteText = collectorDetailNoteText;
    }


    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("universityFiscalPeriodCode", getUniversityFiscalPeriodCode());
        if (getUniversityFiscalYear() != null) {
            m.put("universityFiscalYear", getUniversityFiscalYear().toString());
        }
        if (getCreateDate() != null) {
            m.put("createDate", getCreateDate().toString());
        }
        m.put("transactionLedgerEntrySequenceNumber", getTransactionLedgerEntrySequenceNumber());
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
     * 
     * @return Returns the balanceTyp.
     */
    public BalanceType getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceTyp attribute value.
     * 
     * @param balanceTyp The balanceTyp to set.
     */
    public void setBalanceType(BalanceType balanceTyp) {
        this.balanceType = balanceTyp;
    }

    /**
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the glCreditCode attribute.
     * 
     * @return Returns the glCreditCode.
     */
    public String getGlCreditCode() {
        return glCreditCode;
    }

    /**
     * Sets the glCreditCode attribute value.
     * 
     * @param glCreditCode The glCreditCode to set.
     */
    public void setGLCreditCode(String glCreditCode) {
        this.glCreditCode = glCreditCode;
    }


    /**
     * Gets the objectType attribute.
     * 
     * @return Returns the objectType.
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * Sets the objectType attribute value.
     * 
     * @param objectType The objectType to set.
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }
    
    protected String getValue(String headerLine, int s, int e) {
        return org.springframework.util.StringUtils.trimTrailingWhitespace(StringUtils.substring(headerLine, s, e));
    }
        
    /**
     * @return the static instance of the CollectorBatchrFieldUtil
     */
    protected static CollectorDetailFieldUtil getCollectorDetailFieldUtil() {
        if (collectorDetailFieldUtil == null) {
            collectorDetailFieldUtil = new CollectorDetailFieldUtil();
        }
        return collectorDetailFieldUtil;
    }
    
    public void setFromFileForCollectorDetail(String detailLine, Map<String, String>accountRecordBalanceTypeMap, Date curDate, UniversityDate universityDate, int lineNumber, MessageMap messageMap) {

        try{
            
            final Map<String, Integer> pMap = getCollectorDetailFieldUtil().getFieldBeginningPositionMap();
            
            detailLine = org.apache.commons.lang.StringUtils.rightPad(detailLine, GeneralLedgerConstants.getSpaceAllCollectorDetailFields().length(), ' ');

            setCreateDate(curDate);
            if (!GeneralLedgerConstants.getSpaceUniversityFiscalYear().equals(detailLine.substring(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)))) {
                try {
                    setUniversityFiscalYear(new Integer(getValue(detailLine, pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE))));
                }
                catch (NumberFormatException e) {
                    messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, "Collector detail university fiscal year " + lineNumber + " string " + detailLine.substring(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)));
                    setUniversityFiscalYear(null);
                }
            }
            else {
                setUniversityFiscalYear(null);
            }
            
            if (!GeneralLedgerConstants.getSpaceChartOfAccountsCode().equals(detailLine.substring(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE))))
                setChartOfAccountsCode(getValue(detailLine, pMap.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), pMap.get(KFSPropertyConstants.ACCOUNT_NUMBER)));
            else
                setChartOfAccountsCode(GeneralLedgerConstants.getSpaceChartOfAccountsCode());            
            setAccountNumber(getValue(detailLine, pMap.get(KFSPropertyConstants.ACCOUNT_NUMBER), pMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER)));
            
            // if chart code is empty while accounts cannot cross charts, then derive chart code from account number
            AccountService acctserv = SpringContext.getBean(AccountService.class);
            if (StringUtils.isEmpty(getChartOfAccountsCode()) && StringUtils.isNotEmpty(getAccountNumber()) && !acctserv.accountsCanCrossCharts()) {
                Account account = acctserv.getUniqueAccountForAccountNumber(getAccountNumber());
                if (account != null) {
                    setChartOfAccountsCode(account.getChartOfAccountsCode());
                }            
            }
                        
            setSubAccountNumber(getValue(detailLine, pMap.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE)));
            setFinancialObjectCode(getValue(detailLine, pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE)));
            setFinancialSubObjectCode(getValue(detailLine, pMap.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE)));
            
            // We are in Collector Detail for ID Billing Details because the value from file positions 26, 27 = DT.  We don not want to set Financial Balance Type Code to detail type code (DT)   
            // Generate the account record key to retrieve the balance type from the map which contains the balance type from the accounting record/origin entry
            String accountRecordKey = generateAccountRecordBalanceTypeKey();
            String financialBalanceTypeCode = accountRecordBalanceTypeMap.get(accountRecordKey);
            // Default to "AC" if we do not find account record key record from the map
            setFinancialBalanceTypeCode(StringUtils.defaultIfEmpty(financialBalanceTypeCode, GeneralLedgerConstants.FINALNCIAL_BALANCE_TYPE_FOR_COLLECTOR_DETAIL_RECORD));
            setFinancialObjectTypeCode(getValue(detailLine, pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE), pMap.get(KFSPropertyConstants.COLLECTOR_DETAIL_SEQUENCE_NUMBER)));
            setUniversityFiscalPeriodCode(universityDate.getUniversityFiscalAccountingPeriod());
            setCollectorDetailSequenceNumber(getValue(detailLine, pMap.get(KFSPropertyConstants.COLLECTOR_DETAIL_SEQUENCE_NUMBER), pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE)));
            setFinancialDocumentTypeCode(getValue(detailLine, pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE)));
            setFinancialSystemOriginationCode(getValue(detailLine, pMap.get(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE), pMap.get(KFSPropertyConstants.DOCUMENT_NUMBER)));
            setDocumentNumber(getValue(detailLine, pMap.get(KFSPropertyConstants.DOCUMENT_NUMBER), pMap.get(KFSPropertyConstants.COLLECTOR_DETAIL_AMOUNT)));
            try {
                setCollectorDetailItemAmount(getValue(detailLine, pMap.get(KFSPropertyConstants.COLLECTOR_DETAIL_AMOUNT), pMap.get(KFSPropertyConstants.COLLECTOR_DETAIL_GL_CREDIT_CODE)));
            }
            catch (NumberFormatException e) {
                setCollectorDetailItemAmount(KualiDecimal.ZERO);
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, "Collector detail amount cannot be parsed on line " + lineNumber + " amount string " + detailLine.substring(pMap.get(KFSPropertyConstants.COLLECTOR_DETAIL_AMOUNT), pMap.get(KFSPropertyConstants.GL_CREDIT_CODE)));
            }
            if (KFSConstants.GL_CREDIT_CODE.equalsIgnoreCase(getValue(detailLine, pMap.get(KFSPropertyConstants.COLLECTOR_DETAIL_GL_CREDIT_CODE), pMap.get(KFSPropertyConstants.COLLECTOR_DETAIL_NOTE_TEXT)))) {
                setCollectorDetailItemAmount(getCollectorDetailItemAmount().negated());
            }
            setCollectorDetailNoteText(getValue(detailLine, pMap.get(KFSPropertyConstants.COLLECTOR_DETAIL_NOTE_TEXT), GeneralLedgerConstants.getSpaceAllCollectorDetailFields().length()));
            
            if (org.apache.commons.lang.StringUtils.isEmpty(getSubAccountNumber())) {
                setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            if (org.apache.commons.lang.StringUtils.isEmpty(getFinancialSubObjectCode())) {
                setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            if (org.apache.commons.lang.StringUtils.isEmpty(getCollectorDetailSequenceNumber())) {
                setCollectorDetailSequenceNumber(" ");
            }
        } catch (Exception e){
            throw new RuntimeException(e + " occurred in CollectorDetail.setFromFileForCollectorDetail()");
        }
    }
    
    /**
     * Account record balance type key 
     * Fiscal Year - Chart Code - Account Number - Sub-account Number - Object code - Sub-object Code
     * 
     * For the two optional fields sub-account and sub-object code, create an additional filter to replace
     * the usual place holder - with spaces
     * 
     * NOTE: this should match the same implementation in CollectorFlatFile generateAccountRecordBalanceTypeKey
     * 
     * @return
     */
    private String generateAccountRecordBalanceTypeKey() {
        StringBuilder builder = new StringBuilder();
        builder.append(getUniversityFiscalYear()).append("|")
            .append(getChartOfAccountsCode()).append("|")
            .append(getAccountNumber()).append("|")
            .append(StringUtils.replace(getSubAccountNumber(), "-", "")).append("|")
            .append(getFinancialObjectCode()).append("|")
            .append(StringUtils.replace(getFinancialSubObjectCode(), "-", ""));
        return builder.toString();
    }
}
