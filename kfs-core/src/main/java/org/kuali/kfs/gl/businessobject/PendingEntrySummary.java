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
package org.kuali.kfs.gl.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * An OriginEntryInformation wrapper which helpfully summarizes data for the pending entry report
 */
public class PendingEntrySummary extends TransientBusinessObjectBase {
    private OriginEntryInformation originEntry;
    private boolean suppress;
    
    /**
     * @return the document number of the wrapped entry
     */
    public String getDocumentNumber() {
        return (!suppress) ? getConstantDocumentNumber() : "";
    }
    
    /**
     * @return the document number of the wrapped entry - even if suppressed
     */
    public String getConstantDocumentNumber() {
        return StringUtils.join(new String[] { originEntry.getFinancialSystemOriginationCode(),originEntry.getDocumentNumber()}, '-');
    }
    
    /**
     * @return the document type code of the wrapped entry
     */
    public String getDocumentTypeCode() {
        return (!suppress) ? getConstantDocumentTypeCode() : "";
    }
    
    /**
     * @return the document type code, even if suppressed
     */
    public String getConstantDocumentTypeCode() {
        return originEntry.getFinancialDocumentTypeCode();
    }
    
    /**
     * @return the balance type code of the wrapped entry
     */
    public String getBalanceTypeCode() {
        return (!suppress) ? getConstantBalanceTypeCode() : "";
    }
    
    /**
     * @return the balance type code, even if suppressed
     */
    public String getConstantBalanceTypeCode() {
        return originEntry.getFinancialBalanceTypeCode();
    }
    
    /**
     * @return the chart of accounts code of the wrapped entry
     */
    public String getChartOfAccountsCode() {
        return originEntry.getChartOfAccountsCode();
    }
    
    /**
     * @return the account number of the wrapped entry
     */
    public String getAccountNumber() {
        return originEntry.getAccountNumber();
    }
    
    /**
     * @return the financial object code of the wrapped entry
     */
    public String getFinancialObjectCode() {
        return originEntry.getFinancialObjectCode();
    }
    
    /**
     * @return the amount of the wrapped entry, or null if the entry does not represent a credit
     */
    public KualiDecimal getCreditAmount() {
        if (!StringUtils.isBlank(originEntry.getTransactionDebitCreditCode())) {
            if (originEntry.getTransactionDebitCreditCode().equals(KFSConstants.GL_CREDIT_CODE)) {
                return originEntry.getTransactionLedgerEntryAmount(); 
            }
        }
        return null;
    }

    /**
     * @return the amount of the wrapped entry, or null if the entry does not represent a debit
     */
    public KualiDecimal getDebitAmount() {
        if (!StringUtils.isBlank(originEntry.getTransactionDebitCreditCode())) { 
            if (originEntry.getTransactionDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE)) {
                return originEntry.getTransactionLedgerEntryAmount();
            }
        }
        return null;
    }

    /**
     * @return the amount for the wrapped entry, or null if the entry represents either a debit or a credt
     */
    public KualiDecimal getBudgetAmount() {
        return (originEntry.getTransactionDebitCreditCode() == null || originEntry.getTransactionDebitCreditCode().equals(KFSConstants.GL_BUDGET_CODE) || originEntry.getTransactionDebitCreditCode().equals(KFSConstants.EMPTY_STRING)) ? originEntry.getTransactionLedgerEntryAmount() : null;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> pkHashMap = new LinkedHashMap<String, String>();
        pkHashMap.put("documentTypeCode", this.getDocumentTypeCode());
        pkHashMap.put("documentNumber", this.getDocumentNumber());
        pkHashMap.put("chartOfAccountsCode", this.getChartOfAccountsCode());
        pkHashMap.put("accountNumber", this.getAccountNumber());
        pkHashMap.put("balanceTypeCode", this.getBalanceTypeCode());
        pkHashMap.put("financialObjectCode", this.getFinancialObjectCode());
        return pkHashMap;
    }
    
    /**
     * @param originEntry sets the origin entry
     */
    public void setOriginEntry(OriginEntryInformation originEntry) {
        this.originEntry = originEntry;
        this.suppress = false;
    }

    /**
     * Sets the suppress attribute value.
     * @param suppress The suppress to set.
     */
    public void suppressCommonFields(boolean suppress) {
        this.suppress = suppress;
    }

    /**
     * @return a String representation of suppressable fields
     */
    public String getSuppressableFieldsAsKey() {
        return StringUtils.join(new String[] {originEntry.getFinancialDocumentTypeCode(),originEntry.getFinancialSystemOriginationCode(),originEntry.getDocumentNumber(),originEntry.getFinancialBalanceTypeCode()}, ':');
    }
}
