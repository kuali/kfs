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
package org.kuali.kfs.module.cam.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class AssetDepreciationTransaction extends TransientBusinessObjectBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransientBusinessObjectBase.class);

    protected Long capitalAssetNumber;
    protected String documentNumber;
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String subAccountNumber;
    protected String financialObjectCode;
    protected String financialSubObjectCode;
    protected String financialObjectTypeCode;
    protected String transactionType;
    protected String projectCode;
    protected KualiDecimal transactionAmount;
    protected String transactionLedgerEntryDescription;

    
    
        
        
        
        
        
        
        
        
        
        
        
    


    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }


    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }


    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    public String getAccountNumber() {
        return accountNumber;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public String getSubAccountNumber() {
        return subAccountNumber;
    }


    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    public String getFinancialObjectCode() {
        return financialObjectCode;
    }


    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }


    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }


    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }


    public String getTransactionType() {
        return transactionType;
    }


    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }


    public String getProjectCode() {
        return projectCode;
    }


    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }


    public KualiDecimal getTransactionAmount() {
        return transactionAmount;
    }


    public void setTransactionAmount(KualiDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getKey() {
        return (this.getCapitalAssetNumber().toString() + this.getChartOfAccountsCode() + this.getAccountNumber() + this.getSubAccountNumber() + this.getFinancialObjectCode() + this.getFinancialSubObjectCode() + this.getFinancialObjectTypeCode() + this.getProjectCode() + this.getTransactionType());

    }

    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }

}
