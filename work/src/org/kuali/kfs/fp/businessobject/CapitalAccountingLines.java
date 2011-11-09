/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This transient business object represents the Capital Accounting Lines
 * business object that is used by the FP documents.
 */
public class CapitalAccountingLines extends TransientBusinessObjectBase {
    protected String documentNumber;
    protected Integer sequenceNumber; // relative to the grouping of accounting lines
    protected String lineType; //tells where source or target line
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String financialObjectCode;
    protected String subAccountNumber;
    protected String financialSubObjectCode;
    protected String projectCode;
    protected String financialDocumentLineDescription;
    protected KualiDecimal amount;
    protected boolean selectLine;
    protected String distributionAmountCode;
    protected boolean amountDistributed;
    
    /**
     * Default constructor.
     */
    public CapitalAccountingLines() {
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
     * Gets the sequenceNumber attribute.
     * 
     * @return Returns the sequenceNumber
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /** 
     * Sets the sequenceNumber attribute.
     * 
     * @param sequenceNumber The sequenceNumber to set.
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Gets the lineType attribute.
     * 
     * @return Returns the lineType
     */
    public String getLineType() {
        return lineType;
    }

    /** 
     * Sets the lineType attribute.
     * 
     * @param lineType The lineType to set.
     */
    public void setLineType(String lineType) {
        this.lineType = lineType;
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
     * Gets the projectCode attribute.
     * 
     * @return Returns the projectCode
     */
    public String getProjectCode() {
        return projectCode;
    }

    /** 
     * Sets the projectCode attribute.
     * 
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * Gets the financialDocumentLineDescription attribute.
     * 
     * @return Returns the financialDocumentLineDescription
     */
    public String getFinancialDocumentLineDescription() {
        return financialDocumentLineDescription;
    }

    /** 
     * Sets the financialDocumentLineDescription attribute.
     * 
     * @param financialDocumentLineDescription The financialDocumentLineDescription to set.
     */
    public void setFinancialDocumentLineDescription(String financialDocumentLineDescription) {
        this.financialDocumentLineDescription = financialDocumentLineDescription;
    }

    /**
     * Gets the amount attribute.
     * 
     * @return Returns the amount
     */
    public KualiDecimal getAmount() {
        return amount;
    }

    /** 
     * Sets the amount attribute.
     * 
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the selectLine attribute.
     * 
     * @return Returns the selectLine
     */
    public boolean isSelectLine() {
        return selectLine;
    }

    /** 
     * Sets the selectLine attribute.
     * 
     * @param selectLine The selectLine to set.
     */
    public void setSelectLine(boolean selectLine) {
        this.selectLine = selectLine;
    }

    /**
     * Gets the distributionAmountCode attribute.
     * 
     * @return Returns the distributionAmountCode
     */
    
    public String getDistributionAmountCode() {
        return distributionAmountCode;
    }

    /** 
     * Sets the distributionAmountCode attribute.
     * 
     * @param distributionAmountCode The distributionAmountCode to set.
     */
    public void setDistributionAmountCode(String distributionAmountCode) {
        this.distributionAmountCode = distributionAmountCode;
    }

    /**
     * Gets the amountDistributed attribute.
     * 
     * @return Returns the amountDistributed
     */
    
    public boolean isAmountDistributed() {
        return amountDistributed;
    }

    /** 
     * Sets the amountDistributed attribute.
     * 
     * @param amountDistributed The amountDistributed to set.
     */
    public void setAmountDistributed(boolean amountDistributed) {
        this.amountDistributed = amountDistributed;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    public LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        m.put("sequenceNumber", this.getSequenceNumber());
        m.put("lineType", this.getLineType());
        m.put("chartOfAccountsCode", this.getChartOfAccountsCode());
        m.put("accountNumber", this.getAccountNumber());
        m.put("financialObjectCode", this.getFinancialObjectCode());
        m.put("subAccountNumber", this.getSubAccountNumber());
        m.put("financialSubObjectCode", this.getFinancialSubObjectCode());
        m.put("projectCode", this.getProjectCode());
        m.put("financialDocumentLineDescription", this.getFinancialDocumentLineDescription());
        m.put("amount", this.getAmount());
        m.put("selectLine", this.isSelectLine());
        m.put("distributionAmountCode", this.getDistributionAmountCode());
        m.put("amountDistributed", this.isAmountDistributed());
        
        return m;
    }
}
