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
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.PersistableBusinessObjectExtension;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;

public class AssetGlpeSourceDetail extends PersistableBusinessObjectBase implements GeneralLedgerPendingEntrySourceDetail {

    private Account account;
    private String accountNumber;
    private KualiDecimal amount;
    private String balanceTypeCode;
    private String chartOfAccountsCode;
    private String documentNumber;
    private String financialDocumentLineDescription;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private ObjectCode objectCode;
    private String organizationReferenceId;
    private Integer postingYear;
    private String projectCode;
    private String referenceNumber;
    private String referenceOriginCode;
    private String referenceTypeCode;
    private String subAccountNumber;
    private boolean source;


    public Account getAccount() {
        return account;
    }


    public void setAccount(Account account) {
        this.account = account;
    }


    public String getAccountNumber() {
        return accountNumber;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public KualiDecimal getAmount() {
        return amount;
    }


    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }


    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }


    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }


    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    public String getDocumentNumber() {
        return documentNumber;
    }


    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    public String getFinancialDocumentLineDescription() {
        return financialDocumentLineDescription;
    }


    public void setFinancialDocumentLineDescription(String financialDocumentLineDescription) {
        this.financialDocumentLineDescription = financialDocumentLineDescription;
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


    public ObjectCode getObjectCode() {
        return objectCode;
    }


    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }


    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }


    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }


    public Integer getPostingYear() {
        return postingYear;
    }


    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }


    public String getProjectCode() {
        return projectCode;
    }


    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }


    public String getReferenceNumber() {
        return referenceNumber;
    }


    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }


    public String getReferenceOriginCode() {
        return referenceOriginCode;
    }


    public void setReferenceOriginCode(String referenceOriginCode) {
        this.referenceOriginCode = referenceOriginCode;
    }


    public String getReferenceTypeCode() {
        return referenceTypeCode;
    }


    public void setReferenceTypeCode(String referenceTypeCode) {
        this.referenceTypeCode = referenceTypeCode;
    }


    public String getSubAccountNumber() {
        return subAccountNumber;
    }


    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    public boolean isSource() {
        return source;
    }


    public void setSource(boolean source) {
        this.source = source;
    }


    @Override
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("", "");
        return map;
    }

}
