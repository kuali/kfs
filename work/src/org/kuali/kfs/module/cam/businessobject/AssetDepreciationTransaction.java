/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.TransientBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

public class AssetDepreciationTransaction extends TransientBusinessObjectBase implements Cloneable {
    private Long capitalAssetNumber;
    //private String financialSystemOriginationCode;
    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialObjectTypeCode;
    private String transactionType;
    private String projectCode;
    private KualiDecimal transactionAmount;
    private String transactionLedgerEntryDescription;
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransientBusinessObjectBase.class);
    
    public Object clone()  {
        try
        {
            return (AssetDepreciationTransaction)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e.toString());
        }
    }
    
    public AssetDepreciationTransaction() {
        super();
    }
    @Override
    protected LinkedHashMap<String,Object> toStringMapper() {
        LinkedHashMap<String,Object> m = new LinkedHashMap<String,Object>();
        m.put("AssetNumber", this.capitalAssetNumber);
        m.put("accountNumber",this.accountNumber);
        m.put("subAccountNumber",this.subAccountNumber);
        m.put("financialObjectCode",this.financialObjectCode);
        m.put("financialSubObjectCode",this.financialSubObjectCode);
        m.put("financialObjectTypeCode",this.financialObjectTypeCode);
        m.put(" projectCode", this.projectCode);
        m.put("transactionType",this.transactionType);
        m.put("transactionAmount",this.transactionAmount);
        return m;
    }


    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }


    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

/*
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }


    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }
*/

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
/*        return (this.getCapitalAssetNumber().toString()+this.getFinancialSystemOriginationCode()+this.getChartOfAccountsCode()+
                this.getAccountNumber()+this.getSubAccountNumber()+this.getFinancialObjectCode()+this.getFinancialSubObjectCode()+this.getFinancialObjectTypeCode()+
                this.getProjectCode()+this.getTransactionType());*/
        return (this.getCapitalAssetNumber().toString()+this.getChartOfAccountsCode()+
        this.getAccountNumber()+this.getSubAccountNumber()+this.getFinancialObjectCode()+this.getFinancialSubObjectCode()+this.getFinancialObjectTypeCode()+
        this.getProjectCode()+this.getTransactionType());
        
    }

    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }
    
}
