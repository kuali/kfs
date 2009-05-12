/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.gl.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * An OriginEntry wrapper which helpfully summarizes data for the pending entry report
 */
public class PendingEntrySummary extends TransientBusinessObjectBase {
    private OriginEntry originEntry;
    private boolean suppress;
    
    /**
     * @return the document number of the wrapped entry
     */
    public String getDocumentNumber() {
        return (!suppress) ? StringUtils.join(new String[] { originEntry.getFinancialSystemOriginationCode(),originEntry.getDocumentNumber()}, '-') : "";
    }
    
    /**
     * @return the document type code of the wrapped entry
     */
    public String getDocumentTypeCode() {
        return (!suppress) ? originEntry.getFinancialDocumentTypeCode() : "";
    }
    
    /**
     * @return the balance type code of the wrapped entry
     */
    public String getBalanceTypeCode() {
        return (!suppress) ? originEntry.getFinancialBalanceTypeCode() : "";
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
        return originEntry.getTransactionDebitCreditCode().equals(KFSConstants.GL_CREDIT_CODE) ? originEntry.getTransactionLedgerEntryAmount() : null; 
    }

    /**
     * @return the amount of the wrapped entry, or null if the entry does not represent a debit
     */
    public KualiDecimal getDebitAmount() {
        return originEntry.getTransactionDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE) ? originEntry.getTransactionLedgerEntryAmount() : null;
    }

    /**
     * @return the amount for the wrapped entry, or null if the entry represents either a debit or a credt
     */
    public KualiDecimal getBudgetAmount() {
        return originEntry.getTransactionDebitCreditCode().equals(KFSConstants.GL_BUDGET_CODE) ? originEntry.getTransactionLedgerEntryAmount() : null;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
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
    public void setOriginEntry(OriginEntry originEntry) {
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
